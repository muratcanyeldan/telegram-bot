![GitHub CI](https://github.com/muratcanyeldan/telegram-bot/blob/master/.github/workflows/maven.yml/badge.svg)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/muratcanyeldan/)
&nbsp;
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/muratcanyeldan)
&nbsp;

# My Related Articles on Medium

[![Medium](https://img.shields.io/badge/Medium-12100E?style=for-the-badge&logo=medium&logoColor=white) How to Create a Telegram Bot with Spring Boot](https://muratcanyeldan.com/how-to-create-a-telegram-bot-with-spring-boot-9289d81dfe6a)


# Telegram Bot Example

This project is a simple Telegram bot implemented using Java and Spring Boot. The bot responds to various commands,
including `/start`, `/help`, `/hello`, `/joke`, `/name_surname`, and `/chat_id`.

## Features

- **/start**: Greets the user and provides instructions on how to get help.
- **/start_with_reply**: Greets the user and provides instructions using a reply keyboard.
- **/hello**: Sends a simple greeting to the user.
- **/help**: Lists all available bot commands with their descriptions.
- **/joke**: Tells a random joke to the user.
- **/name_surname**: Displays the user's full name (first name and last name).
- **/chat_id**: Displays the user's chat ID.

## Running the Application

### Prerequisites

- **Java 21 or above**
- **Telegram Bot API token. You can get it by creating a bot via BotFather.**

### Setup & Run

1. **Clone the repository**:
    ```bash
    git clone https://github.com/muratcanyeldan/telegram-bot.git
    cd telegram-bot
    ```
2. **Configure the bot**:

   Open the application.yml file and set your bot's username and token.
    ```yaml
    telegram:
      bot:
        token: <ENTER-YOUR-BOT-TOKEN>
        username: <ENTER-YOUR-BOT-USERNAME>
    ```
3. **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```
   Your bot should now be up and running!