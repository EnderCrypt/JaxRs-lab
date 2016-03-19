package se.github.springlab.exception;

public class InvalidUserException extends RuntimeException
{

	public InvalidUserException(String message)
	{
		super(message);
	}

	public InvalidUserException(Throwable cause)
	{
		super(cause);
	}

}
