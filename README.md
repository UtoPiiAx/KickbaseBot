# KickbaseBot

KickbaseBot is a Java-based bot designed to interact with the Kickbase platform. It automates various actions such as logging in, fetching league data, managing player listings, and handling bids using the Kickbase API.

## Features

- **User Authentication**: Secure login using Kickbase account credentials.
- **League Management**: Fetch and display league data including teams, points, and balance.
- **Leaderboard Tracking**: Retrieve and display league rankings.
- **Player Management**: Retrieve owned players and sell players from your squad.
- **Market Interaction**: Fetch available players from the transfer market and place bids on expiring players.

## Prerequisites

Before using KickbaseBot, ensure that you have the following installed on your system:

- **Java 11 or newer**: Required to run the application. Download it from [AdoptOpenJDK](https://adoptopenjdk.net/).
- **Maven**: Used for dependency management and building the project. Download it from [Apache Maven](https://maven.apache.org/).

## Setup

### 1. Clone the repository

To get started, clone the repository to your local machine using the following command:

```bash
git clone https://github.com/UtoPiiAx/KickbaseBot.git
```

### 2. Install dependencies

Navigate to the project directory and install the necessary dependencies using Maven:

```bash
cd KickbaseBot
mvn install
```

### 3. Run the application

To start the KickbaseBot, use the following command:

```bash
mvn exec:java -Dexec.mainClass="com.kickbasebot.Main"
```

## Usage

Upon running the application, the bot will prompt you to enter your Kickbase credentials. Your password input will be hidden for security reasons.

### Available Actions:
- **View League Ranking**: Choose to see your league ranking.
- **View Profile Stats**: Retrieve your profile data.
- **View Owned Players**: Display your current squad.
- **Sell a Player**: Enter the PlayerID of a player to sell them.
- **Fetch Market Players**: Retrieve a list of players available on the transfer market.
- **Place Bids**: Choose whether to bid on players whose listings expire soon.

## Development

### Code Structure

- `Main.java`: Entry point of the application, handling user input and interaction.
- `KickbaseBot.java`: Interface defining the main bot functionalities.
- `KickbaseBotImpl.java`: Implementation of `KickbaseBot`, interacting with the Kickbase API.
- `data/`: Contains models representing players, leagues, rankings, and profiles.

### Extending Functionality

To add new features, modify `KickbaseBotImpl.java` to implement additional API interactions or enhance existing functionalities.

## Contributing

We welcome contributions to improve KickbaseBot! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

### Steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -am 'Add feature'`).
4. Push to your fork (`git push origin feature-name`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
