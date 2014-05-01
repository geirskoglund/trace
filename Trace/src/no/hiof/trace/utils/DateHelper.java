package no.hiof.trace.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A helper class for converting dates and differences in dates
 * @author Trace Inc.
 *
 */
public class DateHelper 
{
	/**
	 * Converts a Date-object to a human friendly string format
	 * @param date the date to convert
	 * @return the date in a human friendly format
	 */
	public static String getDateTime(Date date) 
	{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
	}
	
	/**
	 * Gets the difference between two dates in seconds
	 * @param newest the newest date
	 * @param oldest the oldest date
	 * @return the difference in seconds
	 */
	public static long getDiffInSeconds(Date newest, Date oldest)
	{
		return (newest.getTime() - oldest.getTime()) / 1000;
	}
	
	/**
	 * Converts a Date to a SimpleDateFormat
	 * @param date the Date to convert
	 * @return the converted Date as a SimpleDateFormat-object
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateString(Date date)
	{
		return new SimpleDateFormat("yyyy-MM-dd, HH:mm").format(date);
	}
}
