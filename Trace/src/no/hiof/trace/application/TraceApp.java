package no.hiof.trace.application;

import no.hiof.trace.utils.TaskPlayerState;
import android.app.Application;
import android.content.Context;


public class TraceApp extends Application 
{
	private static Context context;
	public static TaskPlayerState playerState;
	
    public void onCreate()
    {
        super.onCreate();
        TraceApp.context = getApplicationContext();
        playerState = new TaskPlayerState();
    }

    public static Context getAppContext() 
    {
        return TraceApp.context;
    }
    

    
}
