package com.muratcan.telegrambotexample.service;

import com.muratcan.telegrambotexample.enums.BotCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
	private final String botUsername;

	public TelegramBot(@Value("${telegram.bot.username}") String botUsername,
					   @Value("${telegram.bot.token}") String botToken,
					   DefaultBotOptions options) {
		super(options, botToken);
		this.botUsername = botUsername;
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String chatId = update.getMessage().getChatId().toString();
			String receivedMessage = update.getMessage().getText();
			String username = String.format("%s %s", update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getLastName());

			handleCommands(chatId, receivedMessage, username);
		}
	}

	private void handleCommands(String chatId, String receivedMessage, String username) {
		BotCommand.fromCommand(receivedMessage.split(" ")[0]).ifPresentOrElse(
				botCommand -> {
					switch (botCommand) {
						case START -> {
							String message = String.format("""
									Hello %s ! Welcome to the Telegram Bot. Send "help" to get available commands for this bot.
									""", username);
							sendMessage(chatId, message);
						}
						case HELLO -> {
							String message = String.format("Hello %s !", username);
							sendMessage(chatId, message);
						}
						case ECHO -> {
							String message = receivedMessage.replace(BotCommand.ECHO.getCommand(), "").trim();
							if (message.isBlank()) {
								message = """
										Nothing to echo! You should write "/echo text"
										""";
							}
							sendMessage(chatId, message);
						}
						case NAME_SURNAME -> sendMessage(chatId, username);
						case HELP -> {
							String message = BotCommand.getAvailableCommands();
							sendMessage(chatId, message);
						}
						case CHAT_ID -> sendMessage(chatId, chatId);
					}
				},
				() -> logger.info("Received unknown command: {}", receivedMessage)
		);
	}

	public void sendMessage(String chatId, String message) {
		SendMessage sendMessage = new SendMessage(chatId, message);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			logger.error("Failed to send message to chatId: {} with error: {}", chatId, e.getMessage());
		}
	}
}
