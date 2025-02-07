package com.kickbasebot.service;

import com.kickbasebot.data.managers.Profile;
import com.kickbasebot.data.market.League;
import com.kickbasebot.data.market.PlayerOnMarket;
import com.kickbasebot.data.me.PlayerOnSquad;
import com.kickbasebot.data.Ranking;

import java.io.IOException;
import java.util.List;

/**
 * Interface for the KickbaseBotImpl, which interacts with the Kickbase API.
 * It defines methods for authentication, retrieving game data, and placing bids.
 */
public interface KickbaseBot {

    /**
     * Authenticates the user with the given email address and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A token required for further requests.
     * @throws IOException If there is an issue accessing the Kickbase API.
     * @throws InterruptedException If the process is interrupted.
     */
    String login(String email, String password) throws IOException, InterruptedException;

    /**
     * Retrieves the league in which the bot is active.
     *
     * @return The league as {@link League}.
     * @throws IOException If there is an issue retrieving the league ID.
     * @throws InterruptedException If the process is interrupted.
     */
    League getLeague() throws IOException, InterruptedException;

    /**
     * Retrieves the league ranking.
     *
     * @return The ranking as {@link Ranking}.
     * @throws IOException If there is an issue retrieving the ranking.
     * @throws InterruptedException If the process is interrupted.
     */
    Ranking fetchAndPopulateRanking() throws IOException, InterruptedException;

    /**
     * Retrieves the user's profile.
     *
     * @return The user's profile as {@link Profile}.
     * @throws IOException If there is an issue retrieving the profile.
     * @throws InterruptedException If the process is interrupted.
     */
    Profile getProfile() throws IOException, InterruptedException;

    /**
     * Retrieves the user's player information.
     *
     * @return A list of players on the squad as {@link List<PlayerOnSquad>}.
     * @throws IOException If there is an issue retrieving the player information.
     * @throws InterruptedException If the process is interrupted.
     */
    List<PlayerOnSquad> getPlayers() throws IOException, InterruptedException;

    /**
     * Sells a player from the user's squad.
     * @param playerId The ID of the player to be sold.
     * @throws IOException If there is an issue selling the player.
     * @throws InterruptedException If the process is interrupted.
     */
    void sellPlayer(String playerId) throws IOException, InterruptedException;

    /**
     * Retrieves a list of players available on the transfer market.
     *
     * @return A list of players as {@link List<PlayerOnMarket>}.
     * @throws IOException If there is an issue retrieving the market players.
     * @throws InterruptedException If the process is interrupted.
     */
    List<PlayerOnMarket> getPlayersFromMarket() throws IOException, InterruptedException;

    /**
     * Places bids on expiring players in the Kickbase system.
     *
     * @throws IOException If there is an issue placing bids.
     * @throws InterruptedException If the process is interrupted.
     */
    void placeBids() throws IOException, InterruptedException;

}