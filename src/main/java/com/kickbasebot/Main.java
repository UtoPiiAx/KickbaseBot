package com.kickbasebot;

import com.kickbasebot.service.KickbaseBotImpl;

import java.io.Console;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Console console = System.console();
        String email;
        String password;

        if (console != null) {
            System.out.println("\nWelcome to the Kickbase Bot! In order to proceed, please enter your Kickbase credentials. \nWe will not store your credentials and your password input will be hidden.\n");
            System.out.print("E-Mail: ");
            email = console.readLine();
            System.out.print("Password: ");
            char[] passwordArray = console.readPassword();
            password = new String(passwordArray);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("E-Mail: ");
            email = scanner.nextLine();
            System.out.print("Password: ");
            password = readPasswordWithAsterisks(scanner);
        }

        KickbaseBotImpl bot = KickbaseBotImpl.getInstance();

        try {
            bot.login(email, password);
            bot.getLeague();
            Scanner scanner = new Scanner(System.in);

            System.out.print("\nWould you like to see your ranking? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                bot.fetchAndPopulateRanking();
            }

            System.out.print("\nWould you like to see your profile stats? (yes/no): ");
            response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                bot.getProfile();
            }

            System.out.print("\nWould you like to see your own players? (yes/no): ");
            response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                bot.getPlayers();
            }

            while (true) {
                System.out.print("\nWould you like to sell a player? (yes/no): ");
                response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("yes")) {
                    break;
                }
                System.out.print("\nPlayerID of the player you want to sell: ");
                String playerId = scanner.nextLine().trim();
                bot.sellPlayer(playerId);
            }

            bot.getPlayersFromMarket();

            System.out.print("Would you like to bid on the players on the market who expire next? (yes/no): ");
            response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("yes")) {
                bot.placeBids();
                System.out.println("\nBids have been placed.");
            } else {
                System.out.println("\nNo bids placed. See you next time!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readPasswordWithAsterisks(Scanner scanner) {
        StringBuilder password = new StringBuilder();
        while (true) {
            char ch = scanner.nextLine().charAt(0);
            if (ch == '\n') break;
            password.append(ch);
            System.out.print("*");
        }
        System.out.println();
        return password.toString();
    }
}