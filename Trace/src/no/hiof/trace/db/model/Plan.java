package no.hiof.trace.db.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;

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
	//private Task primaryTask;
	private ArrayList<Task> tasks;
	private String autoTrigger = "";
	
	public Plan()
	{
		String status = TraceApp.getAppContext().getString(R.string.status_open);
		this.status = status;
	}
	
	public Plan(String name, String description, boolean autoRegister)
	{
		this.name = name;
		this.description = description;
		this.autoRegister = autoRegister;
		
		String status = TraceApp.getAppContext().getString(R.string.status_open);
		this.status = status;
		
	}
	
	public Plan(String name, String description, boolean autoRegister, ArrayList<Task> tasks)
	{
		this.name = name;
		this.description = description;
		this.autoRegister = autoRegister;
		this.tasks = tasks;
	}
	
//	public Plan(long id, String name, String description, String ssid,
//			String nfc, double lat, double lon, boolean autoRegister,
//			String status, Task primaryTask, ArrayList<Task> tasks){
//		this.id = id;
//		this.name = name;
//		this.description = description;
//		this.ssid = ssid;
//		this.nfc = nfc;
//		this.lat = lat;
//		this.lon = lon;
//		this.autoRegister = autoRegister;
//		this.status = status;
//		this.primaryTask = primaryTask;
//		this.tasks = tasks;
//	}

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
	
	public Task getPrimaryTask() 
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		if (this.hasPrimaryTask())
			return database.getTask(this.primaryTaskId);
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

	public void setPrimaryTask(Task task) 
	{
		if(task == null)
			throw new IllegalArgumentException("Task was set to null");
		
		if(task.getId()==0)
			return;
		//	throw new IllegalArgumentException("The provided task does not exist");
		
		this.primaryTaskId = task.getId();
		TraceApp.database().updatePlan(this);
	}

	public List<Task> getTasks() 
	{
		DatabaseManager database = new DatabaseManager(TraceApp.getAppContext());
		return database.getTasks(this.id);
	}
	
	public void addTask(Task task)
	{
		tasks.add(task);
	}
	
	public void addTasks(ArrayList<Task> tasks)
	{
		this.tasks.addAll(tasks);
	}
	
	//Should only be used for debugging
	public String toString()
	{
		return String.format("%s: %s", name, description);
	}

	@Override
	public int compareTo(Plan other) 
	{
		return this.getLastActivatedTimestamp().compareTo(other.getLastActivatedTimestamp());
	}
}
