package no.hiof.trace.db.model;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;

public class Task
{
	private long id=0;
	private long planId=0;
	private String name="";
	private String description="";
	private String status;
	private List<Interval> intervals = new ArrayList<Interval>();
	
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
	
//	public Task(String name, String description, String status)
//	{
//		this.name = name;
//		this.description = description;
//		this.status = status;
//	}
//	
//	public Task(String name, String description, String status, ArrayList<Interval> intervals)
//	{
//		this.name = name;
//		this.description = description;
//		this.status = status;
//		this.intervals = intervals;
//	}

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

	public List<Interval> getIntervals() 
	{
		return TraceApp.database().getIntervals(this.id);
	}

	public void setIntervals(ArrayList<Interval> intervals) {
		this.intervals = intervals;
	}

	public void addInterval(Interval interval)
	{
		intervals.add(interval);
	}
	
	public void addIntervals(ArrayList<Interval> intervals)
	{
		this.intervals.addAll(intervals);
	}
	
	public boolean isOpen()
	{
		return status.equals("Open");
	}
	
	public String toString()
	{
		return String.format("Id:%s Name:%s Intervals:%s", id, name, intervals.size());
	}
	
	public Plan getPlan()
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getPlan(this.planId);
	}
	
	public boolean existsInDatabase()
	{
		return this.id > 0;
	}
	
}
