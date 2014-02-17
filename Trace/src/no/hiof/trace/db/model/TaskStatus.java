package no.hiof.trace.db.model;

public class TaskStatus
{
	public static final String OPEN = "Open";
	public static final String CLOSED = "Closed";
	
	private String statusName;
	
	public TaskStatus() {}
	
	public TaskStatus(String status)
	{
		this.statusName = status;
	}
	
	public String getStatus()
	{
		return statusName;
	}

	public void setStatus(String status)
	{
		this.statusName = status;
	}
}
