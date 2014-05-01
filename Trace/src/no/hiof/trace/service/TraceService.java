package no.hiof.trace.service;

import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.sensor.WifiReciever;
import no.hiof.trace.utils.PlanAutomationHelper;
import no.hiof.trace.utils.TaskPlayerState;
import no.hiof.trace.utils.TaskPlayerState.State;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

public class TraceService extends Service 
{
	public static final String PREFS_NAME = "TracePrefs";
	public final TaskPlayerState playerState = new TaskPlayerState();
	private final IBinder binder = new TraceBinder();
	private SensorUpdatesReceiver sensorUpdatesReceiver;
	
	public TraceService() 
	{
		//super("TraceService");
		super();
	}

	@Override
	public IBinder onBind(Intent intent) 
	{	
		return binder;
	}
	
//	public void sendStuff(String text)
//	{
//		sendBroadcast(new Intent(text));
//	}
	
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
    @SuppressWarnings("deprecation")
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
	    if(sensorUpdatesReceiver==null)
	    	sensorUpdatesReceiver = new SensorUpdatesReceiver();
	    
	    IntentFilter wifiFilter = new IntentFilter(WifiReciever.SSID_CHANGED);
	    this.registerReceiver(sensorUpdatesReceiver, wifiFilter);
	}
	
	@Override
	public void onCreate()
	{
		//Log.d("TRACE-MA","Service onCreate");
		super.onCreate();
		
	}
	
	@Override
	public void onDestroy ()
	{
		if(sensorUpdatesReceiver != null)
			this.unregisterReceiver(sensorUpdatesReceiver);
		
		//Log.d("TRACE-MA","Service is destroyed...");
		super.onDestroy();
	}
	
	private class SensorUpdatesReceiver extends BroadcastReceiver
    {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equals(WifiReciever.SSID_CHANGED))
			{				
				PlanAutomationHelper helper = new PlanAutomationHelper();
				boolean connected = intent.getBooleanExtra("connected", false);
				
				if(connected)
				{
					if(playerState.getPlayerState() == State.PLAYING)
						return;
					
					String ssid = intent.getStringExtra("ssid");

					if(helper.autoLoadingSetForSSID(ssid))
					{
						Plan plan = helper.getPlanForSSID(ssid);
						
						if(plan.getId() == helper.getCurrentPlan().getId())
							return;
						
						plan.setAsCurrent();
						
						Task task = plan.getPrimaryTask();
						
						if(!task.existsInDatabase())
							return;
						
						playerState.setActiveTask(task);
						
						if(plan.getAutoRegister())
							playerState.startInterval(true);
					}
				}
				else
				{
					if(playerState.getPlayerState() == State.PLAYING)
					{
						if(playerState.getCurrentInterval().isAutoRegistered())
							playerState.stopInterval();
					}
					
				}
			}
		}
    	
    }

}
