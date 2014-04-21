package no.hiof.trace.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.values.ColumnName;

public class PlanParser 
{

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
		
		Log.d("TRACE-PP",plan.getName() + ": Timestamp: " + plan.getLastActivatedTimestamp());
		
		return plan;
	}
	
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
		values.put(ColumnName.LAST_ACTIVATED, getDateTime(plan.getLastActivatedTimestamp()));
		
		return values;
	}
	
	private static String getDateTime(Date date) 
	{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
	}
}
