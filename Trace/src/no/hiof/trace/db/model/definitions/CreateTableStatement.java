package no.hiof.trace.db.model.definitions;


public class CreateTableStatement
{
	//Rewrite to use Column-definitions?
	public static String PLAN = 
			"CREATE TABLE plan ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"name TEXT NOT NULL,"
			+"description TEXT NOT NULL,"
			+"ssid TEXT,"
			+"nfc TEXT,"
			+"lon DOUBLE,"
			+"lat DOUBLE,"
			+"auto_register BOOLEAN NOT NULL,"
			+"status TEXT,"
			+"primary_task INTEGER,"
			+"last_activated DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+"FOREIGN KEY(status) REFERENCES plan_status(status),"
			+"FOREIGN KEY(primary_task) REFERENCES task(id)"
			+")";
	
	public static String TASK = 
			"CREATE TABLE task ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"name TEXT NOT NULL,"
			+"description TEXT NOT NULL,"
			+"plan_id INTEGER,"
			+"status TEXT,"
			+"FOREIGN KEY(status) REFERENCES task_status(status),"
			+"FOREIGN KEY(plan_id) REFERENCES plan(id)"
			+")";
	
	public static String INTERVAL = 
			"CREATE TABLE interval ( "
			+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+"start_time DATETIME NOT NULL,"
			+"end_time DATETIME,"
			+"task_id INTEGER,"
			+"FOREIGN KEY(task_id) REFERENCES task(id)"
			+")";
	
	public static String PLAN_STATUS = 
			"CREATE TABLE plan_status ( "
			+"status TEXT PRIMARY KEY"
			+")";
	
	public static String TASK_STATUS = 
			"CREATE TABLE task_status ( "
			+"status TEXT PRIMARY KEY"
			+")";
}
