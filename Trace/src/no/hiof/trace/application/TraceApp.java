package no.hiof.trace.application;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.service.TraceService;
import android.app.Application;
import android.content.Context;
import android.content.Intent;


public class TraceApp extends Application 
{
	private static Context context;
	private static DatabaseManager database;
	
    public void onCreate()
    {
        super.onCreate();
        TraceApp.context = getApplicationContext();
        
        Intent startServiceIntent = new Intent(context, TraceService.class);
		context.startService(startServiceIntent);
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
