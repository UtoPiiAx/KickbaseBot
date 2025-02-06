package com.kickbasebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.Ranking;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Offer;
import com.kickbasebot.data.market.PlayerFromMarket;
import com.kickbasebot.data.me.Player;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class KickbaseBotImpl extends HttpClientHelper implements KickbaseBot {

    private static final double BID_PERCENTAGE = 0.966;

    private static KickbaseBotImpl instance;

    private String userId;
    private Profile profile;
    private League league;
    private final List<Player> players = new ArrayList<>();
    private final List<PlayerFromMarket> playersOnTM = new ArrayList<>();

    NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    private KickbaseBotImpl() {
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
                System.out.println("Rank " + position + ": " + user.getName() + " - " + user.getScore() + " points");
                position++;
            }
            return ranking;
        } else {
            System.out.println("No ranking data found!");
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

        String formattedTeamValue = numberFormat.format(profile.getTeamValue());
        String formattedProfit = numberFormat.format(profile.getProfit());

        String lastMatchdayPoints = pastMatchdayPoints.stream()
                .limit(5)
                .map(String::valueOf)
                .collect(Collectors.joining("|"));

        System.out.println("\nProfile successfully loaded for: " + profile.getUsername());
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
    public List<PlayerFromMarket> getPlayersFromMarket() throws IOException, InterruptedException {
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

                PlayerFromMarket playerFromMarket = new PlayerFromMarket(
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
                playersOnTM.add(playerFromMarket);
            }
        }

        System.out.println("\nFound " + playersOnTM.size() + " players on the transfer market.\n");
        return playersOnTM;
    }

    @Override
    public void placeBids() throws IOException, InterruptedException {
        playersOnTM.sort(Comparator.comparingInt(PlayerFromMarket::getRemainingSeconds));

        for (PlayerFromMarket playerFromMarketToBid : playersOnTM) {
            double bidAmount = playerFromMarketToBid.getMarketValue() * BID_PERCENTAGE;
            double profit = playerFromMarketToBid.getMarketValue() - bidAmount;

            String bidUrl = BASE_URL + "/leagues/" + league.getId() + "/market/" + playerFromMarketToBid.getId() + "/offers/";

            Map<String, Object> requestBody = Map.of("price", bidAmount);

            sendPostRequest(bidUrl, requestBody);

            System.out.println("\nBid placed for " + playerFromMarketToBid.getFirstName() + " " + playerFromMarketToBid.getLastName() +
                    " in the amount of " + formatNumber(bidAmount) + "." +
                    "\nCurrent market value: " + formatNumber(playerFromMarketToBid.getMarketValue()) +
                    "\nPotential profit if sold immediately: +" + formatNumber(profit) + "\n");
        }
    }

    @Override
    public void getPlayers() throws IOException, InterruptedException {
        String playersUrl = BASE_URL_LEAGUES + league.getId() + "/squad/";

        JsonNode playersData = sendGetRequest(playersUrl);
        JsonNode playerArray = playersData.get("it");

        List<Player> sortedPlayers = new ArrayList<>(); // Neue Liste für die sortierten Spieler

        if (playerArray != null && playerArray.isArray()) {
            for (JsonNode playerData : playerArray) {
                Player player = new Player(
                        playerData.has("mvgl") ? playerData.get("mvgl").asInt() : 0,            // Market value in the league
                        playerData.has("i") ? playerData.get("i").asText() : "",               // Player ID
                        playerData.has("n") ? playerData.get("n").asText() : "Unknown",        // Player name
                        playerData.has("lo") ? playerData.get("lo").asInt() : 0,               // Loan status
                        playerData.has("lst") ? playerData.get("lst").asInt() : 0,              // Last transfer status
                        playerData.has("st") ? playerData.get("st").asInt() : 0,               // Status
                        playerData.has("mdst") ? playerData.get("mdst").asInt() : 0,            // Matchday status
                        playerData.has("pos") ? playerData.get("pos").asInt() : 0,              // Position in the rank
                        playerData.has("mv") ? playerData.get("mv").asLong() : 0L,              // Market value
                        playerData.has("mvt") ? playerData.get("mvt").asInt() : 0,              // Market value type
                        playerData.has("p") ? playerData.get("p").asInt() : 0,                  // Points
                        playerData.has("ap") ? playerData.get("ap").asInt() : 0,                // Average points
                        playerData.has("iotm") && playerData.get("iotm").asBoolean(),           // Is in the team of the moment
                        playerData.has("ofc") ? playerData.get("ofc").asInt() : 0,              // Offensive contribution
                        playerData.has("tid") ? playerData.get("tid").asText() : "",             // Team ID
                        playerData.has("sdmvt") ? playerData.get("sdmvt").asLong() : 0L,         // Market value change in the last 7 days
                        playerData.has("tfhmvt") ? playerData.get("tfhmvt").asLong() : 0L,       // Market value change in the last day
                        playerData.has("pim") ? playerData.get("pim").asText() : ""              // Player image URL
                );

                String percentageChangeLastDay = calculateMarketValueChangePercentage(player.getMarketValue(), player.getMarketValueChangeInLastDay());
                player.setPercentageChangeLastDay(Double.parseDouble(percentageChangeLastDay.replace("%", "")));
                players.add(player);
            }

            sortedPlayers = players.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getPercentageChangeLastDay(), p1.getPercentageChangeLastDay()))
                    .collect(Collectors.toList());
        }

        for (Player player : sortedPlayers) {
            String position = getPositionName(player.getPositionInRank());

            String formattedMarketValue = numberFormat.format(player.getMarketValue());
            String formattedChangeLastWeek = formatMarketValueChange(player.getMarketValueChangeInLastWeek(), player.getMarketValueType());
            String formattedChangeLastDay = formatMarketValueChange(player.getMarketValueChangeInLastDay(), player.getMarketValueType());
            String percentageChangeLastDay = calculateMarketValueChangePercentage(player.getMarketValue(), player.getMarketValueChangeInLastDay());

            System.out.println("\n------------------------");
            System.out.println("Player: " + player.getPlayerName());
            System.out.println("Position: " + position);
            System.out.println("Market Value: " + formattedMarketValue + " EUR");
            System.out.println("Total Points: " + player.getPoints());
            System.out.println("Average Points: " + player.getAveragePoints());
            System.out.println("Market Value Change (Last Week): " + formattedChangeLastWeek);
            System.out.println("Market Value Change (Last Day): " + formattedChangeLastDay);
            System.out.println("Percentage Change (Last Day): " + percentageChangeLastDay);
            System.out.println("------------------------");
        }

        System.out.println("\nA total of " + sortedPlayers.size() + " players were found in your squad.");
    }

    private String getPositionName(int positionInRank) {
        switch (positionInRank) {
            case 1:
                return "Goalkeeper";
            case 2:
                return "Defender";
            case 3:
                return "Midfielder";
            case 4:
                return "Forward";
            default:
                return "Unknown";
        }
    }

    private String formatMarketValueChange(long change, int mvt) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String formattedChange = numberFormat.format(change);

        if (mvt == 1) {
            return "+ " + formattedChange + " EUR";
        } else if (mvt == 0) {
            return "- " + formattedChange + " EUR";
        }
        return formattedChange + " EUR";
    }

    private String calculateMarketValueChangePercentage(long currentMarketValue, long marketValueChange) {
        if (currentMarketValue == 0) {
            return "No market value available";
        }
        double percentageChange = ((double) marketValueChange / currentMarketValue) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        String percentageString = df.format(percentageChange) + "%";

        if (percentageString.contains(",")) {
            percentageString = percentageString.replace(",", ".");
        }

        return percentageString;
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
