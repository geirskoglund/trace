package no.hiof.trace.db.model.definitions;

public class T
{
	public static final String PLAN = "plan";
	public static final String TASK = "task";
	public static final String INTERVAL = "interval";
	public static final String TASK_STATUS = "task_status";
	public static final String PLAN_STATUS = "plan_status";
	
	public static final String[] PLAN_COLUMNS = {C.ID, C.NAME, C.DESCRIPTION, C.SSID, C.NFC, C.LONG, C.LAT, C.AUTO_REG, C.STATUS, C.PRIMARY_TASK};
	public static final String[] TASK_COLUMNS = {C.ID, C.NAME, C.DESCRIPTION, C.PLAN_ID, C.STATUS};
	public static final String[] INTERVAL_COLUMNS = {C.ID, C.START_TIME, C.END_TIME, C.TASK_ID};
	public static final String[] TASK_STATUS_COLUMNS = {C.STATUS};
	public static final String[] PLAN_STATUS_COLUMNS = {C.STATUS};
	
	public static String CREATE_TABLE_PLAN = 
			"CREATE TABLE plan ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"name TEXT NOT NULL,"
			+"description TEXT NOT NULL,"
			+"ssid TEXT,"
			+"nfc TEXT,"
			+"long DOUBLE,"
			+"lat DOUBLE,"
			+"auto_register BOOLEAN NOT NULL,"
			+"status TEXT,"
			+"primary_task INTEGER,"
			+"FOREIGN KEY(status) REFERENCES plan_status(status),"
			+"FOREIGN KEY(primary_task) REFERENCES task(id)"
			+")";
	
	public static String CREATE_TABLE_TASK = 
			"CREATE TABLE task ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"name TEXT NOT NULL,"
			+"description TEXT NOT NULL,"
			+"plan_id INTEGER,"
			+"status TEXT,"
			+"FOREIGN KEY(status) REFERENCES task_status(status),"
			+"FOREIGN KEY(plan_id) REFERENCES plan(id)"
			+")";
	
	public static String CREATE_TABLE_INTERVAL = 
			"CREATE TABLE interval ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"start_time DATETIME NOT NULL,"
			+"end_time DATETIME,"
			+"task_id INTEGER,"
			+"FOREIGN KEY(task_id) REFERENCES task(id)"
			+")";
	
	public static String CREATE_TABLE_PLAN_STATUS = 
			"CREATE TABLE plan_status ( "
			+"status TEXT PRIMARY KEY"
			+")";
	
	public static String CREATE_TABLE_TASK_STATUS = 
			"CREATE TABLE task_status ( "
			+"status TEXT PRIMARY KEY"
			+")";
}
