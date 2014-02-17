package no.hiof.trace.db.model;

import java.util.Date;

public class Interval {

	private int id;
	private Date startTime;
	private Date endTime;
	
	public Interval(){}
	
	public Interval(int id, Date startTime)
	{
		this.startTime = startTime;
	}
	
	public Interval(int id, Date starTime, Date endTime)
	{
		this.id = id;
		this.startTime = starTime;
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
