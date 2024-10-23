package com.muratcan.telegrambotexample.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum BotCommand {
	START("/start", "Greets and recommends help command"),
	HELLO("/hello", "Simple greeter"),
	HELP("/help", "Shows available commands"),
	ECHO("/echo", "Echo back what you typed"),
	NAME_SURNAME("/name_surname", "Shows your name and surname"),
	CHAT_ID("/chat_id", "Shows your chat id");

	private final String command;
	private final String description;

	BotCommand(String command, String description) {
		this.command = command;
		this.description = description;
	}

	public static Optional<BotCommand> fromCommand(String command) {
		return Arrays.stream(values())
				.filter(botCommand -> botCommand.getCommand().equalsIgnoreCase(command))
				.findFirst();
	}

	public static String getAvailableCommands() {
		StringBuilder sb = new StringBuilder();
		for (BotCommand command : BotCommand.values()) {
			sb.append(command.getCommand()).append(" -> ").append(command.getDescription()).append("\n");
		}
		return sb.toString();
	}
}
