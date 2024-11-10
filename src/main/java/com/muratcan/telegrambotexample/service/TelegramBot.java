package com.muratcan.telegrambotexample.service;

import com.muratcan.telegrambotexample.model.BotCommandInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Component
public class TelegramBot extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(TelegramBot.class);
	private static final List<BotCommandInfo> commandInfos = List.of(
			new BotCommandInfo("/start", "Greets and recommends help command", "Start"),
			new BotCommandInfo("/start_with_reply", "Starts the bot with reply keyboard", "Start with Reply"),
			new BotCommandInfo("/hello", "Simple greeter", "Hello"),
			new BotCommandInfo("/help", "Shows available commands", "Help"),
			new BotCommandInfo("/joke", "Tells a random joke", "Tell me a joke"),
			new BotCommandInfo("/name_surname", "Shows your name and surname", "Name Surname"),
			new BotCommandInfo("/chat_id", "Shows your chat id", "Chat ID")
	);
	private final Random random = new Random();
	private final String botUsername;


	public TelegramBot(@Value("${telegram.bot.username}") String botUsername,
					   @Value("${telegram.bot.token}") String botToken,
					   DefaultBotOptions options) {
		super(options, botToken);
		this.botUsername = botUsername;

		try {
			List<BotCommand> commands = commandInfos.stream()
					.map(info -> new BotCommand(info.command(), info.description()))
					.toList();
			this.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
		} catch (TelegramApiException e) {
			logger.error("Failed to set bot commands: {}", e.getMessage());
		}
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

			String username = String.format("%s %s",
					update.getMessage().getFrom().getFirstName(),
					update.getMessage().getFrom().getLastName());

			handleCommands(chatId, receivedMessage, username);
		} else if (update.hasCallbackQuery()) {
			CallbackQuery callbackQuery = update.getCallbackQuery();

			String username = String.format("%s %s",
					callbackQuery.getFrom().getFirstName(),
					callbackQuery.getFrom().getLastName());

			String callbackData = callbackQuery.getData();
			String chatId = callbackQuery.getMessage().getChatId().toString();

			handleCallbackQuery(callbackData, chatId, username);
		}
	}

	public void sendMessage(String chatId, String message) {
		sendMessage(chatId, message, null);
	}

	public void sendMessage(String chatId, String message, ReplyKeyboard replyKeyboard) {
		SendMessage sendMessage = SendMessage.builder()
				.chatId(chatId)
				.text(message)
				.replyMarkup(replyKeyboard)
				.build();

		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			logger.error("Failed to send message to chatId: {} with error: {}", chatId, e.getMessage());
		}
	}

	private void handleCallbackQuery(String callbackData, String chatId, String username) {
		switch (callbackData) {
			case "hello_callback":
				handleHelloCommand(chatId, username);
				break;
			case "help_callback":
				handleHelpCommand(chatId);
				break;
			case "another_joke":
				handleJokeCommand(chatId);
				break;
			default:
				sendMessage(chatId, "Unknown action!");
				break;
		}
	}


	private void handleCommands(String chatId, String receivedMessage, String username) {

		String messageText = receivedMessage.trim();

		Optional<String> optionalCommand = commandInfos.stream()
				.filter(info -> info.label().equalsIgnoreCase(messageText))
				.map(BotCommandInfo::command)
				.findFirst();

		String commandKey;

		if (optionalCommand.isPresent()) {
			commandKey = optionalCommand.get().toLowerCase();
		} else if (messageText.startsWith("/")) {
			commandKey = messageText.split(" ")[0].toLowerCase();
		} else {
			commandKey = messageText.toLowerCase();
		}

		Map<String, Runnable> commandHandlers = Map.of(
				"/start", () -> handleStartCommand(chatId, username),
				"/start_with_reply", () -> handleStartWithReplyCommand(chatId, username),
				"/hello", () -> handleHelloCommand(chatId, username),
				"/joke", () -> handleJokeCommand(chatId),
				"/name_surname", () -> handleNameSurnameCommand(chatId, username),
				"/help", () -> handleHelpCommand(chatId),
				"/chat_id", () -> sendMessage(chatId, chatId)
		);

		commandHandlers.getOrDefault(commandKey, () -> handleUnknownCommand(chatId))
				.run();
	}

	private void handleUnknownCommand(String chatId) {
		logger.info("Received unknown command");
		sendMessage(chatId, "Unknown command. Please use the keyboard or type /help.", getCommandKeyboard());
	}

	private void handleStartCommand(String chatId, String username) {
		String message = String.format("Hello %s! Welcome to the Telegram Bot. Choose an option:", username);
		sendMessage(chatId, message, getInlineKeyboard());
	}

	private void handleStartWithReplyCommand(String chatId, String username) {
		String message = String.format("Hello %s! Welcome to the Telegram Bot. Use the commands below.", username);
		sendMessage(chatId, message, getCommandKeyboard());
	}

	private void handleHelloCommand(String chatId, String username) {
		String message = String.format("Hello %s!", username);
		sendMessage(chatId, message);
	}

	private void handleNameSurnameCommand(String chatId, String username) {
		sendMessage(chatId, username);
	}

	private void handleJokeCommand(String chatId) {
		String joke = getRandomJoke();
		sendMessage(chatId, joke, getInlineKeyboard());
	}

	private void handleHelpCommand(String chatId) {
		StringBuilder sb = new StringBuilder("Available commands:\n");
		for (BotCommandInfo info : commandInfos) {
			sb.append(info.command())
					.append(" -> ")
					.append(info.description())
					.append("\n");
		}
		sendMessage(chatId, sb.toString(), getCommandKeyboard());
	}

	private ReplyKeyboardMarkup getCommandKeyboard() {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		keyboardMarkup.setResizeKeyboard(true);

		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow firstRow = new KeyboardRow();
		firstRow.add("Start");
		firstRow.add("Help");
		keyboard.add(firstRow);

		KeyboardRow secondRow = new KeyboardRow();
		secondRow.add("Hello");
		secondRow.add("Tell me a joke");
		keyboard.add(secondRow);

		keyboardMarkup.setKeyboard(keyboard);
		return keyboardMarkup;
	}

	private InlineKeyboardMarkup getInlineKeyboard() {
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

		InlineKeyboardButton jokeButton = new InlineKeyboardButton();
		jokeButton.setText("Another Joke");
		jokeButton.setCallbackData("another_joke");

		InlineKeyboardButton helloButton = new InlineKeyboardButton();
		helloButton.setText("Say Hello");
		helloButton.setCallbackData("hello_callback");

		InlineKeyboardButton helpButton = new InlineKeyboardButton();
		helpButton.setText("Help");
		helpButton.setCallbackData("help_callback");
		helpButton.setUrl("https://muratcanyeldan.com/");

		List<InlineKeyboardButton> rowInline = new ArrayList<>();
		rowInline.add(helloButton);
		rowInline.add(helpButton);
		rowInline.add(jokeButton);

		List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
		rowsInline.add(rowInline);

		inlineKeyboardMarkup.setKeyboard(rowsInline);

		return inlineKeyboardMarkup;
	}

	private String getRandomJoke() {
		List<String> jokes = Arrays.asList(
				"Why did the developer go broke? Because he used up all his cache.",
				"Why do programmers prefer dark mode? Because light attracts bugs.",
				"How many programmers does it take to change a light bulb? None, it's a hardware problem.",
				"Why did the functions stop calling each other? Because they had constant arguments.",
				"Where did the API go to eat? To the RESTaurant.",
				"There are 10 types of people in this world... Those who understand binary and those who don't"
		);

		return jokes.get(random.nextInt(jokes.size()));
	}
}
