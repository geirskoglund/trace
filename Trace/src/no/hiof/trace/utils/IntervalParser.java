package no.hiof.trace.utils;

import java.sql.Timestamp;

import android.content.ContentValues;
import android.database.Cursor;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.values.ColumnName;

/**
 * A class for parsing a Cursor to an interval and from an Interval to ContentValues
 * @author Trace Inc.
 *
 */
public class IntervalParser 
{
	/**
	 * Parses a Cursor-object to an Interval-object
	 * @param cursor the cursor to parse from
	 * @return an Interval-object containing data parsed from the input Cursor
	 */
	public static Interval parse(Cursor cursor)
	{
		int id = cursor.getColumnIndex(ColumnName.ID);
		int startTime =  cursor.getColumnIndex(ColumnName.START_TIME);
		int elapsedSeconds = cursor.getColumnIndex(ColumnName.ELAPSED_SECONDS);
		int taskId = cursor.getColumnIndex(ColumnName.TASK_ID);
		int autoRegister = cursor.getColumnIndex(ColumnName.AUTO_REG);
		
		Interval interval = new Interval();
		interval.setId(cursor.getLong(id));
		
		if(!cursor.isNull(startTime))
			interval.setStartTime(Timestamp.valueOf(cursor.getString(startTime)));
		if(!cursor.isNull(elapsedSeconds))
			interval.setElapsedSeconds(cursor.getLong(elapsedSeconds));
		if(!cursor.isNull(taskId))
			interval.setTaskId(cursor.getLong(taskId));
		if(!cursor.isNull(autoRegister))
			interval.setAutoRegistered(cursor.getInt(autoRegister)>0);
		
		return interval;
	}
	
	/**Parses an Interval-object to a ContentValues-object
	 * @param interval the Interval-object to parse
	 * @return a ContentValues-object parsed from the input Interval-object
	 */
	public static ContentValues getContentValues(Interval interval)
	{
		ContentValues values = new ContentValues();
		
		if(!interval.isIdle())
			values.put(ColumnName.START_TIME, DateHelper.getDateTime(interval.getStartTime()));
		if(interval.isCompleted())
			values.put(ColumnName.ELAPSED_SECONDS, interval.getElapsedSeconds());
		values.put(ColumnName.TASK_ID, interval.getTaskId());
		values.put(ColumnName.AUTO_REG, interval.isAutoRegistered());
		
		return values;
	}
}
