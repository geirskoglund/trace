package no.hiof.trace.utils;

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
}
