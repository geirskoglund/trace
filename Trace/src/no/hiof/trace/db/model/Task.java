package no.hiof.trace.db.model;

import java.util.ArrayList;

public class Task
{
	private int id;
	private String name;
	private String description;
	private String status;
	private ArrayList<Interval> intervals;
	
	public Task(){}
	
	public Task(String name, String description, String status)
	{
		this.name = name;
		this.description = description;
		this.status = status;
	}
	
	public Task(String name, String description, String status, ArrayList<Interval> intervals)
	{
		this.name = name;
		this.description = description;
		this.status = status;
		this.intervals = intervals;
	}

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<Interval> getIntervals() {
		return intervals;
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
	
	public String toString()
	{
		return String.format("Id:%s Name:%s Intervals:%s", id, name, intervals.size());
	}
	
}
