package no.hiof.trace.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TraceService extends Service {

	public TraceService() 
	{
		super();
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) 
	{
	    handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    handleCommand(intent);
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	private void handleCommand(Intent intent)
	{
		
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
	}
	
	@Override
	public void onDestroy ()
	{
		super.onDestroy();
		
	}

}
