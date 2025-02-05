package com.kickbasebot.data;

import java.util.ArrayList;
import java.util.List;

public class Ranking {

    private String teamName;               // "ti"
    private String platformId;             // "cpi"
    private boolean isShared;              // "ish"
    private List<User> users;              // "us"
    private int day;                       // "day"
    private int sharedMatchdayNumber;      // "shmdn"
    private String season;                 // "sn"
    private boolean isLeagueFinished;      // "il"
    private int numberOfDays;              // "nd"
    private int lastFinishedMatchday;      // "lfmd"
    private boolean isActive;              // "ia"

    public Ranking(String teamName, String platformId, boolean isShared, List<User> users,
                   int day, int sharedMatchdayNumber, String season, boolean isLeagueFinished,
                   int numberOfDays, int lastFinishedMatchday, boolean isActive) {
        this.teamName = teamName;
        this.platformId = platformId;
        this.isShared = isShared;
        this.users = users;
        this.day = day;
        this.sharedMatchdayNumber = sharedMatchdayNumber;
        this.season = season;
        this.isLeagueFinished = isLeagueFinished;
        this.numberOfDays = numberOfDays;
        this.lastFinishedMatchday = lastFinishedMatchday;
        this.isActive = isActive;
    }

    public String getTeamName() { return teamName; }
    public String getPlatformId() { return platformId; }
    public boolean isShared() { return isShared; }
    public List<User> getUsers() { return users; }
    public int getDay() { return day; }
    public int getSharedMatchdayNumber() { return sharedMatchdayNumber; }
    public String getSeason() { return season; }
    public boolean isLeagueFinished() { return isLeagueFinished; }
    public int getNumberOfDays() { return numberOfDays; }
    public int getLastFinishedMatchday() { return lastFinishedMatchday; }
    public boolean isActive() { return isActive; }

    public static class User {
        private String id;                     // "i"
        private String name;                   // "n"
        private boolean isAdmin;               // "adm"
        private int score;                     // "sp"
        private int matchdayPoints;            // "mdp"
        private int shopPoints;                // "shp"
        private double totalValue;             // "tv"
        private int specialPoints;             // "spl"
        private int matchdaySpecialPoints;     // "mdpl"
        private int shopSpecialPoints;         // "shpl"
        private boolean isActivePlayer;        // "pa"
        private List<Integer> leaguePoints;    // "lp"
        private String userImageUrl;           // "uim" (optional)

        public User(String id, String name, boolean isAdmin, int score, int matchdayPoints,
                    int shopPoints, double totalValue, int specialPoints, int matchdaySpecialPoints,
                    int shopSpecialPoints, boolean isActivePlayer, List<Integer> leaguePoints, String userImageUrl) {
            this.id = id != null ? id : "";
            this.name = name != null ? name : "";
            this.isAdmin = isAdmin;
            this.score = score;
            this.matchdayPoints = matchdayPoints;
            this.shopPoints = shopPoints;
            this.totalValue = totalValue;
            this.specialPoints = specialPoints;
            this.matchdaySpecialPoints = matchdaySpecialPoints;
            this.shopSpecialPoints = shopSpecialPoints;
            this.isActivePlayer = isActivePlayer;
            this.leaguePoints = leaguePoints != null ? leaguePoints : new ArrayList<>();
            this.userImageUrl = userImageUrl;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public boolean isAdmin() { return isAdmin; }
        public int getScore() { return score; }
        public int getMatchdayPoints() { return matchdayPoints; }
        public int getShopPoints() { return shopPoints; }
        public double getTotalValue() { return totalValue; }
        public int getSpecialPoints() { return specialPoints; }
        public int getMatchdaySpecialPoints() { return matchdaySpecialPoints; }
        public int getShopSpecialPoints() { return shopSpecialPoints; }
        public boolean isActivePlayer() { return isActivePlayer; }
        public List<Integer> getLeaguePoints() { return leaguePoints; }
        public String getUserImageUrl() { return userImageUrl; }
    }

}
