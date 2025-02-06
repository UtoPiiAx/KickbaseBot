package com.kickbasebot.data.me;

public class PlayerOnSquad {

    private int marketValueLeague; // "mvgl"
    private String playerId; // "i"
    private String playerName; // "n"
    private int loanStatus; // "lo"
    private int lastTransferStatus; // "lst"
    private int status; // "st"
    private int matchdayStatus; // "mdst"
    private int positionInRank; // "pos"
    private long marketValue; // "mv"
    private int marketValueType; // "mvt"
    private int points; // "p"
    private int averagePoints; // "ap"
    private boolean isInTeamOfTheMonth; // "iotm"
    private int offensiveContribution; // "ofc"
    private String teamId; // "tid"
    private long marketValueChangeInLastWeek; // "sdmvt"
    private long marketValueChangeInLastDay; // "tfhmvt"
    private double percentageChangeLastDay;
    private String playerImageUrl; // "pim"

    public PlayerOnSquad(int marketValueLeague, String playerId, String playerName, int loanStatus, int lastTransferStatus,
                         int status, int matchdayStatus, int positionInRank, long marketValue, int marketValueType,
                         int points, int averagePoints, boolean isInTeamOfTheMoment, int offensiveContribution,
                         String teamId, long marketValueChangeInLastWeek, long marketValueChangeInLastDay, String playerImageUrl) {
        this.marketValueLeague = marketValueLeague;
        this.playerId = playerId;
        this.playerName = playerName;
        this.loanStatus = loanStatus;
        this.lastTransferStatus = lastTransferStatus;
        this.status = status;
        this.matchdayStatus = matchdayStatus;
        this.positionInRank = positionInRank;
        this.marketValue = marketValue;
        this.marketValueType = marketValueType;
        this.points = points;
        this.averagePoints = averagePoints;
        this.isInTeamOfTheMonth = isInTeamOfTheMoment;
        this.offensiveContribution = offensiveContribution;
        this.teamId = teamId;
        this.marketValueChangeInLastWeek = marketValueChangeInLastWeek;
        this.marketValueChangeInLastDay = marketValueChangeInLastDay;
        this.playerImageUrl = playerImageUrl;
    }

    public void setPercentageChangeLastDay(double percentageChangeLastDay) {
        this.percentageChangeLastDay = percentageChangeLastDay;
    }

    public double getPercentageChangeLastDay() {
        return percentageChangeLastDay;
    }

    public int getMarketValueLeague() {
        return marketValueLeague;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLoanStatus() {
        return loanStatus;
    }

    public int getLastTransferStatus() {
        return lastTransferStatus;
    }

    public int getStatus() {
        return status;
    }

    public int getMatchdayStatus() {
        return matchdayStatus;
    }

    public int getPositionInRank() {
        return positionInRank;
    }

    public long getMarketValue() {
        return marketValue;
    }

    public int getMarketValueType() {
        return marketValueType;
    }

    public int getPoints() {
        return points;
    }

    public int getAveragePoints() {
        return averagePoints;
    }

    public boolean isInTeamOfTheMonth() {
        return isInTeamOfTheMonth;
    }

    public int getOffensiveContribution() {
        return offensiveContribution;
    }

    public String getTeamId() {
        return teamId;
    }

    public long getMarketValueChangeInLastWeek() {
        return marketValueChangeInLastWeek;
    }

    public long getMarketValueChangeInLastDay() {
        return marketValueChangeInLastDay;
    }

    public String getPlayerImageUrl() {
        return playerImageUrl;
    }

    @Override
    public String toString() {
        return "PlayerOnSquad{" +
                "marketValueLeague=" + marketValueLeague +
                ", playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", loanStatus=" + loanStatus +
                ", lastTransferStatus=" + lastTransferStatus +
                ", status=" + status +
                ", matchdayStatus=" + matchdayStatus +
                ", positionInRank=" + positionInRank +
                ", marketValue=" + marketValue +
                ", marketValueType=" + marketValueType +
                ", points=" + points +
                ", averagePoints=" + averagePoints +
                ", isInTeamOfTheMoment=" + isInTeamOfTheMonth +
                ", offensiveContribution=" + offensiveContribution +
                ", teamId='" + teamId + '\'' +
                ", marketValueChangeInLastWeek=" + marketValueChangeInLastWeek +
                ", marketValueChangeInLastDay=" + marketValueChangeInLastDay +
                ", playerImageUrl='" + playerImageUrl + '\'' +
                '}';
    }
}
