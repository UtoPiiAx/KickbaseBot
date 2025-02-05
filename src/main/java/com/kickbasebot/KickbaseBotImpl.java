package com.kickbasebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Offer;
import com.kickbasebot.data.market.Player;
import com.kickbasebot.data.Ranking;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class KickbaseBotImpl extends HttpClientHelper implements KickbaseBot {

    private static final double BID_PERCENTAGE = 0.966;

    private static KickbaseBotImpl instance;

    private Profile profile;
    private String userId;
    private League league;
    private final List<Player> playersOnTM = new ArrayList<>();

    private KickbaseBotImpl() {}

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

        System.out.println("\nLogin erfolgreich!");
        return token;
    }

    @Override
    public League getLeague() throws IOException, InterruptedException {
        String leaguesUrl = BASE_URL_LEAGUES + "selection";
        JsonNode leagues = sendGetRequest(leaguesUrl);
        JsonNode leagueArray = leagues.get("it");

        if (leagueArray != null && leagueArray.isArray() && leagueArray.size() > 0) {
            JsonNode leagueData = leagueArray.get(0);

            League league = new League(
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
            this.league = league;
            System.out.println("\nLiga erfolgreich abgerufen: " + league.getName() + " (ID: " + league.getId() + ") \n");
            return league;
        }

        throw new IllegalStateException("Keine Ligen gefunden.");
    }

    @Override
    public Ranking fetchAndPopulateRanking() throws IOException, InterruptedException {
        String rankingUrl = BASE_URL_LEAGUES + league.getId() + "/ranking?dayNumber=999";
        JsonNode rankingData = sendGetRequest(rankingUrl);

        JsonNode dataNode = rankingData.get("us");

        if (dataNode != null && dataNode.isArray()) {
            List<Ranking.User> users = new ArrayList<>();

            for (JsonNode userNode : dataNode) {
                List<Integer> leaguePoints = parseLeaguePoints(userNode.get("lp"));

                Ranking.User user = new Ranking.User(
                        userNode.has("i") ? userNode.get("i").asText() : "",   // Benutzer-ID
                        userNode.has("n") ? userNode.get("n").asText() : "",   // Benutzername
                        userNode.has("adm") && userNode.get("adm").asBoolean(), // Admin-Status
                        userNode.has("sp") ? userNode.get("sp").asInt() : 0,   // Punkte
                        userNode.has("mdpl") ? userNode.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                        userNode.has("shp") ? userNode.get("shp").asInt() : 0,  // Shop-Punkte
                        userNode.has("tv") ? userNode.get("tv").asDouble() : 0.0, // Gesamtwert
                        userNode.has("spl") ? userNode.get("spl").asInt() : 0,  // Sonderpunkte
                        userNode.has("mdpl") ? userNode.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                        userNode.has("shpl") ? userNode.get("shpl").asInt() : 0, // Shop-Sonderpunkte
                        userNode.has("pa") && userNode.get("pa").asBoolean(), // Aktiv-Status
                        leaguePoints,  // League-Punkte
                        userNode.has("uim") ? userNode.get("uim").asText() : null // Profilbild-URL
                );

                users.add(user);
            }

            users.sort(Comparator.comparingInt(Ranking.User::getScore).reversed());

            Ranking ranking = new Ranking(
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

            System.out.println("Leaderboard:");
            int position = 1;
            for (Ranking.User user : users) {
                System.out.println("Platz " + position + ": " + user.getName() + " - " + user.getScore() + " Punkte");
                position++;
            }
            return ranking;
        } else {
            System.out.println("Keine Ranking-Daten gefunden!");
            return null;
        }
    }

    private List<Integer> parseLeaguePoints(JsonNode lpNode) {
        List<Integer> leaguePoints = new ArrayList<>();
        if (lpNode != null && lpNode.isArray()) {
            for (JsonNode pointNode : lpNode) {
                leaguePoints.add(pointNode.asInt());
            }
        }
        return leaguePoints;
    }

    @Override
    public Profile getProfile() throws IOException, InterruptedException {
        String profileUrl = BASE_URL_LEAGUES + league.getId() + "/managers/" + userId + "/dashboard";
        JsonNode profileData = sendGetRequest(profileUrl);

        if (profileData == null) {
            throw new IOException("Fehler beim Abrufen der Profildaten.");
        }

        List<Integer> pastMatchdayPoints = new ArrayList<>();
        if (profileData.has("ph") && profileData.get("ph").isArray()) {
            for (JsonNode point : profileData.get("ph")) {
                pastMatchdayPoints.add(point.isNull() ? 0 : point.asInt());
            }
        }

        this.profile = new Profile(
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
                profileData.get("t").asInt(),              // Anzahl der Transfers
                pastMatchdayPoints,                        // Punkte aus den letzten Spieltagen
                profileData.get("uim").asText(),           // User-Profilbild
                profileData.get("lim").asText()            // Liga-Bild
        );

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String formattedTeamValue = numberFormat.format(profile.getTeamValue());
        String formattedProfit = numberFormat.format(profile.getProfit());

        String lastMatchdayPoints = pastMatchdayPoints.stream()
                .limit(5)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        System.out.println("\nProfil erfolgreich geladen für: " + profile.getUsername());
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-12s | %-16s | %-20s | %-10s | %-12s | %-10s | %-10s |\n",
                "Wins", "Total Pts", "Last 5 Total Pts", "Ø Pts", "Team Value", "Profit", "Transfers");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-12d | %-16d | %-20s | %-10d | %-12s | %-10s | %-10d |\n",
                profile.getMatchdayWins(),
                profile.getTotalPoints(),
                lastMatchdayPoints,
                profile.getAveragePoints(),
                formattedTeamValue,
                formattedProfit,
                profile.getTransfers());
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        return profile;
    }

    @Override
    public List<Player> getPlayers() throws IOException, InterruptedException {
        if (league.getId() == null) throw new IllegalStateException("League ID nicht gesetzt.");

        String playersUrl = BASE_URL_LEAGUES + league.getId() + "/market";
        JsonNode playersData = sendGetRequest(playersUrl);
        JsonNode playerArray = playersData.get("it");

        if (playerArray != null && playerArray.isArray()) {
            for (JsonNode playerData : playerArray) {

                List<Offer> offers = new ArrayList<>();
                JsonNode offersNode = playerData.get("ofs");
                if (offersNode != null && offersNode.isArray()) {
                    for (JsonNode offerData : offersNode) {
                        Offer offer = new Offer(
                                offerData.has("u") ? offerData.get("u").asText() : "",             // Benutzer-ID
                                offerData.has("unm") ? offerData.get("unm").asText() : "",           // Benutzername
                                offerData.has("uoid") ? offerData.get("uoid").asText() : "",         // Benutzerangebots-ID
                                offerData.has("uop") ? offerData.get("uop").asLong() : 0L,           // Benutzerangebotspreis
                                offerData.has("st") ? offerData.get("st").asInt() : 0,               // Status (Standardwert 0)
                                offerData.has("uim") ? offerData.get("uim").asText() : ""            // Benutzerbild
                        );
                        offers.add(offer);
                    }
                }

                Player player = new Player(
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
                        offers                                                                          // Liste von Angeboten
                );
                playersOnTM.add(player);
            }
        }

        System.out.println("\nEs wurden " + playersOnTM.size() + " Spielern auf dem Transfermarkt gefunden.\n");
        return playersOnTM;
    }

    @Override
    public void placeBids() throws IOException, InterruptedException {
        playersOnTM.sort(Comparator.comparingInt(Player::getRemainingSeconds));

        for (Player playerToBid : playersOnTM) {
            double bidAmount = playerToBid.getMarketValue() * BID_PERCENTAGE;
            double profit= playerToBid.getMarketValue() - bidAmount;

            String bidUrl = BASE_URL + "/leagues/" + league.getId() + "/market/" + playerToBid.getId() + "/offers/";

            Map<String, Object> requestBody = Map.of("price", bidAmount);

            sendPostRequest(bidUrl, requestBody);

            System.out.println("\nGebot für " + playerToBid.getFirstName() + " " + playerToBid.getLastName() +
                    " in Höhe von " + formatNumber(bidAmount) + " abgegeben." +
                    "\nAktueller Marktwert: " + formatNumber(playerToBid.getMarketValue()) +
                    "\nTransfergewinn bei sofortigem Verkauf: +" + formatNumber(profit) + "\n");
        }
    }

    @Override
    public String formatNumber(double number) {
        if (number >= 1_000_000) {
            return String.format("%.2f Mio", number / 1_000_000);
        } else if (number >= 1_000) {
            return String.format("%.1f Tsd", number / 1_000);
        } else {
            return String.format("%.0f", number);
        }
    }
}
