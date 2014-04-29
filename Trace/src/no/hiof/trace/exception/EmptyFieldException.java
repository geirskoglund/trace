package no.hiof.trace.exception;

@SuppressWarnings("serial")
public class EmptyFieldException extends Exception
{
	public EmptyFieldException(String message)
	{
		super(message);
	}
}