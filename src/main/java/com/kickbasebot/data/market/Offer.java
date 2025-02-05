package com.kickbasebot.data.market;

public class Offer {

    private String userId;          // "u"
    private String username;        // "unm"
    private String userOfferId;     // "uoid"
    private long userOfferPrice;    // "uop"
    private int status;             // "st"
    private String userImage;       // "uim"

    public Offer(String userId, String username, String userOfferId, long userOfferPrice, int status, String userImage) {
        this.userId = userId;
        this.username = username;
        this.userOfferId = userOfferId;
        this.userOfferPrice = userOfferPrice;
        this.status = status;
        this.userImage = userImage;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserOfferId() { return userOfferId; }
    public long getUserOfferPrice() { return userOfferPrice; }
    public int getStatus() { return status; }
    public String getUserImage() { return userImage; }
}
