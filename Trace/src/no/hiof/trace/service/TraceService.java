package no.hiof.trace.service;

import no.hiof.trace.activity.MainActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.sensor.WifiReciever;
import no.hiof.trace.utils.Feedback;
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

/**
 * @author Trace Inc.
 *
 */
public class TraceService extends Service 
{
	public static final String PREFS_NAME = "TracePrefs";
	public final TaskPlayerState playerState = new TaskPlayerState();
	private final IBinder binder = new TraceBinder();
	private SensorUpdatesReceiver sensorUpdatesReceiver;
	
	private boolean sendSelectionNotificationt = false;
	private boolean sendRegistrationNotification = false;
	
	public TraceService() 
	{
		super();
	}

	/**
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) 
	{	
		return binder;
	}
	
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     * @author Trace Inc.
     *
     */
    public class TraceBinder extends Binder 
    {
        /**
         * Gets the instance of the service
         * @return the service-object
         */
        public TraceService getService() 
        {
            // Return this instance of TraceService so clients can call public methods
            return TraceService.this;
        }
    }
	
    /**
     * This is the old onStart method that will be called on the pre-2.0
     * platform.  On 2.0 or later we override onStartCommand() so this
     * method will not be called.
     * @see android.app.Service#onStart(android.content.Intent, int)
     */
    @SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) 
	{
	    handleCommand();
	    super.onStart(intent, startId);
	}
	
	/**
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
	    handleCommand(); 
	    return super.onStartCommand(intent, flags, startId);
	}
	
	/*
	 * Handles the intent sent from 
	 */
	private void handleCommand()
	{
	    if(sensorUpdatesReceiver==null)
	    	sensorUpdatesReceiver = new SensorUpdatesReceiver();
	    
	    IntentFilter wifiFilter = new IntentFilter(WifiReciever.SSID_CHANGED);
	    this.registerReceiver(sensorUpdatesReceiver, wifiFilter);
	}
	
	/*
	 * Runs when the service gets created/instantiated
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	/* 
	 * Runs when the service gets destroyed
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy ()
	{
		if(sensorUpdatesReceiver != null)
			this.unregisterReceiver(sensorUpdatesReceiver);
		
		super.onDestroy();
	}
	
	/**
	 * A reciever for handling WiFi-connection changes
	 * @author Trace Inc.
	 *
	 */
	private class SensorUpdatesReceiver extends BroadcastReceiver
    {

		/**
		 * A method for recieving WiFi-connection changes
		 * Checks the SSID and compares it to SSIDS registered with Plans
		 * If a Plan has registered the SSID it auto selects the Plan. If it has a registered SSID and is set to auto register, it starts to register a time slot for the Plan.
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
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
					Plan plan = null;

					if(helper.autoLoadingSetForSSID(ssid))
					{
						plan = helper.getPlanForSSID(ssid);
						
						if(plan.getId() == helper.getCurrentPlan().getId())
							return;
						
						plan.setAsCurrent();
						sendSelectionNotificationt = true;
						
						Task task = plan.getPrimaryTask();
						
						if(!task.existsInDatabase())
							return;
						
						playerState.setActiveTask(task);
						
						if(plan.getAutoRegister())
						{
							playerState.startInterval(true);
							sendRegistrationNotification = true;
						}
					}
					
					displayNotification(plan);
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
	
	/*A helper function for displaying notifications when a plan gets auto selected or starts auto registering.
	 * @param plan the Plan-object to notify the user about
	 */
	private void displayNotification(Plan plan)
	{
		if(plan==null)
			return;
		
		String title = "";
		String content ="";
		
		if(sendRegistrationNotification)
		{
			title = getString(R.string.app_name) + " - " + getString(R.string.auto_registration);
			content = String.format("%s %s",getString(R.string.started_to_register_time_for), plan.getName());
		}
		else if(sendSelectionNotificationt)
		{
			title = getString(R.string.app_name) + " - " + getString(R.string.auto_selection);
			content = String.format("%s %s %s", getString(R.string.plan),plan.getName(),getString(R.string.is_selected));
		}
		
		Intent notificationIntent = Feedback.buildNotificationIntent(MainActivity.class, "planId", plan.getId());
		Feedback.showSmallNotification(R.drawable.ic_launcher, title, content, notificationIntent, MainActivity.class);
		Feedback.vibrateDevice(Feedback.LONG_VIBRATION);
		
		sendRegistrationNotification = false;
		sendSelectionNotificationt = false;
	}

}
