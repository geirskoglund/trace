package no.hiof.trace.utils;

import android.content.ContentValues;
import android.database.Cursor;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.db.values.ColumnName;

/**
 * @author Trace Inc.
 *A class used to parse Tasks to ContentValue and from a Cursor
 */
public class TaskParser 
{
	/**
	 * Parses Tasks from a Cursor object
	 * @param cursor the cursor containing the data to be parsed
	 * @return the parsed Task
	 */
	public static Task parse(Cursor cursor)
	{
		int id = cursor.getColumnIndex(ColumnName.ID);
		int name = cursor.getColumnIndex(ColumnName.NAME);
		int description = cursor.getColumnIndex(ColumnName.DESCRIPTION);
		int planId = cursor.getColumnIndex(ColumnName.PLAN_ID);
		int status = cursor.getColumnIndex(ColumnName.STATUS);
		
		Task task = new Task();
		task.setId(cursor.getLong(id));
		task.setName(cursor.getString(name));
		task.setDescription(cursor.getString(description));
		task.setPlanId(cursor.getLong(planId));
		task.setStatus(cursor.getString(status));
		
		return task;
	}
	
	/**
	 * Gets the ContentValues of a Task
	 * @param task the Task to get ContentValues from.
	 * @return ContentValues parsed from a Task
	 */
	public static ContentValues getContentValues(Task task)
	{
		ContentValues values = new ContentValues();
		
		values.put(ColumnName.NAME, task.getName());
		values.put(ColumnName.DESCRIPTION, task.getDescription());
		values.put(ColumnName.PLAN_ID, task.getPlanId());
		values.put(ColumnName.STATUS, task.getStatus());
		
		return values;
	}
}
