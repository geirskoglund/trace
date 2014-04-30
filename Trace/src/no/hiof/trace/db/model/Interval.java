package no.hiof.trace.db.model;

import java.util.Date;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.utils.DateHelper;
import no.hiof.trace.utils.TimeSlot;

public class Interval 
{
	private long id;
	private Date startTime = null;
	private long elapsedSeconds = 0;
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
	
	public long getElapsedSeconds()
	{
		return this.elapsedSeconds;
	}
	
	public void setElapsedSeconds(long elapsedSeconds)
	{
		this.elapsedSeconds = elapsedSeconds;
	}
	
	public void stop() 
	{
		if(!isRunning())
			return;
		
		Date endTime = new Date();
		long elapsed = DateHelper.getDiffInSeconds(endTime, this.startTime);
		
		stop(elapsed);
	}

	public void stop(long elapsedSeconds) 
	{
		if(isRunning()) 
			this.elapsedSeconds = elapsedSeconds;
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
		return this.elapsedSeconds > 0;
	}
	
	public TimeSlot getElapsedTime()
	{
		if(isIdle())
			return new TimeSlot(0);		
		else if(isCompleted())
			return new TimeSlot(this.elapsedSeconds);
		else
			return new TimeSlot(DateHelper.getDiffInSeconds(new Date(), startTime));
	}
		
}
