package com.kickbasebot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.DataCreationService;
import com.kickbasebot.data.Ranking;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Offer;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.PlayerOnSquad;
import com.kickbasebot.gui.PrintService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KickbaseBotImpl extends HttpClientHelper implements KickbaseBot {

    private static final double BID_PERCENTAGE = 0.97;

    private static KickbaseBotImpl instance;
    private final PrintService printService;
    private final DataCreationService dataCreationService;

    private String userId;
    private Profile profile;
    private League league;
    private List<PlayerOnSquad> playerOnSquad;
    private List<PlayerOnMarket> playersOnTM;

    private KickbaseBotImpl() {
        this.printService = new PrintService();
        this.dataCreationService = new DataCreationService();
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
            this.league = dataCreationService.createLeague(leagueData);
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
                List<Integer> leaguePoints = parseLeaguePoints(userData.get("lp"));
                Ranking.User user = dataCreationService.createRankingUser(userData, leaguePoints);
                users.add(user);
            }

            users.sort(Comparator.comparingInt(Ranking.User::getScore).reversed());

            Ranking ranking = dataCreationService.createRanking(rankingData, users);
            printService.printRanking(ranking, users);
            return ranking;
        }
        throw new IllegalStateException("No ranking data found.");
    }

    private List<Integer> parseLeaguePoints(JsonNode lpNode) {
        List<Integer> leaguePoints = new ArrayList<>();
        if (lpNode != null && lpNode.isArray()) {
            lpNode.forEach(node -> leaguePoints.add(node.asInt()));
        }
        return leaguePoints;
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

            this.profile = dataCreationService.createProfile(profileData, pastMatchdayPoints);

            printService.printProfile(profile, pastMatchdayPoints);
            return profile;
        }
        throw new IllegalStateException("No profile data found.");
    }

    @Override
    public void sellPlayer(String playerId) throws IOException, InterruptedException {
        String sellUrl = BASE_URL_LEAGUES + league.getId() + "/market/" + playerId + "/sell";

        Optional<PlayerOnSquad> playerToSell = playerOnSquad.stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst();

        if (playerToSell.isPresent()) {
            PlayerOnSquad player = playerToSell.get();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Are you sure you want to sell player " + player.getPlayerName() + " (ID: " + playerId + ")?");
            System.out.print("Type 'yes' to confirm the sale: ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if ("yes".equals(confirmation)) {
                sendPostRequest(sellUrl, null);
                printService.printPlayerSold(player, player.getMarketValue(), player.getMarketValueLeague());
            } else {
                System.out.println("\nPlayer " + player.getPlayerName() + " (ID: " + playerId + ") was not sold.\n");
            }
        } else {
            System.out.println("\nPlayer with ID " + playerId + " not found in your squad.\n");
        }
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
                        Offer offer = dataCreationService.createOffer(offerData, offers);
                        offers.add(offer);
                    }
                }

                PlayerOnMarket playerOnMarket = dataCreationService.createPlayersFromMarket(playerData, offers);
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

            printService.printBids(playerOnMarketToBid, bidAmount, profit);
        }
    }

    @Override
    public List<PlayerOnSquad> getPlayers() throws IOException, InterruptedException {
        String playersUrl = BASE_URL_LEAGUES + league.getId() + "/squad/";

        JsonNode playersData = sendGetRequest(playersUrl);
        JsonNode playerArray = playersData.get("it");

        this.playerOnSquad = new ArrayList<>();
        List<PlayerOnSquad> sortedPlayerOnSquad = new ArrayList<>();

        if (playerArray != null && playerArray.isArray()) {
            for (JsonNode playerData : playerArray) {
                PlayerOnSquad player = dataCreationService.createPlayerOnSquad(playerData);
                String percentageChangeLastDay = printService.calculateMarketValueChangePercentage(player.getMarketValue(), player.getMarketValueChangeInLastDay());
                player.setPercentageChangeLastDay(Double.parseDouble(percentageChangeLastDay.replace("%", "")));
                playerOnSquad.add(player);
            }

            sortedPlayerOnSquad = playerOnSquad.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getPercentageChangeLastDay(), p1.getPercentageChangeLastDay()))
                    .collect(Collectors.toList());
        }

        printService.printPlayerResults(sortedPlayerOnSquad);
        return sortedPlayerOnSquad;
    }

}
