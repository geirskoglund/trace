package no.hiof.trace.db.model;

import java.util.ArrayList;

public class Plan
{
	private int id;
	private String name;
	private String description;
	private String ssid;
	private String nfc;
	private double lat = 0;
	private double lon = 0;
	private boolean autoRegister = false;
	private String status;
	private Task primaryTask;
	private ArrayList<Task> tasks;
	
	public Plan(){}
	
	public Plan(String name, String description, boolean autoRegister)
	{
		this.name = name;
		this.description = description;
		this.autoRegister = autoRegister;
		
	}
	
	public Plan(String name, String description, boolean autoRegister, ArrayList<Task> tasks)
	{
		this.name = name;
		this.description = description;
		this.autoRegister = autoRegister;
		this.tasks = tasks;
	}
	
	public Plan(int id, String name, String description, String ssid,
			String nfc, double lat, double lon, boolean autoRegister,
			String status, Task primaryTask, ArrayList<Task> tasks){
		this.id = id;
		this.name = name;
		this.description = description;
		this.ssid = ssid;
		this.nfc = nfc;
		this.lat = lat;
		this.lon = lon;
		this.autoRegister = autoRegister;
		this.status = status;
		this.primaryTask = primaryTask;
		this.tasks = tasks;
	}

	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getNfc() {
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
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Task getPrimaryTask() {
		return primaryTask;
	}

	public void setPrimaryTask(Task primaryTask) {
		this.primaryTask = primaryTask;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
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
		return String.format("Id:%s  Name:%s", id, name);
	}
	
}
