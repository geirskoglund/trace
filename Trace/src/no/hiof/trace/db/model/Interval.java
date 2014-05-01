package no.hiof.trace.db.model;

import java.util.Date;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.utils.DateHelper;
import no.hiof.trace.utils.TimeSlot;

/**
 * @author Trace Inc.
 * Class representing the Interval entity from the data model.
 */
public class Interval 
{
	private long id;
	private Date startTime = null;
	private long elapsedSeconds = 0;
	private long taskId;
	
	private boolean autoRegister = false;
	
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

	public void setAutoRegistered(boolean auto)
	{
		this.autoRegister = auto;
	}
	public boolean isAutoRegistered()
	{
		return autoRegister;
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
	
	/**
	 * Starts a new registration on an idle Interval
	 */
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
	
	/**
	 * Calculates and stores elapsed seconds based on the current time, if time registration is started.
	 */
	public void stop() 
	{
		if(!isRunning())
			return;
		
		Date endTime = new Date();
		long elapsed = DateHelper.getDiffInSeconds(endTime, this.startTime);
		
		stop(elapsed);
	}

	/**
	 * @param elapsedSeconds The number of seconds for this registration
	 * Stores elapsed seconds, if time registration is started.
	 */ 
	public void stop(long elapsedSeconds) 
	{
		if(isRunning()) 
			this.elapsedSeconds = elapsedSeconds;
	}
	
	/**
	 * @return True if registration is started but not completed.
	 */
	public boolean isRunning() 
	{
		return wasStarted() && !wasStopped();
	}
	
	/**
	 * @return True if registration is complete
	 */
	public boolean isCompleted()
	{
		return wasStarted() && wasStopped();
	}
	
	/**
	 * @return True if the registration is not started
	 */
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
	
	/**
	 * @return a TimeSlot showing the elapsed time in hours, minutes and seconds.
	 * Will return a TimeSlot even if registration is not started or not complete.
	 */
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
