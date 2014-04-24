package no.hiof.trace.utils;

import java.util.Date;

public class TimeSlot 
{
	int minutes;
	Date older; 
	Date newer;
	
	public TimeSlot(Date older, Date newer)
	{
		this.older = older;
		this.newer = newer;
		setMinutes();
	}
	
	private void setMinutes()
	{
		minutes = (int)( (newer.getTime() - older.getTime()) / (1000 * 60) );
	}
	
	public int getHours()
	{
		return (int) minutes/60;
	}
	
	public int getMinutes()
	{
		return (int) minutes%60;
	}
	
	public int getElapsedTimeInMinutes()
	{
		return minutes;
	}
}
