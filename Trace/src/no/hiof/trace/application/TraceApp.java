package no.hiof.trace.application;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.utils.TaskPlayerState;
import android.app.Application;
import android.content.Context;


public class TraceApp extends Application 
{
	private static Context context;
	//public static TaskPlayerState playerState;
	private static DatabaseManager database;
	
    public void onCreate()
    {
        super.onCreate();
        TraceApp.context = getApplicationContext();
        //playerState = new TaskPlayerState(context);
    }
    
    public static DatabaseManager database()
    {
    	if(TraceApp.database == null)
    		TraceApp.database = new DatabaseManager(TraceApp.context);
    	return database;
    }

    public static Context getAppContext() 
    {
        return TraceApp.context;
    }
    

    
}
