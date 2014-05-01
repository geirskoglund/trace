package no.hiof.trace.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.db.model.definitions.CreateTableStatement;
import no.hiof.trace.db.values.ColumnName;
import no.hiof.trace.db.values.DatabaseInfo;
import no.hiof.trace.db.values.TableName;
import no.hiof.trace.utils.IntervalParser;
import no.hiof.trace.utils.PlanParser;
import no.hiof.trace.utils.TaskParser;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Trace Inc.
 * 
 * Providing access to the database and methods for writing and reading.
 *
 */
@SuppressLint("DefaultLocale")
public class DatabaseManager extends SQLiteOpenHelper
{		
	public DatabaseManager(Context context)
	{
		super(context,DatabaseInfo.DATABASE_NAME, null, DatabaseInfo.DATABASE_VERSION);
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
	
	//Creates tables in the database, based on the supplied create statements
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

	//Populates the status tables with initial data. 
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
		//Drops all tables at this point. Should fetch all data, parse and write to new database.
		dropTables(theDatabaseInStartupMode, TableName.PLAN, TableName.TASK, TableName.INTERVAL, TableName.PLAN_STATUS, TableName.TASK_STATUS);
		onCreate(theDatabaseInStartupMode);
	}
	
	//Drops supplied tables from database
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

	private void log(String message)
	{
		Log.d("TRACE-DM", message);
	}
	
	
	/**
	 * @return a list containing the status values in the plan_status table 
	 */
	public List<String> getPlanStatusValues()
	{
		return getStatusValues(TableName.PLAN_STATUS);
	}
	
	/**
	 * @return a list containing the status values in the task_status table 
	 */
	public List<String> getTaskStatusValues()
	{
		return getStatusValues(TableName.TASK_STATUS);
	}
	
	//helper method, returning a list of statuses from the given status table
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
	
	
	/**
	 * @param plan A Plan object
	 * @return The row id of the plan
	 * 
	 * Creates a new Plan or updates an existing Plan in the database.
	 */
	public long writeToDatabase(Plan plan)
	{
		if(plan == null)
			throw new IllegalArgumentException();
		
		if(plan.getId()==0)
			return addPlan(plan);
		else if(planExists(plan))
			return updatePlan(plan);
		else
			return addPlan(plan);
	}
	
	/**
	 * @param task A Task object
	 * @return The row id of the task
	 * 
	 * Creates a new Task or updates an existing Task in the database.
	 */
	public long writeToDatabase(Task task)
	{
		if(task == null)
			throw new IllegalArgumentException();
		
		if(task.getId()==0)
			return addTask(task);
		else if(taskExists(task))
			return updateTask(task);
		else
			return addTask(task);
	}
	
	/**
	 * @param interval An Interval object
	 * @return The row id of the interval
	 * 
	 * Creates a new Interval or updates an existing Interval in the database.
	 */
	public long writeToDatabase(Interval interval)
	{
		if(interval==null)
			throw new IllegalArgumentException();
		
		if(interval.getId()==0)
			return addInterval(interval);
		else if(intervalExists(interval))
			return 0;
		else
			return addInterval(interval);
	}
	
	//Helper method: Updates an existing task in the database
	private long updateTask(Task task)
	{
		ContentValues values = TaskParser.getContentValues(task);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + task.getId()};
		
		updateRow(TableName.TASK, values, whereClause, whereArgs);
		
		return task.getId();
	}
	
	
	private long updatePlan(Plan plan) 
	{
		ContentValues values = PlanParser.getContentValues(plan);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + plan.getId()};
		
		updateRow(TableName.PLAN, values, whereClause, whereArgs);
		
		return plan.getId();
	}
	
	public long updateInterval(Interval interval)
	{
		ContentValues values = IntervalParser.getContentValues(interval);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + interval.getId()};
		
		updateRow(TableName.INTERVAL, values, whereClause, whereArgs);
		
		return interval.getId();
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
			rowId = theDatabase.insert(tableName, null, values);
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
	
	private boolean taskExists(Task task)
	{
		return rowExists(TableName.TASK, ColumnName.ID, task.getId());
	}
	
	private boolean planExists(Plan plan)
	{	
		return rowExists(TableName.PLAN, ColumnName.ID, plan.getId());
	}
	
	private boolean intervalExists(Interval interval)
	{
		return rowExists(TableName.INTERVAL, ColumnName.ID, interval.getId());
	}
	
	@SuppressLint("DefaultLocale")
	private boolean rowExists(String tableName, String idColumnName, long rowId)
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		boolean rowCheck = false;
		
		String query = String.format("SELECT * FROM %s WHERE %s = %d", tableName, idColumnName, rowId);
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		rowCheck = cursor.moveToFirst();
		theDatabase.close();
		
		return rowCheck;
	}

	private long addTask(Task task)
	{
		ContentValues values = TaskParser.getContentValues(task);
		return insertRow(TableName.TASK, values);
	}
	
	private long addPlan(Plan plan)
	{
		ContentValues values = PlanParser.getContentValues(plan);	
		return insertRow(TableName.PLAN, values);
	}
	public long addInterval(Interval interval)
	{
		ContentValues values = IntervalParser.getContentValues(interval);
		return insertRow(TableName.INTERVAL, values);
	}
	public Interval createIntervalWithStartTime(long taskId)
	{
		Interval interval = new Interval(taskId);
		interval.setStartTime(new Date());
		long id = addInterval(interval);
		interval.setId(id);
		return interval;
	}
	
	@SuppressLint("DefaultLocale")
	public Task getTask(long taskId)
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		Task task = new Task();
		
		if(taskId == 0) return task;
		
		String query = String.format("SELECT * FROM %s WHERE %s = %d", TableName.TASK, ColumnName.ID, taskId);
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			task = TaskParser.parse(cursor);
		}
		
		return task;
	}
	
	public Interval getNewestInterval()
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		
		String query = String.format("SELECT * FROM %s ORDER BY %s DESC", TableName.INTERVAL, ColumnName.ID);
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
			return IntervalParser.parse(cursor);
		else
			return null;
	}
	
	@SuppressLint("DefaultLocale")
	public Plan getPlan(long planId)
	{
		SQLiteDatabase theDatabase = getReadableDatabase();
		Plan plan = new Plan();
		
		if(planId==0) return plan;

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
	
	private List<Plan> getAllPlans(String orderByField, boolean ascending)
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
	
	public List<Plan> getOpenAutoLoadingPlans(String column, String criteria, String autoTrigger)
	{
		List<Plan> plans = new ArrayList<Plan>();
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		
		String whereClause = String.format("%s = ? AND %s = ? AND %s = ?", column, ColumnName.STATUS, ColumnName.AUTO_TRIGGER);
		String open = TraceApp.getAppContext().getString(R.string.status_open);
		
		Cursor cursor = theDatabase.query(TableName.PLAN, null, whereClause , new String[]{criteria, open, autoTrigger}, 
				null, null, ColumnName.ID + " DESC", null);
				
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
	
	public List<Task> getTasks(long planId)
	{
		List<Task> tasks = new ArrayList<Task>();
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		
		String query = "SELECT * FROM "+ TableName.TASK + " WHERE " + ColumnName.PLAN_ID + " = " + planId;
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do
				tasks.add(TaskParser.parse(cursor));
			while(cursor.moveToNext());
		}
		
		return tasks;
	}
	
	public List<Interval> getIntervals(long taskId)
	{
		List<Interval> intervals = new ArrayList<Interval>();
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		
		String query = "SELECT * FROM "+ TableName.INTERVAL + " WHERE " + ColumnName.TASK_ID + " = " + taskId + 
						" ORDER BY " + ColumnName.ID + " DESC";
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			do
			{
				Interval interval = IntervalParser.parse(cursor);
				if(interval.isCompleted())
					intervals.add(interval);
			}while(cursor.moveToNext());
		}
		return intervals;
	}
	
	public long getAggregatedTimeSlots(long taskId)
	{
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		
		String query = "SELECT SUM("+ColumnName.ELAPSED_SECONDS +") FROM "+ TableName.INTERVAL + " WHERE " + ColumnName.TASK_ID + " = " + taskId;
		Cursor cursor = theDatabase.rawQuery(query, null);
		
		if(cursor.moveToFirst())
		{
			return cursor.getLong(0);
		}
		
		return 0;
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