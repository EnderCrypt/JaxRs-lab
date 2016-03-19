package se.github.springlab.exception;

public class InvalidItemException extends RuntimeException
{

	public InvalidItemException(String message)
	{
		super(message);
	}

	public InvalidItemException(Throwable cause)
	{
		super(cause);
	}

}
