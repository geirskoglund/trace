package no.hiof.trace.db;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.PlanStatus;
import no.hiof.trace.db.model.TaskStatus;
import no.hiof.trace.db.model.definitions.C;
import no.hiof.trace.db.model.definitions.G;
import no.hiof.trace.db.model.definitions.T;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

public class DatabaseManager extends SQLiteOpenHelper
{	
	public DatabaseManager(Context context)
	{
		super(context,G.DATABASE_NAME,null,G.DATABASE_VERSION);
		Log.d("DM","Opprettet");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		//createTables(T.CREATE_TABLE_PLAN_STATUS, T.CREATE_TABLE_TASK_STATUS, T.CREATE_TABLE_INTERVAL, T.CREATE_TABLE_TASK, T.CREATE_TABLE_PLAN );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//Drops all tables at this point. Should fetch all data, parse and write to new database.
		//dropTables(T.PLAN, T.TASK, T.INTERVAL, T.PLAN_STATUS, T.TASK_STATUS);
		//this.onCreate(db);
	}
	
	public void addPlan(Plan plan)
	{
		if(plan == null)
			throw new IllegalArgumentException();
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		if(plan.getName()!=null)
			values.put(C.NAME, plan.getName());
		
		if(plan.getDescription()!=null)
			values.put(C.DESCRIPTION, plan.getDescription());
		
		if(plan.getAutoRegister()== true)
			values.put(C.AUTO_REG, "1");
		else
			values.put(C.AUTO_REG, "0");
		
		if(plan.getNfc()!=null)
			values.put(C.NFC, plan.getNfc());
		
		if(plan.getSsid()!=null)
			values.put(C.SSID, plan.getSsid());
		
		db.beginTransaction();
		try
		{
			db.insert(T.PLAN, null, values);
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
	}
	
	public Plan getPlan(int id)
	{
		String query = "SELECT * FROM "+T.PLAN+" WHERE "+C.ID+" = "+id;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		Plan plan = null;
		if(cursor.moveToFirst())
		{
			/*int id = cursor.getColumnIndex(C.ID);
			int name = cursor.getColumnIndex(C.NAME);
			int description = cursor.getColumnIndex(C.DESCRIPTION);
			int ssid = cursor.getColumnIndex(C.SSID);
			int nfc = cursor.getColumnIndex(C.NFC);
			int lon = cursor.getColumnIndex(C.LONG);
			int lat = cursor.getColumnIndex(C.LAT);
			int autoRegister = cursor.getColumnIndex(C.AUTO_REG);
			int status = cursor.getColumnIndex(C.STATUS);
			*/
			
			plan = new Plan();
			plan.setId(cursor.getInt(0));
			if(!cursor.isNull(1))
				plan.setName(cursor.getString(1));
			if(!cursor.isNull(2))
				plan.setDescription(cursor.getString(2));
			if(!cursor.isNull(3))
				plan.setSsid(cursor.getString(3));
			if(!cursor.isNull(4))
				plan.setNfc(cursor.getString(4));
			if(!cursor.isNull(5))
				plan.setLon(cursor.getDouble(5));
			if(!cursor.isNull(6))
				plan.setLat(cursor.getDouble(6));
			if(!cursor.isNull(7))
				plan.setAutoRegister(Boolean.getBoolean(cursor.getString(7)));
			if(!cursor.isNull(8))
				plan.setSsid(cursor.getString(8));
			
			return plan;
		}
		
		return null;
	}
	
	public ArrayList<Plan> getAllPlans()
	{
		ArrayList<Plan> plans = new ArrayList<Plan>();
		String query = "SELECT * FROM "+T.PLAN;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		Plan plan = null;
		if(cursor.moveToFirst())
		{
			int id = cursor.getColumnIndex(C.ID);
			int name = cursor.getColumnIndex(C.NAME);
			int description = cursor.getColumnIndex(C.DESCRIPTION);
			int ssid = cursor.getColumnIndex(C.SSID);
			int nfc = cursor.getColumnIndex(C.NFC);
			int lon = cursor.getColumnIndex(C.LONG);
			int lat = cursor.getColumnIndex(C.LAT);
			int autoRegister = cursor.getColumnIndex(C.AUTO_REG);
			int status = cursor.getColumnIndex(C.STATUS);
			
			do
			{
				plan = new Plan();
				plan.setId(cursor.getInt(id));
				if(!cursor.isNull(name))
					plan.setName(cursor.getString(1));
				if(!cursor.isNull(description))
					plan.setDescription(cursor.getString(description));
				if(!cursor.isNull(ssid))
					plan.setSsid(cursor.getString(ssid));
				if(!cursor.isNull(nfc))
					plan.setNfc(cursor.getString(nfc));
				if(!cursor.isNull(lon))
					plan.setLon(cursor.getDouble(lon));
				if(!cursor.isNull(lat))
					plan.setLat(cursor.getDouble(lat));
				if(!cursor.isNull(autoRegister))
					plan.setAutoRegister(Boolean.getBoolean(cursor.getString(autoRegister)));
				if(!cursor.isNull(status))
					plan.setSsid(cursor.getString(status));
				
				plans.add(plan);
			}
			while(cursor.moveToNext());
		}
		
		Log.d("getAllPlans", plans.toString());
		
		return plans;
	}
	
	public void addPlanStatus(String statusName)
	{
		if(statusName == null || statusName.isEmpty())
			throw new IllegalArgumentException();
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(C.STATUS, statusName);
		
		db.beginTransaction(); //Begin a transaction
		try
		{
			db.insert(T.PLAN_STATUS, null, values);
			db.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		
	}
	
	
	
	
	public ArrayList<PlanStatus> getAllPlanStatus()
	{
		ArrayList<PlanStatus> statuses = new ArrayList<PlanStatus>();
		String query = "Select * FROM "+T.PLAN_STATUS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		PlanStatus status = null;
		if(cursor.moveToFirst())
		{
			do
			{
				status = new PlanStatus();
				status.setStatus(cursor.getString(0));
				Log.d("PlanStatus",status.getStatus());
				statuses.add(status);
			}
			while(cursor.moveToNext());
		}
		
		//Log.d("getAllPlanStatuses", statuses.toString());
		return statuses;
	}
	
	
	
	
	public void deletePlanStatus(String... limiter)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try
		{
			db.delete(T.PLAN_STATUS, C.STATUS+" = ?", limiter);
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			db.close();
		}
		Log.d("deletePlanStatus", limiter.toString());
	}
	
	
	
	
	public void addTaskStatus(String statusName)
	{
		if(statusName == null || statusName.isEmpty())
			throw new IllegalArgumentException();
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(C.STATUS, statusName);
		
		db.beginTransaction(); //Begin a transaction
		try
		{
			db.insert(T.TASK_STATUS, null, values);
			db.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		catch(Exception e) { System.out.println(e.getMessage()); }
		finally{ db.endTransaction(); }
		
		//return false;
	}
	
	
	
	
	public ArrayList<TaskStatus> getAllTaskStatus()
	{
		ArrayList<TaskStatus> statuses = new ArrayList<TaskStatus>();
		String query = "Select * FROM "+T.TASK_STATUS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		TaskStatus status = null;
		if(cursor.moveToFirst())
		{
			do
			{
				status = new TaskStatus();
				status.setStatus(cursor.getString(0));
				Log.d("TaskStatus",status.getStatus());
				statuses.add(status);
			}
			while(cursor.moveToNext());
		}
		
		Log.d("getAllTaskStatuses", statuses.toString());
		return statuses;
	}
	
	
	
	public void deleteRow(String table, String column, Object... whereArgs)
	{
		if(table == null || table.isEmpty())
			throw new IllegalArgumentException();
		if(column==null || column.isEmpty())
			throw new IllegalArgumentException();
		if(whereArgs==null || whereArgs.length==0)
			throw new IllegalArgumentException();
		
		SQLiteDatabase db = getWritableDatabase();
		
		for(Object obj: whereArgs)
		{
			if(obj instanceof Boolean)
			{
				Boolean bool = (Boolean)obj;
				if(bool.booleanValue()==true)
					obj = new String("1");
				else
					obj = new String("0");
			}
			else
			{
				obj = obj.toString();
			}
		}
		
		db.beginTransaction();
		try
		{
			db.delete(table, column+" = ?", (String[]) whereArgs);
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
		}
	}
	
	
	
	public void createTables(String... createTableQuery)
	{
		SQLiteDatabase db = getWritableDatabase();
		
		if(createTableQuery==null || createTableQuery.length<=0)
			throw new IllegalArgumentException();
		
		db.beginTransaction(); //Begin a transaction
		try
		{
			for(String query:createTableQuery)
			{
				db.execSQL(query); // Execute queries
			}
			db.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		catch(Exception e) { System.out.println(e.getMessage()); }
		finally{ db.endTransaction(); }
		
		Log.d("Create", "Tables");
	}
	
	
	
	//Gets the database name
	public void getDatabases()
	{
		SQLiteDatabase db = getWritableDatabase();
		List<Pair<String,String>> al = db.getAttachedDbs();
		for(Pair<String, String> p:al)
		{
			System.out.println("Pair:"+p.first.toString()+" : "+p.second.toString());
		}
	}
	
	
	
	
	public void dropTables(String... tables)
	{
		SQLiteDatabase db = getWritableDatabase();
		
		if(tables==null || tables.length<=0)
			throw new IllegalArgumentException();
		
		db.beginTransaction(); //Begin a transaction
		try
		{
			for(String table:tables)
			{
				db.execSQL("DROP TABLE "+table); // Execute queries
			}
			db.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		catch(Exception e) { System.out.println(e.getMessage()); }
		finally{ db.endTransaction(); }
		
		Log.d("DropTables", tables.toString());
	}
	
	
	
	//Prints all tables in the database
	public void getTables()
	{
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);
		
		while(cursor.moveToNext())
		{
			System.out.println(cursor.getString(cursor.getColumnIndex("name"))+"\n");
		}
	}
	
	public String toString()
	{
		return "DatabaseManager";
	}
}