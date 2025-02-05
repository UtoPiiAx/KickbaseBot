package com.kickbasebot.data.market;

public class League {

    private String id;                  // "i"
    private String name;                // "n"
    private String platformId;          // "cpi"
    private long balance;               // "b"
    private int userId;                 // "un"
    private String logoUrl;             // "f"
    private int points;                 // "lpc"
    private int playerStatus;           // "bs"
    private int availability;           // "vr"
    private boolean isAdmin;            // "adm"
    private int position;               // "pl"
    private long totalValue;            // "tv"
    private boolean isIdf;              // "idf"
    private String limitUrl;            // "lim"
    private String profileImageUrl;     // "cpim"

    public League(String id, String name, String platformId, long balance, int userId, String logoUrl,
                  int points, int playerStatus, int availability, boolean isAdmin, int position,
                  long totalValue, boolean isIdf, String limitUrl, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.platformId = platformId;
        this.balance = balance;
        this.userId = userId;
        this.logoUrl = logoUrl;
        this.points = points;
        this.playerStatus = playerStatus;
        this.availability = availability;
        this.isAdmin = isAdmin;
        this.position = position;
        this.totalValue = totalValue;
        this.isIdf = isIdf;
        this.limitUrl = limitUrl;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPlatformId() { return platformId; }
    public long getBalance() { return balance; }
    public int getUserId() { return userId; }
    public String getLogoUrl() { return logoUrl; }
    public int getPoints() { return points; }
    public int getPlayerStatus() { return playerStatus; }
    public int getAvailability() { return availability; }
    public boolean isAdmin() { return isAdmin; }
    public int getPosition() { return position; }
    public long getTotalValue() { return totalValue; }
    public boolean isIdf() { return isIdf; }
    public String getLimitUrl() { return limitUrl; }
    public String getProfileImageUrl() { return profileImageUrl; }
}


