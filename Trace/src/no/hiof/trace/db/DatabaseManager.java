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
			return updateInterval(interval);
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
	
	//Helper method: Updates an existing Plan in the database
	private long updatePlan(Plan plan) 
	{
		ContentValues values = PlanParser.getContentValues(plan);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + plan.getId()};
		
		updateRow(TableName.PLAN, values, whereClause, whereArgs);
		
		return plan.getId();
	}
	
	//Helper method: Updates an existing Interval in the database
	private long updateInterval(Interval interval)
	{
		ContentValues values = IntervalParser.getContentValues(interval);
		String whereClause = ColumnName.ID + " = ?";
		String[] whereArgs = {"" + interval.getId()};
		
		updateRow(TableName.INTERVAL, values, whereClause, whereArgs);
		
		return interval.getId();
	}
	
	//Helper method: Updates rows in a given table with the given content values. 
	//The whereClause and whereArgs controls which rows to update.
	//Returns the number of affected rows
	private int updateRow(String table, ContentValues values, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase theDatabase = getWritableDatabase();
		int updatedRecords = 0;
		
		theDatabase.beginTransaction();
		try
		{
			updatedRecords = theDatabase.update(table, values, whereClause, whereArgs);
			theDatabase.setTransactionSuccessful();
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
	
	//Helper method: Inserts a new row in a given table, using the given ContentValues. Returns row id.
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
	
	//Returns true if the Task exists in the database
	private boolean taskExists(Task task)
	{
		return rowExists(TableName.TASK, ColumnName.ID, task.getId());
	}
	
	//Returns true if the Plan exists in the database
	private boolean planExists(Plan plan)
	{	
		return rowExists(TableName.PLAN, ColumnName.ID, plan.getId());
	}
	
	//Returns true if the Interval exists in the database
	private boolean intervalExists(Interval interval)
	{
		return rowExists(TableName.INTERVAL, ColumnName.ID, interval.getId());
	}
	
	//Checks a given table for a given row id, returning true if the row is found
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

	//Helper method: Adds a new Task record
	private long addTask(Task task)
	{
		ContentValues values = TaskParser.getContentValues(task);
		return insertRow(TableName.TASK, values);
	}
	
	//Helper method: Adds a new Plan record
	private long addPlan(Plan plan)
	{
		ContentValues values = PlanParser.getContentValues(plan);	
		return insertRow(TableName.PLAN, values);
	}
	
	//Helper method: Adds a new Interval record
	public long addInterval(Interval interval)
	{
		ContentValues values = IntervalParser.getContentValues(interval);
		return insertRow(TableName.INTERVAL, values);
	}
	
	
	/**
	 * @param taskId A task row id
	 * @return Inserts and returns an Interval connected to the given task. 
	 * 
	 * The interval start time is set to the current time.
	 */
	public Interval createIntervalWithStartTime(long taskId)
	{
		Interval interval = new Interval(taskId);
		interval.setStartTime(new Date());
		long id = addInterval(interval);
		interval.setId(id);
		return interval;
	}
	
	/**
	 * @param taskId The row id of a task
	 * @return The task as a Task instance
	 */
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
	
	/**
	 * @return The newest interval created, or null if no interval is found
	 */
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
	
	/**
	 * @param planId The row id of a plan
	 * @return The plan as a Plan instance
	 */
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
	
	/**
	 * @return A list containing all plans in the database
	 */
	public List<Plan> getAllPlans()
	{	
		return getAllPlans(ColumnName.ID, true);
	}
	
	/**
	 * @return A list containing the given number of newly activated plans
	 */
	public List<Plan> getLatestPlans(int quantity)
	{
		List<Plan> plans = getAllPlans(ColumnName.LAST_ACTIVATED, false);
		
		if(quantity > plans.size())
			return plans;
		
		return plans.subList(0, quantity);
	}
	
	/**
	 * @return The current active plan, or a dummy plan if no plans exist
	 */
	public Plan getActivePlan()
	{
		List<Plan> plans = getLatestPlans(1);
		
		if(plans.isEmpty())
			return new Plan();
		else
			return plans.get(0);
	}
	
	//Helper method: This is the base method used by the various public getPlans-methods. 
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
	
	/**
	 * @param column The column containing the value to look for. 
	 * @param criteria The value to look for
	 * @param autoTrigger The auto trigger type
	 * @return A list of open plans matching the criteria, or an empty list
	 */
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
	
	/**
	 * @param planId The row id of the parent plan
	 * @return A list containing all tasks connected to the given plan id
	 */
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
	
	/**
	 * @param taskId The row id of the parent task
	 * @return A list containing all intervals connected to the given task id
	 */
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
	
	
	/**
	 * @param taskId The row id of the task to aggregate time slots for
	 * @return The sum of all elapsed seconds on all intervals for the given task
	 */
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

	/**
	 * @param planId The row id of the plan
	 * Deletes a given plan from the database 
	 */
	public void deletePlan(long planId)
	{
		deleteRowBasedOnId(TableName.PLAN, ColumnName.PLAN_ID, planId);
	}
	
	/**
	 * @param taskId The row id of the task
	 * Deletes a given task from the database 
	 */
	public void deleteTask(long taskId)
	{
		deleteRowBasedOnId(TableName.TASK, ColumnName.TASK_ID, taskId);
	}
	
	//Helper method: This is the helper method for the various public delete-methods.
	private void deleteRowBasedOnId(String tableName, String idColumnName, long rowId)
	{
		SQLiteDatabase theDatabase = getWritableDatabase();
		theDatabase.delete(	
				tableName, 
				idColumnName + " = ?", 
				new String[]{ String.valueOf(rowId) });
		theDatabase.close();
	}
	
	
	/**
	 * Closes the database connection
	 */
	public void closeDatabase()
	{
		SQLiteDatabase theDatabase = this.getReadableDatabase();
		if (theDatabase != null && theDatabase.isOpen())
			theDatabase.close();
	}
	
	@Override
	public String toString()
	{
		return "DatabaseManager";
	}
}