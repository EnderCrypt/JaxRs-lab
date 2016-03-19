package se.github.springlab.exception;

public class InvalidTeamException extends RuntimeException
{
	public InvalidTeamException(String message)
	{
		super(message);
	}

	public InvalidTeamException(Throwable cause)
	{
		super(cause);
	}

}
