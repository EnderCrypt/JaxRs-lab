package se.github.logger;

import java.util.HashMap;
import java.util.logging.Level;

public class MultiLogger
{
	private static HashMap<String, SingleLogger> loggers = new HashMap<>();

	private MultiLogger()
	{
	}

	public static void createLogger(String name)
	{
		if (loggers.containsKey(name.toLowerCase()))
		{
			throw new RuntimeException("Logger with the name \"" + name + "\" already exists");
		}
		else
		{
			loggers.put(name.toLowerCase(), new SingleLogger(name));
		}
	}

	private static SingleLogger getLogger(String name)
	{
		if (loggers.containsKey(name.toLowerCase()) == false)
		{
			throw new RuntimeException("Unknown logger: " + name);
		}
		else
		{
			return loggers.get(name.toLowerCase());
		}
	}

	public static void setGlobalLoggingLevel(String loggerName, Level loggingLevel)
	{
		SingleLogger singleLogger = getLogger(loggerName);
		singleLogger.parentLogger.setLevel(loggingLevel);
		singleLogger.consoleHandler.setLevel(loggingLevel);
		singleLogger.fileHandler.setLevel(loggingLevel);
	}

	public static void setConsoleLoggingLevel(String loggerName, Level loggingLevel)
	{
		SingleLogger singleLogger = getLogger(loggerName);
		singleLogger.consoleHandler.setLevel(loggingLevel);
	}

	public static void setFileLoggingLevel(String loggerName, Level loggingLevel)
	{
		SingleLogger singleLogger = getLogger(loggerName);
		singleLogger.fileHandler.setLevel(loggingLevel);
	}

	/*
	public void logFrom(String loggerName, Level level, String message)
	{
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[stackTraceElements.length - 1];
		log(level, caller.toString() + ": " + message);
	}*/

	public static void log(String loggerName, Level level, String message)
	{
		SingleLogger singleLogger = getLogger(loggerName);
		singleLogger.parentLogger.log(level, "{" + loggerName + "} " + message);
	}

	public static void log(String loggerName, Throwable error)
	{
		log(loggerName, error, null);
	}

	public static void log(String loggerName, Throwable error, String note)
	{
		SingleLogger singleLogger = getLogger(loggerName);
		if (note != null)
		{
			note = "{" + loggerName + "} " + note;
		}
		singleLogger.parentLogger.log(Level.SEVERE, note, error);
	}

}
