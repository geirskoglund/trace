package no.hiof.trace.utils;

import java.sql.Timestamp;

import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.values.ColumnName;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Trace Inc.
 *A class for parsing a Plan from a Cursor and from a Plan to ContentValues
 */
public class PlanParser 
{

	/**
	 * Parses a Cursor-object to a Plan-object
	 * @param cursor the Cursor to parse
	 * @return the parsed Plan
	 */
	public static Plan parse(Cursor cursor)
	{
		int id = cursor.getColumnIndex(ColumnName.ID);
		int name = cursor.getColumnIndex(ColumnName.NAME);
		int description = cursor.getColumnIndex(ColumnName.DESCRIPTION);
		int ssid = cursor.getColumnIndex(ColumnName.SSID);
		int nfc = cursor.getColumnIndex(ColumnName.NFC);
		int lon = cursor.getColumnIndex(ColumnName.LONG);
		int lat = cursor.getColumnIndex(ColumnName.LAT);
		int lastActivated = cursor.getColumnIndex(ColumnName.LAST_ACTIVATED);
		int autoRegister = cursor.getColumnIndex(ColumnName.AUTO_REG);
		int status = cursor.getColumnIndex(ColumnName.STATUS);
		int autoTrigger = cursor.getColumnIndex(ColumnName.AUTO_TRIGGER);
		int primaryTask = cursor.getColumnIndex(ColumnName.PRIMARY_TASK);
		
		Plan plan = new Plan();
		plan.setId(cursor.getLong(id));
		plan.setName(cursor.getString(name));
		plan.setDescription(cursor.getString(description));
		plan.setSsid(cursor.getString(ssid));
		plan.setNfc(cursor.getString(nfc));
		plan.setLon(cursor.getDouble(lon));
		plan.setLat(cursor.getDouble(lat));
		plan.setAutoRegister(cursor.getInt(autoRegister)>0);
		plan.setStatus(cursor.getString(status));
		plan.setAutoTrigger(cursor.getString(autoTrigger));
		plan.setLastActivatedTimestamp(Timestamp.valueOf(cursor.getString(lastActivated)));
		
		if (!cursor.isNull(primaryTask))
			plan.setPrimaryTaskId(cursor.getLong(primaryTask));
		
		Log.d("TRACE-PP",plan.getName() + ": Timestamp: " + plan.getLastActivatedTimestamp());
		
		return plan;
	}
	
	/**
	 * parses a Plan-object to a ContentValues-object.
	 * @param plan the plan to parse
	 * @return a ContentValues-object parsed from input Plan
	 */
	public static ContentValues getContentValues(Plan plan)
	{
		ContentValues values = new ContentValues();
		
		values.put(ColumnName.NAME, plan.getName());
		values.put(ColumnName.DESCRIPTION, plan.getDescription());
		values.put(ColumnName.SSID, plan.getSsid());
		values.put(ColumnName.NFC, plan.getNfc());
		values.put(ColumnName.LAT, plan.getLat());
		values.put(ColumnName.LONG, plan.getLon());
		values.put(ColumnName.AUTO_REG, plan.getAutoRegister());
		values.put(ColumnName.STATUS, plan.getStatus());
		values.put(ColumnName.AUTO_TRIGGER, plan.getAutoTrigger());
		values.put(ColumnName.LAST_ACTIVATED, DateHelper.getDateTime(plan.getLastActivatedTimestamp()));
		
		if(plan.hasPrimaryTask())
			values.put(ColumnName.PRIMARY_TASK, plan.getPrimaryTaskId());
		else
			values.put(ColumnName.PRIMARY_TASK, 0);
		
		return values;
	}
}
