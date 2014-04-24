package no.hiof.trace.utils;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.database.Cursor;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.values.ColumnName;

public class IntervalParser 
{
	public static Interval parse(Cursor cursor)
	{
		int id = cursor.getColumnIndex(ColumnName.ID);
		int startTime =  cursor.getColumnIndex(ColumnName.START_TIME);
		int endTime = cursor.getColumnIndex(ColumnName.END_TIME);
		int taskId = cursor.getColumnIndex(ColumnName.TASK_ID);
		
		Interval interval = new Interval();
		interval.setId(cursor.getLong(id));
		
		if(!cursor.isNull(startTime))
			interval.setStartTime(Timestamp.valueOf(cursor.getString(startTime)));
		if(!cursor.isNull(endTime))
			interval.setEndTime(Timestamp.valueOf(cursor.getString(endTime)));
		if(!cursor.isNull(taskId))
			interval.setTaskId(cursor.getLong(taskId));
		
		return interval;
	}
	
	public static ContentValues getContentValues(Interval interval)
	{
		ContentValues values = new ContentValues();
		
		if(!interval.isIdle())
			values.put(ColumnName.START_TIME, DateHelper.getDateTime(interval.getStartTime()));
		if(interval.isCompleted())
			values.put(ColumnName.END_TIME, DateHelper.getDateTime(interval.getEndTime()));
		values.put(ColumnName.TASK_ID, interval.getTaskId());
		
		return values;
	}
}
