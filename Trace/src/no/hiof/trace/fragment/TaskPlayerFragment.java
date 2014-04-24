package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.utils.Feedback;
import no.hiof.trace.utils.TaskPlayerState;
import no.hiof.trace.utils.TaskPlayerState.State;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskPlayerFragment extends Fragment
{
	DatabaseManager database;
	final boolean PLAY = true;
	final boolean PAUSE = true;
	boolean playerStatus;
	
	ImageView button;
	TextView planName;
	TextView taskName;
	
	Task currentTask = new Task();
	
	public TaskPlayerFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_task_player, container, false);
		
		playerStatus = PAUSE;
		
		database = new DatabaseManager(this.getActivity());
		setViewVariables(view);
		setButtonEventListener();
		setPlayerButtonIcon();
		
		return view;
	}
	
	private void setViewVariables(View view) 
	{
		button = (ImageView)view.findViewById(R.id.task_player_button);
		planName = (TextView)view.findViewById(R.id.task_player_plan_name);
		taskName = (TextView)view.findViewById(R.id.task_player_task_name);
	}

	private void setButtonEventListener()
	{
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				taskPlayerButtonToggle();		
			}
		});
	}

	private void setPlayerButtonIcon()
	{
		if(playerStatus == PAUSE)
			button.setImageResource(android.R.drawable.ic_media_play);
		else
			button.setImageResource(android.R.drawable.ic_media_pause);
	}

	public void taskPlayerButtonToggle()
	{
		playerStatus = !playerStatus;
		setState();
		setPlayerButtonIcon();
		Feedback.showToast("Pressed");
	}
	
	private void setState() 
	{
		switch (TraceApp.playerState.getPlayerState())
		{
			case IDLE: 
				TraceApp.playerState.setPlayerState(State.PAUSED);
				break;
			case PAUSED:
				TraceApp.playerState.setPlayerState(State.PLAYING);
				break;
			case PLAYING:
				TraceApp.playerState.setPlayerState(State.PAUSED);
				break;
			default:
				TraceApp.playerState.setPlayerState(State.IDLE);
				break;
		}
	}

	public void load(Task task)
	{
		if(noLoadingIsNeeded(task)) return;
		
		pauseCurrentTask();
		setNewCurrentTask(task);
		displayTask();
		
		Feedback.showToast(task.getName() + " loaded.");
	}
	
	private boolean noLoadingIsNeeded(Task task) 
	{
		return (task == null || 
				this.currentTask.getId() == task.getId() || 
				task.getId() == 0);	
	}

	private void pauseCurrentTask() 
	{
		if(TraceApp.playerState.getPlayerState() != TaskPlayerState.State.PLAYING)
			return;
		
		// TODO Add pause functionality
		return;
	}

	private void setNewCurrentTask(Task task) 
	{
		TraceApp.playerState.setActiveTask(task);
		this.currentTask = task;
	}

	private void displayTask() 
	{
		this.taskName.setText(currentTask.getName());
		this.planName.setText(currentTask.getPlan().getName());
	}

	@Override
	public void onResume()
    {  
	    super.onResume();
	    
    }	
}
