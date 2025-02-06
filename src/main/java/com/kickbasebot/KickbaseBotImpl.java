package com.kickbasebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.Ranking;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Offer;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.PlayerOnSquad;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KickbaseBotImpl extends HttpClientHelper implements KickbaseBot {

    private static final double BID_PERCENTAGE = 0.97;

    private static KickbaseBotImpl instance;
    private PrintUtils printUtils;

    private String userId;
    private Profile profile;
    private League league;
    private List<PlayerOnSquad> playerOnSquad;
    private List<PlayerOnSquad> sortedPlayerOnSquad;
    private List<PlayerOnMarket> playersOnTM;

    private KickbaseBotImpl() {
        this.printUtils = new PrintUtils();
    }

    public static synchronized KickbaseBotImpl getInstance() {
        if (instance == null) {
            instance = new KickbaseBotImpl();
        }
        return instance;
    }

    @Override
    public String login(String email, String password) throws IOException, InterruptedException {
        String loginUrl = BASE_URL + "/user/login";
        Map<String, Object> requestBody = Map.of(
                "em", email,
                "pass", password,
                "loy", false,
                "rep", Map.of()
        );

        JsonNode responseJson = sendPostRequest(loginUrl, requestBody);
        this.token = responseJson.get("tkn").asText();

        JsonNode userNode = responseJson.get("u");
        this.userId = userNode.get("id").asText();

        System.out.println("\nLogin successful! User ID: " + userId + "\n");
        return token;
    }

    @Override
    public League getLeague() throws IOException, InterruptedException {
        String leaguesUrl = BASE_URL_LEAGUES + "selection";

        JsonNode leagues = sendGetRequest(leaguesUrl);
        JsonNode leagueArray = leagues.get("it");

        if (leagueArray != null) {
            JsonNode leagueData = leagueArray.get(0);
            this.league = createLeague(leagueData);
            System.out.println("\nLeague successfully retrieved: " + league.getName() + " (ID: " + league.getId() + ") \n");
            return league;
        }
        throw new IllegalStateException("No leagues found.");
    }

    @Override
    public Ranking fetchAndPopulateRanking() throws IOException, InterruptedException {
        String rankingUrl = BASE_URL_LEAGUES + league.getId() + "/ranking?dayNumber=999";

        JsonNode rankingData = sendGetRequest(rankingUrl);
        JsonNode dataNode = rankingData.get("us");

        if (dataNode != null) {
            List<Ranking.User> users = new ArrayList<>();

            for (JsonNode userData : dataNode) {
                List<Integer> leaguePoints = printUtils.parseLeaguePoints(userData.get("lp"));
                Ranking.User user = createRankingUser(userData, leaguePoints);
                users.add(user);
            }

            users.sort(Comparator.comparingInt(Ranking.User::getScore).reversed());

            Ranking ranking = createRanking(rankingData, users);
            printUtils.printRanking(ranking, users);
            return ranking;
        }
        throw new IllegalStateException("No ranking data found.");
    }

    @Override
    public Profile getProfile() throws IOException, InterruptedException {
        String profileUrl = BASE_URL_LEAGUES + league.getId() + "/managers/" + userId + "/dashboard";

        JsonNode profileData = sendGetRequest(profileUrl);

        if (profileData != null) {
            List<Integer> pastMatchdayPoints = new ArrayList<>();
            if (profileData.has("ph") && profileData.get("ph").isArray()) {
                for (JsonNode point : profileData.get("ph")) {
                    pastMatchdayPoints.add(point.isNull() ? 0 : point.asInt());
                }
            }

            this.profile = createProfile(profileData, pastMatchdayPoints);

            printUtils.printProfile(profile, pastMatchdayPoints);
            return profile;
        }
        throw new IllegalStateException("No profile data found.");
    }

    @Override
    public List<PlayerOnMarket> getPlayersFromMarket() throws IOException, InterruptedException {
        String playersUrl = BASE_URL_LEAGUES + league.getId() + "/market";

        JsonNode playersData = sendGetRequest(playersUrl);
        JsonNode playerArray = playersData.get("it");

        this.playersOnTM = new ArrayList<>();

        if (playerArray != null) {
            for (JsonNode playerData : playerArray) {

                List<Offer> offers = new ArrayList<>();
                JsonNode offersNode = playerData.get("ofs");
                if (offersNode != null && offersNode.isArray()) {
                    for (JsonNode offerData : offersNode) {
                        Offer offer = createOffer(offerData, offers);
                        offers.add(offer);
                    }
                }

                PlayerOnMarket playerOnMarket = createPlayersFromMarket(playerData, offers);
                playersOnTM.add(playerOnMarket);
            }

            System.out.println("\nFound " + playersOnTM.size() + " players on the transfer market.\n");
            return playersOnTM;
        }
        throw new IllegalStateException("No players found on the transfer market.");
    }

    @Override
    public void placeBids() throws IOException, InterruptedException {
        playersOnTM.sort(Comparator.comparingInt(PlayerOnMarket::getRemainingSeconds));

        for (PlayerOnMarket playerOnMarketToBid : playersOnTM) {
            double bidAmount = playerOnMarketToBid.getMarketValue() * BID_PERCENTAGE;
            double profit = playerOnMarketToBid.getMarketValue() - bidAmount;

            String bidUrl = BASE_URL + "/leagues/" + league.getId() + "/market/" + playerOnMarketToBid.getId() + "/offers/";

            Map<String, Object> requestBody = Map.of("price", bidAmount);

            sendPostRequest(bidUrl, requestBody);

            printUtils.printBids(playerOnMarketToBid, bidAmount, profit);
        }
    }

    @Override
    public List<PlayerOnSquad> getPlayers() throws IOException, InterruptedException {
        String playersUrl = BASE_URL_LEAGUES + league.getId() + "/squad/";

        JsonNode playersData = sendGetRequest(playersUrl);
        JsonNode playerArray = playersData.get("it");

        this.playerOnSquad = new ArrayList<>();
        this.sortedPlayerOnSquad = new ArrayList<>();

        if (playerArray != null && playerArray.isArray()) {
            for (JsonNode playerData : playerArray) {
                PlayerOnSquad player = createPlayerOnSquad(playerData);
                String percentageChangeLastDay = printUtils.calculateMarketValueChangePercentage(player.getMarketValue(), player.getMarketValueChangeInLastDay());
                player.setPercentageChangeLastDay(Double.parseDouble(percentageChangeLastDay.replace("%", "")));
                playerOnSquad.add(player);
            }

            sortedPlayerOnSquad = playerOnSquad.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getPercentageChangeLastDay(), p1.getPercentageChangeLastDay()))
                    .collect(Collectors.toList());
        }

        printUtils.printPlayerResults(sortedPlayerOnSquad);
        return sortedPlayerOnSquad;
    }

    /**
     * ---------------------------------------Create Data from JSON NODE------------------------------------------------
     */

    private Ranking.User createRankingUser(JsonNode userData, List<Integer> leaguePoints) {
        return new Ranking.User(
                userData.has("i") ? userData.get("i").asText() : "",   // Benutzer-ID
                userData.has("n") ? userData.get("n").asText() : "",   // Benutzername
                userData.has("adm") && userData.get("adm").asBoolean(), // Admin-Status
                userData.has("sp") ? userData.get("sp").asInt() : 0,   // Punkte
                userData.has("mdpl") ? userData.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                userData.has("shp") ? userData.get("shp").asInt() : 0,  // Shop-Punkte
                userData.has("tv") ? userData.get("tv").asDouble() : 0.0, // Gesamtwert
                userData.has("spl") ? userData.get("spl").asInt() : 0,  // Sonderpunkte
                userData.has("mdpl") ? userData.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                userData.has("shpl") ? userData.get("shpl").asInt() : 0, // Shop-Sonderpunkte
                userData.has("pa") && userData.get("pa").asBoolean(), // Aktiv-Status
                leaguePoints,  // League-Punkte
                userData.has("uim") ? userData.get("uim").asText() : null // Profilbild-URL
        );
    }

    private Ranking createRanking(JsonNode rankingData, List<Ranking.User> users) {
        return new Ranking(
                rankingData.has("ti") ? rankingData.get("ti").asText() : "", // Teamname
                rankingData.has("cpi") ? rankingData.get("cpi").asText() : "", // Plattform-ID
                rankingData.has("ish") && rankingData.get("ish").asBoolean(), // Shared-Status
                users,
                rankingData.has("day") ? rankingData.get("day").asInt() : 0, // Aktueller Tag
                rankingData.has("shmdn") ? rankingData.get("shmdn").asInt() : 0, // Shared Matchday Number
                rankingData.has("sn") ? rankingData.get("sn").asText() : "", // Saison, Default: ""
                rankingData.has("il") && rankingData.get("il").asBoolean(), // Liga abgeschlossen
                rankingData.has("nd") ? rankingData.get("nd").asInt() : 0, // Anzahl der Tage
                rankingData.has("lfmd") ? rankingData.get("lfmd").asInt() : 0, // Letzter abgeschlossener Matchday
                rankingData.has("ia") && rankingData.get("ia").asBoolean() // Aktiver Status
        );
    }

    private League createLeague(JsonNode leagueData) {
        return new League(
                leagueData.get("i").asText(),              // League ID
                leagueData.get("n").asText(),              // Name
                leagueData.get("cpi").asText(),            // Plattform-ID
                leagueData.get("b").asLong(),              // Balance
                leagueData.get("un").asInt(),              // Benutzer-ID
                leagueData.get("f").asText(),              // Logo-URL
                leagueData.get("lpc").asInt(),             // Punkte
                leagueData.get("bs").asInt(),              // Spielerstand
                leagueData.get("vr").asInt(),              // Verfügbarkeit
                leagueData.get("adm").asBoolean(),         // Admin-Status
                leagueData.get("pl").asInt(),              // Position
                leagueData.get("tv").asLong(),             // Gesamtwert
                leagueData.get("idf").asBoolean(),         // IDF-Flag
                leagueData.get("lim").asText(),            // Limit-URL
                leagueData.get("cpim").asText()            // Profilbild-URL
        );
    }

    private Profile createProfile(JsonNode profileData, List<Integer> pastMatchdayPoints) {
        return new Profile(
                profileData.get("u").asText(),             // Benutzer-ID
                profileData.get("unm").asText(),           // Benutzername
                profileData.get("st").asInt(),             // Status
                profileData.get("ap").asInt(),             // Durchschnittliche Punkte
                profileData.get("tp").asInt(),             // Gesamtpunkte
                profileData.get("mdw").asInt(),            // Anzahl der gewonnenen Spieltage
                profileData.get("pl").asInt(),             // Platzierung in der Liga
                profileData.get("tv").asDouble(),          // Teamwert
                profileData.get("prft").asInt(),           // Gesamtprofit
                profileData.get("lnm").asText(),           // Ligabezeichnung
                profileData.get("li").asText(),            // Liga-ID
                profileData.get("adm").asBoolean(),        // Admin-Status
                profileData.get("t").asInt(),              // Transfers
                pastMatchdayPoints,                        // Punkte aus den letzten Spieltagen
                profileData.get("uim").asText(),           // User-Profilbild
                profileData.get("lim").asText()            // Liga-Bild
        );
    }

    private Offer createOffer(JsonNode offerData, List<Offer> offers) {
        return new Offer(
                offerData.has("u") ? offerData.get("u").asText() : "",               // Benutzer-ID
                offerData.has("unm") ? offerData.get("unm").asText() : "",           // Benutzername
                offerData.has("uoid") ? offerData.get("uoid").asText() : "",         // Benutzerangebots-ID
                offerData.has("uop") ? offerData.get("uop").asLong() : 0L,           // Benutzerangebotspreis
                offerData.has("st") ? offerData.get("st").asInt() : 0,               // Status (Standardwert 0)
                offerData.has("uim") ? offerData.get("uim").asText() : ""            // Benutzerbild
        );
    }

    private PlayerOnMarket createPlayersFromMarket(JsonNode playerData, List<Offer> offers) {
        return new PlayerOnMarket(
                playerData.has("i") ? playerData.get("i").asText() : "",              // ID
                playerData.has("fn") ? playerData.get("fn").asText() : "",            // Vorname
                playerData.has("n") ? playerData.get("n").asText() : "",              // Nachname
                playerData.has("tid") ? playerData.get("tid").asText() : "",          // Team-ID
                playerData.has("pos") ? playerData.get("pos").asInt() : 0,            // Position
                playerData.has("st") ? playerData.get("st").asInt() : 0,              // Status
                playerData.has("mvt") ? playerData.get("mvt").asInt() : 0,            // Marktwert-Trend
                playerData.has("mv") ? playerData.get("mv").asLong() : 0L,            // Marktwert
                playerData.has("p") ? playerData.get("p").asInt() : 0,                // Punkte
                playerData.has("ap") ? playerData.get("ap").asInt() : 0,              // Durchschnittliche Punkte
                playerData.has("ofc") ? playerData.get("ofc").asInt() : 0,            // Anzahl der Angebote
                playerData.has("exs") ? playerData.get("exs").asInt() : 0,            // Verbleibende Sekunden
                playerData.has("prc") ? playerData.get("prc").asLong() : 0L,          // Preis
                playerData.has("uop") ? playerData.get("uop").asLong() : 0L,          // Benutzerangebotspreis
                playerData.has("uoid") ? playerData.get("uoid").asText() : "",        // Benutzerangebots-ID
                playerData.has("isn") && playerData.get("isn").asBoolean(),           // Neu
                playerData.has("iposl") && playerData.get("iposl").asBoolean(),       // Position gesperrt
                playerData.has("dt") ? playerData.get("dt").asText() : "",            // Datum
                playerData.has("pim") ? playerData.get("pim").asText() : "",          // Spielerbild
                offers                                                                // Liste von Angeboten
        );
    }

    private PlayerOnSquad createPlayerOnSquad(JsonNode playerData) {
        return new PlayerOnSquad(
                playerData.has("mvgl") ? playerData.get("mvgl").asInt() : 0,            // Market value in the league
                playerData.has("i") ? playerData.get("i").asText() : "",                // PlayerOnSquad ID
                playerData.has("n") ? playerData.get("n").asText() : "Unknown",         // PlayerOnSquad name
                playerData.has("lo") ? playerData.get("lo").asInt() : 0,                // Loan status
                playerData.has("lst") ? playerData.get("lst").asInt() : 0,              // Last transfer status
                playerData.has("st") ? playerData.get("st").asInt() : 0,                // Status
                playerData.has("mdst") ? playerData.get("mdst").asInt() : 0,            // Matchday status
                playerData.has("pos") ? playerData.get("pos").asInt() : 0,              // Position in the rank
                playerData.has("mv") ? playerData.get("mv").asLong() : 0L,              // Market value
                playerData.has("mvt") ? playerData.get("mvt").asInt() : 0,              // Market value type
                playerData.has("p") ? playerData.get("p").asInt() : 0,                  // Points
                playerData.has("ap") ? playerData.get("ap").asInt() : 0,                // Average points
                playerData.has("iotm") && playerData.get("iotm").asBoolean(),           // Is in the team of the moment
                playerData.has("ofc") ? playerData.get("ofc").asInt() : 0,              // Offensive contribution
                playerData.has("tid") ? playerData.get("tid").asText() : "",            // Team ID
                playerData.has("sdmvt") ? playerData.get("sdmvt").asLong() : 0L,        // Market value change in the last 7 days
                playerData.has("tfhmvt") ? playerData.get("tfhmvt").asLong() : 0L,      // Market value change in the last day
                playerData.has("pim") ? playerData.get("pim").asText() : ""             // PlayerOnSquad image URL
        );
    }

}
