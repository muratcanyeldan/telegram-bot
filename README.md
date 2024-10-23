# Telegram Bot Example

This project is a simple Telegram bot implemented using Java and Spring Boot. The bot responds to various commands,
including `/start`, `/help`, `/hello`, `/echo`, `/name_surname`, and `/chat_id`.

## Features

- **/start**: Greets the user and provides instructions on how to get help.
- **/hello**: Sends a simple greeting to the user.
- **/help**: Lists all available bot commands with their descriptions.
- **/echo**: Echoes back the text that the user sends after the `/echo` command.
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