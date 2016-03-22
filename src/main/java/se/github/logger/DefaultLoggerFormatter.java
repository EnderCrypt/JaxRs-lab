package se.github.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DefaultLoggerFormatter extends Formatter
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
}
