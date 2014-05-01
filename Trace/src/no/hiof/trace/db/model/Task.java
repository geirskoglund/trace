package no.hiof.trace.db.model;

import java.util.List;
import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.utils.TimeSlot;

/**
 * @author Trace Inc.
 * Class representing the Task entity from the data model.
 */
public class Task
{
	private long id=0;
	private long planId=0;
	private String name="";
	private String description="";
	private String status;
	
	public Task()
	{
		setDefaults();
	}
	
	public Task(long planId)
	{
		this.planId = planId;
		setDefaults();
	}
	
	private void setDefaults()
	{
		String status = TraceApp.getAppContext().getString(R.string.status_open);
		this.status = status;
	}

	public long getId() 
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	
	public long getPlanId() 
	{
		return planId;
	}
	public void setPlanId(long planId)
	{
		this.planId = planId;
	}
	
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getDescription() 
	{
		return description;
	}
	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getStatus() 
	{
		return status;
	}
	public void setStatus(String status) 
	{
		this.status = status;
	}

	/**
	 * @return a List containing all Intervals for this Task
	 */
	public List<Interval> getIntervals() 
	{
		return TraceApp.database().getIntervals(this.id);
	}
	
	/**
	 * @return A TimeSlot aggregating the elapsed seconds of all Intervals
	 */
	public TimeSlot getAggregatedTimeSlots()
	{
		return new TimeSlot(TraceApp.database().getAggregatedTimeSlots(this.id));
	}
	
	/**
	 * @return True if the Status is "Open"
	 */
	public boolean isOpen()
	{
		return status.equals("Open");
	}
	
	@Override
	public String toString()
	{
		return String.format("%s: %s", name, description);
	}
	
	/**
	 * @return The Plan this task belongs to
	 */
	public Plan getPlan()
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getPlan(this.planId);
	}
	
	/**
	 * @return True if the Task exists in the database
	 */
	public boolean existsInDatabase()
	{
		return this.id > 0;
	}
	
}
