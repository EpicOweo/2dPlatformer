package com.epicoweo.platformer.etc;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Log {

	public static enum MessageType {
		INFO, WARNING, ERROR;
	}
	
	public static void print(MessageType type, String message, boolean timeStamp) {
		String printMessage = "";
		String ts = "";
		
		LocalTime now = LocalTime.now();
		
		String color = "";
		
		switch(type) {
		case INFO:
			color = Refs.ANSI_CYAN;
			printMessage = "[INFO] ";
			break;
		case WARNING:
			color = Refs.ANSI_YELLOW;
			printMessage = "[WARNING] ";
			break;
		case ERROR:
			color = Refs.ANSI_RED;
			printMessage = "[ERROR] ";
			break;
		default:
			break;
		}
		
		if(timeStamp) {
			ts = "[" + now.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)) + "] ";
		} else {
			ts = " ";
		}
		
		printMessage += message;
		
		System.out.println(color + ts + printMessage + Refs.ANSI_RESET);
	}
	
	public static void print(MessageType type, String message) {
		print(type, message, true);
	}
}
