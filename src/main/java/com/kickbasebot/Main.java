package com.kickbasebot;

import java.io.Console;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Console console = System.console();
        String email = "";
        String password = "";

        if (console != null) {
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
            bot.fetchAndPopulateRanking();
            bot.getProfile();
            bot.getPlayers();
            bot.getPlayersFromMarket();

            System.out.print("Would you like to place bids? (yes/no): ");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();

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