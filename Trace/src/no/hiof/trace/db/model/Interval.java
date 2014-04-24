package no.hiof.trace.db.model;

import java.util.Date;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.utils.TimeSlot;

public class Interval 
{
	private long id;
	private Date startTime = null;
	private Date endTime = null;
	private long taskId;
	
	public Interval(){}
	
	public Interval(long taskId)
	{
		this.taskId = taskId;
	}
	
	public Interval(long taskId, Date startTime)
	{
		this.taskId = taskId;
		this.startTime = startTime;
	}

	public void setTaskId(long taskId)
	{
		this.taskId = taskId;
	}
	public long getTaskId()
	{
		return this.taskId;
	}
	public Task getTask()
	{
		return TraceApp.database().getTask(this.taskId);
	}
	
	public long getId() 
	{
		return this.id;
	}
	public void setId(long id)
	{
		this.id=id;
	}
	
	public Date getStartTime() 
	{
		return this.startTime;
	}
	public void setStartTime(Date date)
	{
		this.startTime = date;
	}
	
	public void start() 
	{
		if(isIdle()) this.startTime = new Date();
	}

	public Date getEndTime() 
	{
		return this.endTime;
	}
	public void setEndTime(Date date)
	{
		this.endTime = date;
	}
	
	public void stop() 
	{
		if(isRunning()) this.endTime = new Date();
	}

	public boolean isRunning() 
	{
		return wasStarted() && !wasStopped();
	}
	
	public boolean isCompleted()
	{
		return wasStarted() && wasStopped();
	}
	
	public boolean isIdle()
	{
		return (!wasStarted());
	}
	
	private boolean wasStarted()
	{
		return this.startTime!=null;
	}
	
	private boolean wasStopped()
	{
		return this.endTime!=null;
	}
	
	public TimeSlot getElapsedTime()
	{
		if(isIdle())
			return new TimeSlot(new Date(), new Date());		
		else if(isCompleted())
			return new TimeSlot(this.startTime, this.endTime);
		else
			return new TimeSlot(this.startTime, new Date());
	}
		
}
