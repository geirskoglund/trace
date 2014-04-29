package no.hiof.trace.service;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.utils.TaskPlayerState;
import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TraceService extends IntentService 
{

	public final TaskPlayerState playerState = new TaskPlayerState();
	private final IBinder binder = new TraceBinder();
	//private final DatabaseManager database = new DatabaseManager(this);
	
	public TraceService() 
	{
		super("TraceService");
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return binder;
	}
	
	/**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TraceBinder extends Binder 
    {
        public TraceService getService() 
        {
            // Return this instance of TraceService so clients can call public methods
            return TraceService.this;
        }
    }
	
	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) 
	{
	    handleCommand(intent);
	    super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    handleCommand(intent);
	    
	    return super.onStartCommand(intent, flags, startId);
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

	@Override
	protected void onHandleIntent(Intent arg0) 
	{
		// TODO Auto-generated method stub
	}

}
