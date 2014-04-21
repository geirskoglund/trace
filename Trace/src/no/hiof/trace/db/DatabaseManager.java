package no.hiof.trace.db;

import java.util.ArrayList;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.definitions.CreateTableStatement;
import no.hiof.trace.db.values.ColumnName;
import no.hiof.trace.db.values.Columns;
import no.hiof.trace.db.values.DatabaseInfo;
import no.hiof.trace.db.values.TableName;
import no.hiof.trace.utils.PlanParser;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

public class DatabaseManager extends SQLiteOpenHelper
{	
	//private SQLiteDatabase theDatabase;
		
	public DatabaseManager(Context context)
	{
		super(context,DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION);
		//writeTableNamesToLog();
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase theDatabaseInStartupMode)
	{
		try
		{
			createTables(theDatabaseInStartupMode,
				CreateTableStatement.PLAN_STATUS, 
				CreateTableStatement.TASK_STATUS, 
				CreateTableStatement.INTERVAL, 
				CreateTableStatement.TASK, 
				CreateTableStatement.PLAN );
		}
		catch(Exception e)
		{
			log("Error: "+e.getMessage());
		}
		
		createInitialStatuses(theDatabaseInStartupMode);
	}
		
	private void createTables(SQLiteDatabase database, String... createTableQuery)
	{		
		if(createTableQuery == null || createTableQuery.length <= 0 || database == null)
			throw new IllegalArgumentException();
				
		database.beginTransaction(); //Begin a transaction

		try
		{
			for(String query:createTableQuery)
			{
				database.execSQL(query); // Execute queries
			}
			database.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		catch(Exception e) 
		{ 
			log(e.getMessage()); 
		}
		finally
		{ 
			database.endTransaction(); 
		}
	}

	private void createInitialStatuses(SQLiteDatabase theDatabaseInStartupMode) 
	{
		Context context = TraceApp.getAppContext();
		ContentValues values = new ContentValues();
		String[] statuses = 
		{
			context.getString(R.string.status_open), 
			context.getString(R.string.status_closed)
		};
	
		for(String status : statuses)
		{
			values.put(ColumnName.STATUS, status);
			theDatabaseInStartupMode.insert(TableName.PLAN_STATUS, null, values);
			theDatabaseInStartupMode.insert(TableName.TASK_STATUS, null, values);
			values.clear();
		}
	}


		@Override
	public void onUpgrade(SQLiteDatabase theDatabaseInStartupMode, int oldVersion, int newVersion)
	{
		//log("onUpdate kjøres");
		//Drops all tables at this point. Should fetch all data, parse and write to new database.
		dropTables(theDatabaseInStartupMode, TableName.PLAN, TableName.TASK, TableName.INTERVAL, TableName.PLAN_STATUS, TableName.TASK_STATUS);
		onCreate(theDatabaseInStartupMode);
		
	}
	
	private void dropTables(SQLiteDatabase database, String... tableNames)
	{	
		if(tableNames==null || tableNames.length<=0)
			throw new IllegalArgumentException();
		
		database.beginTransaction(); //Begin a transaction
		try
		{
			for(String tableName : tableNames)
			{
				database.execSQL("DROP TABLE " + tableName);
			}
			database.setTransactionSuccessful(); // Check if the transaction is successful. Don't do anything DB related after this. Throws exception if transaction fails.
		}
		catch(Exception e) { System.out.println(e.getMessage()); }
		finally{ database.endTransaction(); }
		
		log("DropTables: " + tableNames.toString());
	}

	//Prints all tables in the database
	private void writeTableNamesToLog()
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		
		Cursor cursor = theDatabase.rawQuery("SELECT * FROM sqlite_master WHERE type='table'",null);
		log("getTables kjører");
		while(cursor.moveToNext())
		{
			log("..."+cursor.getString(cursor.getColumnIndex("name")));
		}
		
		//theDatabase.close();
	}

	private void log(String message)
	{
		Log.d("TRACE-DM", message);
	}
	
	public List<String> getPlanStatusValues()
	{
		return getStatusValues(TableName.PLAN_STATUS);
	}
	
	public List<String> getTaskStatusValues()
	{
		return getStatusValues(TableName.TASK_STATUS);
	}
	
	private List<String> getStatusValues(String tableName)
	{
		List<String> statuses = new ArrayList<String>();
		SQLiteDatabase theDatabase = getReadableDatabase();
				
		String query = "SELECT * FROM " + tableName;
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		int status = cursor.getColumnIndex(ColumnName.STATUS);
		
		if(cursor.moveToFirst())
		{
			do
			{
				statuses.add(cursor.getString(status));
			}while(cursor.moveToNext());
		}
		
		return statuses;
	}
	
	public long writeToDatabase(Plan plan)
	{
		if(plan == null)
			throw new IllegalArgumentException();
		
		if(plan.getId()==0)
			return addPlan2(plan);
		else if(planExists(plan))
			return updatePlan(plan);
		else
			return addPlan2(plan);
	}
	
	public long updatePlan(Plan plan) 
	{
		ContentValues values = PlanParser.getContentValues(plan);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + plan.getId()};
		
		updateRow(TableName.PLAN, values, whereClause, whereArgs);
		
		return plan.getId();
	}
	
	private int updateRow(String table, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase theDatabase = getWritableDatabase();
		int updatedRecords = 0;
		
		theDatabase.beginTransaction();
		try
		{
			log("Før update");
			updatedRecords = theDatabase.update(table, values, whereClause, whereArgs);
			log("Etter");
			theDatabase.setTransactionSuccessful();
			log("Etter transaction");
		}
		catch(Exception e)
		{
			log("Error: " + e.getMessage());
		}
		finally
		{
			theDatabase.endTransaction();
			theDatabase.close();
		}
		
		return updatedRecords;
	}
	
	private long insertRow(String tableName, ContentValues values)
	{
		SQLiteDatabase theDatabase = getWritableDatabase();
		long rowId = 0;
		
		theDatabase.beginTransaction();
		try
		{
			log("Før insert (2) ");
			rowId = theDatabase.insert(TableName.PLAN, null, values);
			log("Etter");
			theDatabase.setTransactionSuccessful();
			log("Etter transaction");
		}
		catch(Exception e)
		{
			log("Error: " + e.getMessage());
		}
		finally
		{
			theDatabase.endTransaction();
			theDatabase.close();
		}
		
		return rowId;
	}
	
	private boolean planExists(Plan plan)
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		boolean planCheck = false;
		
		String query = String.format("SELECT * FROM plan WHERE %s = %d", ColumnName.ID, plan.getId());
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		planCheck = cursor.moveToFirst();
		
		theDatabase.close();
		
		return planCheck;
	}


	private long addPlan2(Plan plan)
	{
		log("Starter addPlan2");
		
		ContentValues values = PlanParser.getContentValues(plan);
		
		return insertRow(TableName.PLAN, values);
		
	}
	
	private long addPlan(Plan plan)
	{
		log("Starter addPlan");
		if(plan == null)
			throw new IllegalArgumentException();
		
		long rowId = 0;
		
		SQLiteDatabase theDatabase = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		//if(plan.getName()!=null)
			values.put(ColumnName.NAME, plan.getName());
		
		//if(plan.getDescription()!=null)
			values.put(ColumnName.DESCRIPTION, plan.getDescription());
		
		if(plan.getAutoRegister()== true)
			values.put(ColumnName.AUTO_REG, "1");
		else
			values.put(ColumnName.AUTO_REG, "0");
		
		//if(plan.getNfc()!=null)
			values.put(ColumnName.NFC, plan.getNfc());
		
		//if(plan.getSsid()!=null)
			values.put(ColumnName.SSID, plan.getSsid());
		
		log("Har samlet verdier");
			
		theDatabase.beginTransaction();
		try
		{
			log("Før insert");
			rowId = theDatabase.insert(TableName.PLAN, null, values);
			log("Etter");
			theDatabase.setTransactionSuccessful();
			log("Etter transaction");
		}
		catch(Exception e)
		{
			log("Error: " + e.getMessage());
		}
		finally
		{
			theDatabase.endTransaction();
			theDatabase.close();
		}
		
		return rowId;
		
	}
	
	public Plan getPlan(long planId)
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		Plan plan = new Plan();
		
		if(planId==0) return plan;
		
		//String query = "SELECT * FROM " + TableName.PLAN + " WHERE " + ColumnName.ID + " = " + planId;
		String query = String.format("SELECT * FROM %s WHERE %s = %d", TableName.PLAN, ColumnName.ID, planId);
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			plan = PlanParser.parse(cursor);
		}
		
		return plan;
	}
	
	public List<Plan> getAllPlans()
	{	
		return getAllPlans(ColumnName.ID, true);
	}
	
	public List<Plan> getLatestPlans(int quantity)
	{
		List<Plan> plans = getAllPlans(ColumnName.LAST_ACTIVATED, false);
		
		if(quantity > plans.size())
			return plans;
		
		return plans.subList(0, quantity);
	}
	
	public Plan getActivePlan()
	{
		List<Plan> plans = getLatestPlans(1);
		
		if(plans.isEmpty())
			return new Plan();
		else
			return plans.get(0);
	}
	
	public List<Plan> getAllPlans(String orderByField, boolean ascending)
	{
		String direction = (ascending ? "" : " DESC"); 
		String sorting = " ORDER BY " + orderByField + direction;
		String query = "SELECT * FROM "+ TableName.PLAN + sorting;
		
		List<Plan> plans = new ArrayList<Plan>();
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				plans.add(PlanParser.parse(cursor));
			}
			while(cursor.moveToNext());
		}
		
		return plans;
	}
	
	public void deletePlan(long planId)
	{
		deleteRowBasedOnId(TableName.PLAN, ColumnName.PLAN_ID, planId);
	}
	
	public void deleteTask(long taskId)
	{
		deleteRowBasedOnId(TableName.TASK, ColumnName.TASK_ID, taskId);
	}
	
	private void deleteRowBasedOnId(String tableName, String idColumnName, long rowId)
	{
		SQLiteDatabase theDatabase = getWritableDatabase();
		theDatabase.delete(	
				tableName, 
				idColumnName + " = ?", 
				new String[]{ String.valueOf(rowId) });
		theDatabase.close();
	}
	
//	private void deleteRow(String table, String column, Object... whereArgs)
//	{
//		// TODO: Tror denne utgår, til fordel for deletePlan, deleteTask etc...
//		if(table == null || table.isEmpty())
//			throw new IllegalArgumentException();
//		if(column==null || column.isEmpty())
//			throw new IllegalArgumentException();
//		if(whereArgs==null || whereArgs.length==0)
//			throw new IllegalArgumentException();
//		
//		SQLiteDatabase theDatabase = getWritableDatabase();
//		
//		for(Object obj: whereArgs)
//		{
//			if(obj instanceof Boolean)
//			{
//				Boolean bool = (Boolean)obj;
//				if(bool.booleanValue()==true)
//					obj = new String("1");
//				else
//					obj = new String("0");
//			}
//			else
//			{
//				obj = obj.toString();
//			}
//		}
//		
//		theDatabase.beginTransaction();
//		try
//		{
//			theDatabase.delete(table, column+" = ?", (String[]) whereArgs);
//			theDatabase.setTransactionSuccessful();
//		}
//		finally
//		{
//			theDatabase.endTransaction();
//		}
//	}
	
	public void closeDatabase()
	{
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		if (theDatabase != null && theDatabase.isOpen())
			theDatabase.close();
	}
	
	public String toString()
	{
		return "DatabaseManager";
	}
}