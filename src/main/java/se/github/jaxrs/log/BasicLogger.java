package se.github.jaxrs.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BasicLogger
{
	private Logger logger;
	private FileHandler fileHandler;

	public BasicLogger(String name)
	{
		logger = Logger.getLogger(name);
		try
		{
			fileHandler = new FileHandler("Logs/" + name + ".log", true);
			setFormatter();
			fileHandler.setLevel(Level.ALL);
			logger.addHandler(fileHandler);
		}
		catch (SecurityException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void setFormatter()
	{
		fileHandler.setFormatter(new Formatter()
		{
			@Override
			public String format(LogRecord record)
			{
				final String message = record.getMessage();
				StringBuilder sb = new StringBuilder();
				// add time
				String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
				sb.append(time + " ");
				// add level
				sb.append("[" + record.getLevel().getName() + "] ");
				// add message
				Throwable error = record.getThrown();
				if (error == null)
				{
					sb.append(message + '\n');
				}
				else
				{
					if (record.getMessage() != null)
					{
						sb.append("(\"" + message + "\") ");
					}
					StringWriter sw = new StringWriter();
					error.printStackTrace(new PrintWriter(sw));
					sb.append(sw.toString());
				}
				// return
				return sb.toString();
			}
		});
	}

	public void doConsoleOutput(boolean value)
	{
		logger.setUseParentHandlers(value);
	}

	public void setLoggingLevel(Level loggingLevel)
	{
		logger.setLevel(loggingLevel);
	}

	public Level getLoggingLevel()
	{
		return logger.getLevel();
	}

	public void log(Level level, String message)
	{
		logger.log(level, message);
	}

	public void log(Throwable error)
	{
		log(error, null);
	}

	public void log(Throwable error, String note)
	{
		logger.log(Level.SEVERE, note, error);
	}
}
