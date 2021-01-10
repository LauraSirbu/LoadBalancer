package net.codejava;

public interface ILogger {
	
	public enum Severity 
	{
		Info,
		Debug,
		Warning,
		Error
	}
	
	public void Log(String logMessage, Severity severity);
}
