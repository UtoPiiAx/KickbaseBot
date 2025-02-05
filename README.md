
# KickbaseBot

KickbaseBot is a Java-based bot designed to interact with the Kickbase platform. It provides functionality to automate various actions such as logging in, fetching league data, managing player listings, and handling offers, all while utilizing the Kickbase API.

## Features

- **Login to Kickbase**: Login functionality using your Kickbase account credentials.
- **Get League Data**: Fetch your league data including points, teams, and balance.
- **Player Management**: Retrieve and manage players available in the market.
- **Offer Handling**: View and place offers for players in the Kickbase marketplace.
- **Leaderboard Tracking**: Fetch and display the current league rankings and leaderboard.

## Prerequisites

Before using KickbaseBot, ensure that you have the following installed on your system:

- **Java 11 or newer**: The bot is built using Java 11 (or higher). You can download it from [AdoptOpenJDK](https://adoptopenjdk.net/).
- **Maven**: Maven is used for managing dependencies and building the project. You can download it from [Apache Maven](https://maven.apache.org/).

## Setup

1. **Clone the repository**

   To get started, clone the repository to your local machine using the following command:

   ```bash
   git clone https://github.com/UtoPiiAx/KickbaseBot.git
   ```

2. **Install dependencies**

   Navigate to the project directory and run Maven to download the necessary dependencies:

   ```bash
   cd KickbaseBot
   mvn install
   ```

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
