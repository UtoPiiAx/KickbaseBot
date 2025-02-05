package com.kickbasebot;

import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.Player;
import com.kickbasebot.data.Ranking;

import java.io.IOException;
import java.util.List;

/**
 * Interface für den KickbaseBotImpl, der mit der Kickbase API interagiert.
 * Es definiert Methoden für die Authentifizierung, das Abrufen von Spieldaten und das Platzieren von Geboten.
 */
public interface KickbaseBot {

    /**
     * Authentifiziert den Benutzer mit der angegebenen E-Mail-Adresse und dem Passwort.
     *
     * @param email Die E-Mail-Adresse des Benutzers.
     * @param password Das Passwort des Benutzers.
     * @return Ein Token, der für weitere Anfragen benötigt wird.
     * @throws IOException Wenn ein Problem beim Zugriff auf die Kickbase API auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    String login(String email, String password) throws IOException, InterruptedException;

    /**
     * Ruft die Liga ab, in der der Bot aktiv ist.
     *
     * @return Die Liga als {@link League}.
     * @throws IOException Wenn ein Problem beim Abrufen der Liga-ID auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    League getLeague() throws IOException, InterruptedException;

    /**
     * Ruft die Rangliste der Liga ab.
     *
     * @return Die Rangliste als {@link Ranking}.
     * @throws IOException Wenn ein Problem beim Abrufen der Rangliste auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    public Ranking fetchAndPopulateRanking() throws IOException, InterruptedException;

    /**
     * Ruft das Profil des Benutzers ab.
     * @return Das Profil des Benutzers als {@link Profile}.
     * @throws IOException Wenn ein Problem beim Abrufen des Profils auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    public Profile getProfile() throws IOException, InterruptedException;

    /**
     * Ruft eine Liste von Spielern ab, die auf dem Transfermarkt sind.
     *
     * @return Eine Liste der Spieler als {@link List< Player >}.
     * @throws IOException Wenn ein Problem beim Abrufen der Spielerinformationen auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    List<Player> getPlayers() throws IOException, InterruptedException;

    /**
     * Platziert Gebote auf ablaufende Spieler im Kickbase-System.
     *
     * @throws IOException Wenn ein Problem beim Platzieren der Gebote auftritt.
     * @throws InterruptedException Wenn der Vorgang unterbrochen wird.
     */
    void placeBids() throws IOException, InterruptedException;

    /**
     * Formatiert eine Zahl (z.B. für Preisangaben) in eine benutzerfreundliche Darstellung.
     *
     * @param number Die Zahl, die formatiert werden soll.
     * @return Die formatierte Zahl als {@link String}.
     */
    String formatNumber(double number);
}
