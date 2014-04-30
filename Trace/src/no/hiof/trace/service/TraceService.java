package no.hiof.trace.service;

import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.sensor.WifiReciever;
import no.hiof.trace.utils.Feedback;
import no.hiof.trace.utils.PlanAutomationHelper;
import no.hiof.trace.utils.TaskPlayerState;
import no.hiof.trace.utils.TaskPlayerState.State;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TraceService extends IntentService 
{
	public static final String PREFS_NAME = "TracePrefs";
	public final TaskPlayerState playerState = new TaskPlayerState();
	private final IBinder binder = new TraceBinder();
	private SensorUpdatesReceiver sensorUpdatesReceiver;
	//private final DatabaseManager database = new DatabaseManager(this);
	
	public TraceService() 
	{
		super("TraceService");
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		
		return binder;
	}
	
	public void sendStuff(String text)
	{
		sendBroadcast(new Intent(text));
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
	    
	    if(sensorUpdatesReceiver==null)
	    	sensorUpdatesReceiver = new SensorUpdatesReceiver();
	    
	    IntentFilter wifiFilter = new IntentFilter(WifiReciever.SSID_CHANGED);
	    this.registerReceiver(sensorUpdatesReceiver, wifiFilter);
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
		if(sensorUpdatesReceiver != null)
			this.unregisterReceiver(sensorUpdatesReceiver);
	}

	@Override
	protected void onHandleIntent(Intent intent) 
	{
		// TODO Auto-generated method stub
	}
	
	private class SensorUpdatesReceiver extends BroadcastReceiver
    {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equals(WifiReciever.SSID_CHANGED))
			{				
				SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = prefs.edit();
				
				PlanAutomationHelper helper = new PlanAutomationHelper();
				boolean connected = intent.getBooleanExtra("connected", false);
				
				if(connected)
				{
					if(playerState.getPlayerState() == State.PLAYING)
						return;
					
					String ssid = intent.getStringExtra("ssid");
					
					editor.putString("last_ssid", ssid);
					editor.commit();

					if(helper.autoLoadingSetForSSID(ssid))
					{
						Plan plan = helper.getPlanForSSID(ssid);
						
						if(plan.getId() == helper.getCurrentPlan().getId())
							return;
						
						plan.setAsCurrent();
						editor.putLong("last_autoload", plan.getId());
						
						Task task = plan.getPrimaryTask();
						
						if(!task.existsInDatabase())
							return;
						
						playerState.setActiveTask(plan.getPrimaryTask());
						
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
