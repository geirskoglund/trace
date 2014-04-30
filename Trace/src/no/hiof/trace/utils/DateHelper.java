package no.hiof.trace.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper 
{
	public static String getDateTime(Date date) 
	{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
	}
	
	public static long getDiffInSeconds(Date newest, Date oldest)
	{
		return (newest.getTime() - oldest.getTime()) / 1000;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getDateString(Date date)
	{
		return new SimpleDateFormat("yyyy-MM-dd, HH:mm").format(date);
	}
}
