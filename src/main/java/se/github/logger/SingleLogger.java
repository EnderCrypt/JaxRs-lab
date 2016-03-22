package se.github.logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleLogger
{
	private String name;
	private File logfile;
	protected Logger parentLogger;
	protected ConsoleHandler consoleHandler;
	protected FileHandler fileHandler;

	protected SingleLogger(String name)
	{
		this.name = name;

		// logger
		parentLogger = Logger.getLogger(name);
		parentLogger.setUseParentHandlers(false);

		//console handler
		consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new DefaultLoggerFormatter());

		//file handler
		try
		{
			File logFolder = new File("/Users/MelEnt/Desktop/" + name);
			logFolder.mkdirs();
			logfile = new File(logFolder + "/" + generateFilename() + ".log");
			fileHandler = new FileHandler(logfile.toString());
			fileHandler.setFormatter(new DefaultLoggerFormatter());

		}
		catch (SecurityException | IOException e)
		{
			throw new RuntimeException(e);
		}

		// add handlers
		parentLogger.addHandler(consoleHandler);
		parentLogger.addHandler(fileHandler);

		// set levels
		parentLogger.setLevel(Level.ALL);
		consoleHandler.setLevel(Level.ALL);
		fileHandler.setLevel(Level.ALL);
	}

	private String generateFilename()
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH_mm");
		Date today = Calendar.getInstance().getTime();
		String dateString = df.format(today);
		return dateString;
	}
}
