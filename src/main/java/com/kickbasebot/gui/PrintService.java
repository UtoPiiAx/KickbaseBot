package com.kickbasebot.gui;

import com.kickbasebot.data.Ranking;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.Budget;
import com.kickbasebot.data.me.PlayerOnSquad;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PrintService {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
    private static final int LABEL_WIDTH = 35;

    private void printAligned(String label, Object value) {
        System.out.printf("%-" + LABEL_WIDTH + "s %s%n", label, value);
    }

    public void printLogIn(String userId) {
        System.out.println("\nSuccessfully logged in with user ID: " + userId + "\n");
    }

    public void printLeague(String leagueName, String id) {
        System.out.println("\nLeague successfully retrieved: " + leagueName + " (ID: " + id + ") \n");
    }

    public void printBudget(Budget budget) {
        System.out.println("\nBudget successfully retrieved:");
        System.out.println("Budget After All Bids & Sales: " + numberFormat.format(budget.getProjectedBudgetAfterSales()) + " EUR");
        System.out.println("Budget After All Bids: " + numberFormat.format(budget.getProjectedBudgetAfterAllActions()) + " EUR");
        System.out.println("Current Budget: " + numberFormat.format(budget.getCurrentBudget()) + " EUR");
    }

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
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-12s | %-16s | %-24s | %-10s | %-12s | %-10s | %-10s |%n",
                "Wins", "Total Pts", "Last 5 Total Pts", "Ø Pts", "Team Value", "Profit", "Transfers");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-12d | %-16d | %-24s | %-10d | %-12s | %-10s | %-10d |%n",
                profile.getMatchdayWins(),
                profile.getTotalPoints(),
                lastMatchdayPoints,
                profile.getAveragePoints(),
                formattedTeamValue,
                formattedProfit,
                profile.getTransfers());
        System.out.println("--------------------------------------------------------------------------------------------------------------------");
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

    public void printPlayerResults(List<PlayerOnSquad> sortedPlayerOnSquad, int totalProfitOfPlayers, Profile profile, Budget budget) {
        System.out.println("\nA total of " + sortedPlayerOnSquad.size() + " players were found in your squad:");

        for (PlayerOnSquad playerOnSquad : sortedPlayerOnSquad) {
            System.out.println("\n-----------------------------------------------------");
            printAligned("ID: ", playerOnSquad.getPlayerId());
            printAligned("Player: ", playerOnSquad.getPlayerName());
            printAligned("Position: ", getPositionName(playerOnSquad.getPositionInRank()));
            printAligned("Total Points: ", playerOnSquad.getPoints());
            printAligned("Average Points: ", playerOnSquad.getAveragePoints());
            printAligned("Market Value: ", numberFormat.format(playerOnSquad.getMarketValue()) + " EUR");
            printAligned("Profit since Purchase: ",numberFormat.format(playerOnSquad.getMarketValueLeague()) + " EUR");
            printAligned("Market Value Change (Last Week): ", formatMarketValueChange(playerOnSquad.getMarketValueChangeInLastWeek(), playerOnSquad.getMarketValueType()));
            printAligned("Market Value Change (Last Day): ", formatMarketValueChange(playerOnSquad.getMarketValueChangeInLastDay(), playerOnSquad.getMarketValueType()));
            printAligned("Percentage Change (Last Day): ",calculateMarketValueChangePercentage(playerOnSquad.getMarketValue(), playerOnSquad.getMarketValueChangeInLastDay()));
            System.out.println("-----------------------------------------------------");
        }

        printAligned("\nProfit of all players in squad: " , formatNumber(totalProfitOfPlayers) + " EUR\n");
        printAligned("Total profit (value above + realized profit): ", formatNumber(totalProfitOfPlayers + profile.getProfit()) + " EUR\n");
        printAligned("Current squad value (after budget): ", formatNumber(profile.getTeamValue() + budget.getCurrentBudget()) + " EUR\n");
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
        if (mvt == 1) {
            return "+ " + numberFormat.format(change) + " EUR";
        } else if (mvt == 0) {
            return "- " + numberFormat.format(change) + " EUR";
        }
        return numberFormat.format(change) + " EUR";
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
