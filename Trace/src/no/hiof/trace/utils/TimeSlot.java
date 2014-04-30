package no.hiof.trace.utils;

import java.util.Date;

public class TimeSlot 
{
	final int MIN = 60;
	final int HOUR = MIN * 60;
	
	long totalSeconds;
	int seconds;
	int minutes;
	int hours;
	Date older; 
	Date newer;
		
	public TimeSlot(long seconds)
	{
		this.hours = (int) seconds/HOUR;
		this.minutes = (int) (seconds%HOUR) / MIN;
		this.seconds = (int) (seconds%HOUR) % MIN;
	}
	
	public int getHours()
	{
		return hours;
	}
	
	public int getMinutes()
	{
		return minutes;
	}
	
	public int getSeconds()
	{
		return seconds;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s:%s:%s", leadingZero(hours), leadingZero(minutes), leadingZero(seconds));
	}
	
	private String leadingZero(int number)
	{
		if(number<10)
			return "0"+number;
		else
			return ""+number;
					
	}
}
