package no.hiof.trace.db.model;

import java.util.Date;
import java.util.List;
import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;

/**
 * @author Trace Inc.
 * Class representing the Plan entity from the data model.
 */
public class Plan implements Comparable<Plan> 
{
	private long id;
	private String name="";
	private String description="";
	private String ssid="";
	private String nfc="";
	private double lat = 0;
	private double lon = 0;
	private boolean autoRegister = false;
	private String status="";
	private Date lastActivated;
	private long primaryTaskId;
	private String autoTrigger = "";
	
	public Plan()
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
		if(description==null)
			return "";
		
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getSsid() 
	{
		if(ssid==null)
			return "";
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getNfc() {
		if(nfc==null)
			return "";
		return nfc;
	}

	public void setNfc(String nfc) {
		this.nfc = nfc;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public boolean getAutoRegister() {
		return autoRegister;
	}

	public void setAutoRegister(boolean autoRegister) {
		this.autoRegister = autoRegister;
	}

	public String getStatus() {
		if(status==null)
			return "";
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getLastActivatedTimestamp()
	{
		if(lastActivated!=null)
			return lastActivated;
		else
			return new Date();
	}
	
	public void setLastActivatedTimestamp(Date timestamp)
	{
		lastActivated = timestamp;
	}
	
	public String getAutoTrigger()
	{
		if(autoTrigger==null)
			return "";
		return autoTrigger;
	}
	
	public void setAutoTrigger(String trigger)
	{
		this.autoTrigger = trigger;
	}
	
	public long getPrimaryTaskId()
	{
		return this.primaryTaskId;
	}
	public void setPrimaryTaskId(long taskId)
	{
		this.primaryTaskId = taskId;
	}
	
	public boolean hasPrimaryTask()
	{
		return primaryTaskId > 0;
	}
	
	/**
	 * @return the primary Task for this Plan. If no primary task is set, the first task found
	 * is set as primary. If no task can be found, a blank Task is created and returned.
	 */
	public Task getPrimaryTask() 
	{
		if (this.hasPrimaryTask())
			return TraceApp.database().getTask(this.primaryTaskId);
		else if(this.getId()>0)
		{
			List<Task> tasks = this.getTasks();
			if(!tasks.isEmpty())
			{
				this.setPrimaryTask(tasks.get(0));
				return this.getPrimaryTask();
			}
		}
		
		return new Task();
	}

	/**
	 * @param task Sets a new primary task and writes it to the database
	 */
	public void setPrimaryTask(Task task) 
	{
		if(task == null)
			throw new IllegalArgumentException("Task was set to null");
		
		if(task.getId()==0)
			return;
		
		this.primaryTaskId = task.getId();

		TraceApp.database().writeToDatabase(this);
	}
	
	public void removePrimaryTask()
	{
		this.primaryTaskId = 0;
	}

	/**
	 * @return a List containing all Tasks for this Plan
	 */
	public List<Task> getTasks() 
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getTasks(this.id);
	}
	
	/**
	 * @return True if Task status is "Open"
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

	/* 
	 * Allows sorting a list of plans, based on activation date
	 */
	@Override
	public int compareTo(Plan other) 
	{
		return this.getLastActivatedTimestamp().compareTo(other.getLastActivatedTimestamp());
	}
	
	/**
	 * @return True if the Plan exists in the database
	 */
	public boolean existsInDatabase()
	{
		return this.id > 0;
	}
	
	/**
	 * Sets the plan as the current plan and updates database
	 */
	public void setAsCurrent()
	{
		if (this.existsInDatabase())
		{
			DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
			this.setLastActivatedTimestamp(new Date());
			database.writeToDatabase(this);
		}
	}
}
