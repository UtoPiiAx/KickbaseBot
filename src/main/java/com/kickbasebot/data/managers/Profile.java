package com.kickbasebot.data.managers;

import java.util.List;

public class Profile {

    private String userId; // "u"
    private String username; // "unm"
    private int status; // "st"
    private int averagePoints; // "ap"
    private int totalPoints; // "tp"
    private int matchdayWins; // "mdw"
    private int position; // "pl"
    private double teamValue; // "tv"
    private int profit; // "prft"
    private String leagueName; // "lnm"
    private String leagueId; // "li"
    private boolean isAdmin; // "adm"
    private int transfers; // "t"
    private List<Integer> pointsHistory; // "ph"
    private String userImage; // "uim"
    private String leagueImage; // "lim"

    public Profile(String userId, String username, int status, int averagePoints, int totalPoints, int matchdayWins,
                   int position, double teamValue, int profit, String leagueName, String leagueId, boolean isAdmin,
                   int transfers, List<Integer> pointsHistory, String userImage, String leagueImage) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.averagePoints = averagePoints;
        this.totalPoints = totalPoints;
        this.matchdayWins = matchdayWins;
        this.position = position;
        this.teamValue = teamValue;
        this.profit = profit;
        this.leagueName = leagueName;
        this.leagueId = leagueId;
        this.isAdmin = isAdmin;
        this.transfers = transfers;
        this.pointsHistory = pointsHistory;
        this.userImage = userImage;
        this.leagueImage = leagueImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }

    public int getAveragePoints() {
        return averagePoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getMatchdayWins() {
        return matchdayWins;
    }

    public int getPosition() {
        return position;
    }

    public double getTeamValue() {
        return teamValue;
    }

    public int getProfit() {
        return profit;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public int getTransfers() {
        return transfers;
    }

    public List<Integer> getPointsHistory() {
        return pointsHistory;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getLeagueImage() {
        return leagueImage;
    }

    @Override
    public String toString() {
        return "Profil{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", actionPoints=" +  averagePoints +
                ", totalPoints=" + totalPoints +
                ", matchdaysWon=" + matchdayWins +
                ", position=" + position +
                ", teamValue=" + teamValue +
                ", profit=" + profit +
                ", leagueName='" + leagueName + '\'' +
                ", leagueId='" + leagueId + '\'' +
                ", isAdmin=" + isAdmin +
                ", transfers=" + transfers +
                ", pointsHistory=" + pointsHistory +
                ", userImage='" + userImage + '\'' +
                ", leagueImage='" + leagueImage + '\'' +
                '}';
    }
}
