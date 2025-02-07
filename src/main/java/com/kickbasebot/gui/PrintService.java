package com.kickbasebot.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.Ranking;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.PlayerOnSquad;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PrintService {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    public void printRanking(Ranking ranking, List<Ranking.User> users) {
        System.out.println("\nLeaderboard:");
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("Rank %d: %s - %d points%n", i + 1, users.get(i).getName(), users.get(i).getScore());
        }
    }

    public void printProfile(Profile profile, List<Integer> pastMatchdayPoints) {
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
    }

    public void printPlayerSold(PlayerOnSquad player, long salePrice, long profit) {
        System.out.println("\n------------------------------");
        System.out.println("Player sold: " + player.getPlayerName());
        System.out.println("Player ID: " + player.getPlayerId());
        System.out.println("Sale Price: " + formatNumber(salePrice));
        System.out.println("Profit: " + formatNumber(profit));
        System.out.println("------------------------------\n");
    }

    public void printBids(PlayerOnMarket player, double bidAmount, double profit) {
        System.out.printf("%nBid placed for %s %s in the amount of %s.%nCurrent market value: %s%nPotential profit if sold immediately: +%s%n%n",
                player.getFirstName(), player.getLastName(), formatNumber(bidAmount),
                formatNumber(player.getMarketValue()), formatNumber(profit));
    }

    public void printPlayerResults(List<PlayerOnSquad> sortedPlayerOnSquad) {
        System.out.println("\nA total of " + sortedPlayerOnSquad.size() + " players were found in your squad:");

        for (PlayerOnSquad playerOnSquad : sortedPlayerOnSquad) {
            String position = getPositionName(playerOnSquad.getPositionInRank());

            String formattedMarketValue = numberFormat.format(playerOnSquad.getMarketValue());
            String formattedChangeLastWeek = formatMarketValueChange(playerOnSquad.getMarketValueChangeInLastWeek(), playerOnSquad.getMarketValueType());
            String formattedChangeLastDay = formatMarketValueChange(playerOnSquad.getMarketValueChangeInLastDay(), playerOnSquad.getMarketValueType());
            String percentageChangeLastDay = calculateMarketValueChangePercentage(playerOnSquad.getMarketValue(), playerOnSquad.getMarketValueChangeInLastDay());

            System.out.println("\n-----------------------------------------------------");
            System.out.println("ID: " + playerOnSquad.getPlayerId());
            System.out.println("PlayerOnSquad: " + playerOnSquad.getPlayerName());
            System.out.println("Position: " + position);
            System.out.println("Market Value: " + formattedMarketValue + " EUR");
            System.out.println("Total Points: " + playerOnSquad.getPoints());
            System.out.println("Average Points: " + playerOnSquad.getAveragePoints());
            System.out.println("Market Value Change (Last Week): " + formattedChangeLastWeek);
            System.out.println("Market Value Change (Last Day): " + formattedChangeLastDay);
            System.out.println("Percentage Change (Last Day): " + percentageChangeLastDay);
            System.out.println("-----------------------------------------------------");
        }
    }

    public List<Integer> parseLeaguePoints(JsonNode lpNode) {
        List<Integer> leaguePoints = new ArrayList<>();
        if (lpNode != null && lpNode.isArray()) {
            lpNode.forEach(node -> leaguePoints.add(node.asInt()));
        }
        return leaguePoints;
    }

    public String getPositionName(int position) {
        return switch (position) {
            case 1 -> "Goalkeeper";
            case 2 -> "Defender";
            case 3 -> "Midfielder";
            case 4 -> "Forward";
            default -> "Unknown";
        };
    }

    public String formatMarketValueChange(long change, int mvt) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        String formattedChange = numberFormat.format(change);

        if (mvt == 1) {
            return "+ " + formattedChange + " EUR";
        } else if (mvt == 0) {
            return "- " + formattedChange + " EUR";
        }
        return formattedChange + " EUR";
    }

    public String calculateMarketValueChangePercentage(long currentMarketValue, long marketValueChange) {
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
