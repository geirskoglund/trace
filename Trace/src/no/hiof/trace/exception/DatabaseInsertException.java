package no.hiof.trace.exception;

@SuppressWarnings("serial")
public class DatabaseInsertException extends Exception
{
	public DatabaseInsertException(String message)
	{
		super(message);
	}
}
