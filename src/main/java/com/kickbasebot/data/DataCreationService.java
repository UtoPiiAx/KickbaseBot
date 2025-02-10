package com.kickbasebot.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Offer;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.Budget;
import com.kickbasebot.data.me.PlayerOnSquad;

import java.util.List;

public class DataCreationService {

    public Budget createBudget(JsonNode budgetData) {
        return new Budget(
                budgetData.get("pbaa").asDouble(), // projectedBudgetAfterAllActions
                budgetData.get("pbas").asDouble(), // projectedBudgetAfterSales
                budgetData.get("b").asDouble(),    // currentBudget
                budgetData.get("bs").asInt()       // budgetStatus
        );
    }

    public Ranking.User createRankingUser(JsonNode userData, List<Integer> leaguePoints) {
        return new Ranking.User(
                userData.has("i") ? userData.get("i").asText() : "",   // Benutzer-ID
                userData.has("n") ? userData.get("n").asText() : "",   // Benutzername
                userData.has("adm") && userData.get("adm").asBoolean(), // Admin-Status
                userData.has("sp") ? userData.get("sp").asInt() : 0,   // Punkte
                userData.has("mdpl") ? userData.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                userData.has("shp") ? userData.get("shp").asInt() : 0,  // Shop-Punkte
                userData.has("tv") ? userData.get("tv").asDouble() : 0.0, // Gesamtwert
                userData.has("spl") ? userData.get("spl").asInt() : 0,  // Sonderpunkte
                userData.has("mdpl") ? userData.get("mdpl").asInt() : 0, // Matchday-Sonderpunkte
                userData.has("shpl") ? userData.get("shpl").asInt() : 0, // Shop-Sonderpunkte
                userData.has("pa") && userData.get("pa").asBoolean(), // Aktiv-Status
                leaguePoints,  // League-Punkte
                userData.has("uim") ? userData.get("uim").asText() : null // Profilbild-URL
        );
    }

    public Ranking createRanking(JsonNode rankingData, List<Ranking.User> users) {
        return new Ranking(
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
    }

    public League createLeague(JsonNode leagueData) {
        return new League(
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
    }

    public Profile createProfile(JsonNode profileData, List<Integer> pastMatchdayPoints) {
        return new Profile(
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
                profileData.get("t").asInt(),              // Transfers
                pastMatchdayPoints,                        // Punkte aus den letzten Spieltagen
                profileData.get("uim").asText(),           // User-Profilbild
                profileData.get("lim").asText()            // Liga-Bild
        );
    }

    public Offer createOffer(JsonNode offerData, List<Offer> offers) {
        return new Offer(
                offerData.has("u") ? offerData.get("u").asText() : "",               // Benutzer-ID
                offerData.has("unm") ? offerData.get("unm").asText() : "",           // Benutzername
                offerData.has("uoid") ? offerData.get("uoid").asText() : "",         // Benutzerangebots-ID
                offerData.has("uop") ? offerData.get("uop").asLong() : 0L,           // Benutzerangebotspreis
                offerData.has("st") ? offerData.get("st").asInt() : 0,               // Status (Standardwert 0)
                offerData.has("uim") ? offerData.get("uim").asText() : ""            // Benutzerbild
        );
    }

    public PlayerOnMarket createPlayersFromMarket(JsonNode playerData, List<Offer> offers) {
        return new PlayerOnMarket(
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
                offers                                                                // Liste von Angeboten
        );
    }

    public PlayerOnSquad createPlayerOnSquad(JsonNode playerData) {
        return new PlayerOnSquad(
                playerData.has("mvgl") ? playerData.get("mvgl").asInt() : 0,            // Market value in the league
                playerData.has("i") ? playerData.get("i").asText() : "",                // PlayerOnSquad ID
                playerData.has("n") ? playerData.get("n").asText() : "Unknown",         // PlayerOnSquad name
                playerData.has("lo") ? playerData.get("lo").asInt() : 0,                // Loan status
                playerData.has("lst") ? playerData.get("lst").asInt() : 0,              // Last transfer status
                playerData.has("st") ? playerData.get("st").asInt() : 0,                // Status
                playerData.has("mdst") ? playerData.get("mdst").asInt() : 0,            // Matchday status
                playerData.has("pos") ? playerData.get("pos").asInt() : 0,              // Position in the rank
                playerData.has("mv") ? playerData.get("mv").asLong() : 0L,              // Market value
                playerData.has("mvt") ? playerData.get("mvt").asInt() : 0,              // Market value type
                playerData.has("p") ? playerData.get("p").asInt() : 0,                  // Points
                playerData.has("ap") ? playerData.get("ap").asInt() : 0,                // Average points
                playerData.has("iotm") && playerData.get("iotm").asBoolean(),           // Is in the team of the moment
                playerData.has("ofc") ? playerData.get("ofc").asInt() : 0,              // Offensive contribution
                playerData.has("tid") ? playerData.get("tid").asText() : "",            // Team ID
                playerData.has("sdmvt") ? playerData.get("sdmvt").asLong() : 0L,        // Market value change in the last 7 days
                playerData.has("tfhmvt") ? playerData.get("tfhmvt").asLong() : 0L,      // Market value change in the last day
                playerData.has("pim") ? playerData.get("pim").asText() : ""             // PlayerOnSquad image URL
        );
    }

}
