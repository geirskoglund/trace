package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.activity.TaskDetailActivity;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.service.TraceService;
import no.hiof.trace.service.TraceService.TraceBinder;
import no.hiof.trace.utils.Feedback;
import no.hiof.trace.utils.TaskPlayerState;
import no.hiof.trace.utils.TaskPlayerState.State;
import no.hiof.trace.view.TimerTextView;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Trace Inc.
 * 
 * A fragment displaying a small player, inspired by music players like Spotify.
 * 
 * A task can be loaded to the player, and registration of intervals is done by pressing play or stop.
 * The player is tightly connected to the TaskPlayerState, which is instantiated on the TraceService.
 */
public class TaskPlayerFragment extends Fragment implements OnClickListener
{
	ImageView button;
	TextView planName;
	TextView taskName;
	TimerTextView timer;
	
	TraceService service;
	boolean bound = false;	
	private PlayerUpdateReceiver playerUpdateReciever;
	LinearLayout infoBox;

	
	/**
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 * Preparing the fragment for loading data.
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_task_player, container, false);
		
		setViewVariables(view);
		
		button.setOnClickListener(this);
		infoBox.setOnClickListener(this);

		return view;
	}
	
	// Instantiating all View variables
	private void setViewVariables(View view) 
	{
		button = (ImageView)view.findViewById(R.id.task_player_button);
		planName = (TextView)view.findViewById(R.id.task_player_plan_name);
		taskName = (TextView)view.findViewById(R.id.task_player_task_name);
		timer = (TimerTextView)view.findViewById(R.id.task_player_timer);
		infoBox = (LinearLayout)view.findViewById(R.id.task_player_info_box);
	}

	// Callback for the Start/Stop button. Starts or stops registering, based on the state.
	private void taskPlayerButtonToggle()
	{
		if(!bound)
			return;

		if(service.playerState.getPlayerState() == State.PAUSED)
		{
			service.playerState.startInterval();
		}
		else if(service.playerState.getPlayerState() == State.PLAYING)
		{
			service.playerState.stopInterval(timer.getTimeInSeconds()-1);
		}
	}
	
	// Updates elements in the fragment, based on state. This is de facto a callback for the 
	// players local broadcast reciever (playerUpdateReceiver) who is listening to broadcasts
	// from the TaskPlayerState-instance on the service.
	private void updateBasedOnState() 
	{
		if(!bound)
			return;
		
		switch (service.playerState.getPlayerState())
		{
			case IDLE: 
				button.setImageResource(R.drawable.ic_action_play_dark); 
				timer.stop();
				timer.setVisibility(View.INVISIBLE);
				return;
			case PAUSED:
				button.setImageResource(R.drawable.ic_action_play);
				
				boolean aNewTaskWasJustLoaded = service.playerState.getCurrentInterval().
						getTaskId() != service.playerState.getActiveTask().getId();
				if(aNewTaskWasJustLoaded)
					timer.setTime(0);
				
				timer.stop();
				timer.setTimerText();
				timer.setVisibility(View.VISIBLE);
				return;
			case PLAYING:
				button.setImageResource(R.drawable.ic_action_stop);
				timer.setTime(service.playerState.getCurrentInterval().getStartTime());
				timer.setVisibility(View.VISIBLE);
				timer.start();
				return;
		}
	}

	/**
	 * @param task The Task-instance to be loaded to the player
	 * 
	 * 
	 */
	public void load(Task task)
	{
		if(!bound)
			return;
		
		if(noLoadingIsNeeded(task)) return;
		
		service.playerState.stopInterval();
		service.playerState.setActiveTask(task);

		if(!task.getName().equals(""))
			Feedback.showToast(getActivity().getString(R.string.capital_task)+"\"" + task.getName() + "\"" +getActivity().getString(R.string.loaded));
	}
	
	private boolean noLoadingIsNeeded(Task task) 
	{
		if(!bound)
			return true;
		
		return (task == null || service.playerState.getActiveTask().getId() == task.getId() );
	}

	private void displayTask() 
	{
		if(bound)
		{
			this.taskName.setText(service.playerState.getActiveTask().getName());
			this.planName.setText(service.playerState.getActiveTask().getPlan().getName());
		}
	}

	/**
	 * @see android.support.v4.app.Fragment#onResume()
	 * 
	 * Setting up the broadcast receiver used for listening to state changes
	 */
	@Override
	public void onResume()
    {  
	    super.onResume();
	    if (playerUpdateReciever==null)
	    	playerUpdateReciever = new PlayerUpdateReceiver();
	    
	    IntentFilter intentFilter = new IntentFilter(TaskPlayerState.REFRESH_DATA_INTENT);
	    this.getActivity().registerReceiver(playerUpdateReciever, intentFilter);
    }
	
	/**
	 * @see android.support.v4.app.Fragment#onPause()
	 * 
	 * Unregistering broadcast reciever
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		if(playerUpdateReciever != null)
			this.getActivity().unregisterReceiver(playerUpdateReciever);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onStart()
	 * 
	 * Binds to the TraceService
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		Intent intent = new Intent(this.getActivity(), TraceService.class);
		this.getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * @see android.support.v4.app.Fragment#onStop()
	 * 
	 * Unbinds from the TraceService
	 */
	@Override
	public void onStop()
	{
		super.onStop();
		if(bound)
		{
			this.getActivity().unbindService(serviceConnection);
			bound = false;
		}
	}
	
	/**
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * OnClick callback wrapper, routing to proper methods
	 */
	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
		case R.id.task_player_info_box:
			navigateToTaskDetail();
			break;
		case R.id.task_player_button:
			taskPlayerButtonToggle();
			break;
		}	
	}

	private void navigateToTaskDetail() 
	{
		if(!bound)
			return;
		
		Task task = service.playerState.getActiveTask();
		if(task==null || task.getId()==0)
			return;
		
		Intent showTaskDetail = new Intent(TraceApp.getAppContext() , TaskDetailActivity.class);
		showTaskDetail.putExtra("taskId", task.getId());
		showTaskDetail.putExtra("planId", task.getPlanId());
		startActivity(showTaskDetail);
	}
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) 
        {
            // We've bound to LocalService, cast the IBinder and get TraceService instance
            TraceBinder traceBinder = (TraceBinder) binder;
            service = traceBinder.getService();
            bound = true;
            updateBasedOnState();
            displayTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) 
        {
            bound = false;
        }
    };
    
    /**
     * Receive updates from service
     * */
    private class PlayerUpdateReceiver extends BroadcastReceiver
    {

		@Override
		public void onReceive(Context context, Intent intent) 
		{
			if(intent.getAction().equals(TaskPlayerState.REFRESH_DATA_INTENT))
			{
				updateBasedOnState();
	            displayTask();
			}
		}
    	
    }
}
