package no.hiof.trace.utils;

import java.util.Date;

/**
 * @author  Trace Inc.
 * A class for keeping time slots. Based on seconds to get hours, minutes and seconds elapsed.
 */
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
		
	/**
	 * Constructor for TimeSlot
	 * @param seconds the elapsed time of the time slot
	 */
	public TimeSlot(long seconds)
	{
		this.hours = (int) seconds/HOUR;
		this.minutes = (int) (seconds%HOUR) / MIN;
		this.seconds = (int) (seconds%HOUR) % MIN;
	}
	
	/**
	 * Example 5482 seconds = 1H, 31M, 22S
	 * @return the amount of hours of the time slot
	 */
	public int getHours()
	{
		return hours;
	}
	
	/**
	 * Example 5482 seconds = 1H, 31M, 22S
	 * @return the amount of minutes of the time slot.
	 */
	public int getMinutes()
	{
		return minutes;
	}
	
	/**
	 * Example 5482 seconds = 1H, 31M, 22S
	 * @return the amount of seconds in the time slot
	 */
	public int getSeconds()
	{
		return seconds;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("%s:%s:%s", leadingZero(hours), leadingZero(minutes), leadingZero(seconds));
	}
	
	/**
	 * Adds a leading zero to a number if it is lower than 10
	 * @param number the number to add a leading zero to
	 * @return the input number with or without a leading zero added
	 */
	private String leadingZero(int number)
	{
		if(number<10)
			return "0"+number;
		else
			return ""+number;
					
	}
}
