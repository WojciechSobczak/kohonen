package utils;

public class Logger {
	
	public static void log(String message) {
		if (Settings.LOGGER_ENABLED) {
			System.out.println(message);
		}
	}

}
