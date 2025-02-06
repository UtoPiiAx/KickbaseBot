package com.kickbasebot.data.market;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlayerOnMarket {

    private String id;                  // "i"
    private String firstName;           // "fn"
    private String lastName;            // "n"
    private String teamId;              // "tid"
    private int position;               // "pos"
    private int status;                 // "st"
    private int marketValueTrend;       // "mvt"
    private long marketValue;           // "mv"
    private int totalPoints;            // "p"
    private int averagePoints;          // "ap"
    private int offersCount;            // "ofc"
    private int remainingSeconds;       // "exs"
    private long price;                 // "prc"
    private long userOfferPrice;        // "uop"
    private String userOfferId;         // "uoid"
    private boolean isNew;              // "isn"
    private boolean isPositionLocked;   // "iposl"
    private LocalDateTime date;         // "dt"
    private String playerImage;         // "pim"
    private List<Offer> offers;         // "ofs"

    public PlayerOnMarket(String id, String firstName, String lastName, String teamId, int position, int status,
                          int marketValueTrend, long marketValue, int totalPoints, int averagePoints, int offersCount,
                          int remainingSeconds, long price, long userOfferPrice, String userOfferId, boolean isNew,
                          boolean isPositionLocked, String date, String playerImage, List<Offer> offers) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.teamId = teamId;
        this.position = position;
        this.status = status;
        this.marketValueTrend = marketValueTrend;
        this.marketValue = marketValue;
        this.totalPoints = totalPoints;
        this.averagePoints = averagePoints;
        this.offersCount = offersCount;
        this.remainingSeconds = remainingSeconds;
        this.price = price;
        this.userOfferPrice = userOfferPrice;
        this.userOfferId = userOfferId;
        this.isNew = isNew;
        this.isPositionLocked = isPositionLocked;
        this.playerImage = playerImage;
        this.offers = offers;

        this.date = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTeamId() { return teamId; }
    public int getPosition() { return position; }
    public int getStatus() { return status; }
    public int getMarketValueTrend() { return marketValueTrend; }
    public long getMarketValue() { return marketValue; }
    public int getTotalPoints() { return totalPoints; }
    public int getAveragePoints() { return averagePoints; }
    public int getOffersCount() { return offersCount; }
    public int getRemainingSeconds() { return remainingSeconds; }
    public long getPrice() { return price; }
    public long getUserOfferPrice() { return userOfferPrice; }
    public String getUserOfferId() { return userOfferId; }
    public boolean isNew() { return isNew; }
    public boolean isPositionLocked() { return isPositionLocked; }
    public LocalDateTime getDate() { return date; }
    public String getPlayerImage() { return playerImage; }
    public List<Offer> getOffers() { return offers; }

}
