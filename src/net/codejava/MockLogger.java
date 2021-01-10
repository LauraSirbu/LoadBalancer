package net.codejava;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MockLogger implements ILogger {
	public void Log(String logMessage, Severity severity)
	{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println("(" + LocalDateTime.now().format(formatter) + ") (" + severity +  "): " + logMessage);
	}
}
