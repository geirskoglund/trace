package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.utils.Feedback;
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
	ImageView button;
	TextView planName;
	TextView taskName;
	
	public TaskPlayerFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_task_player, container, false);
		
		setViewVariables(view);
		setButtonEventListener();
		setPlayerButtonIconForCurrentState();
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

	public void taskPlayerButtonToggle()
	{
		if(TraceApp.playerState.getPlayerState() == State.PAUSED)
		{
			TraceApp.playerState.startInterval();
		}
		else if(TraceApp.playerState.getPlayerState() == State.PLAYING)
		{
			TraceApp.playerState.stopInterval();
		}
		
		setPlayerButtonIconForCurrentState();
	}
	
	private void setPlayerButtonIconForCurrentState() 
	{
		switch (TraceApp.playerState.getPlayerState())
		{
			case IDLE: 
				// TODO: Use correct idle icon
				button.setImageResource(android.R.drawable.ic_media_ff); //temporary
				return;
			case PAUSED:
				button.setImageResource(android.R.drawable.ic_media_play);
				return;
			case PLAYING:
				button.setImageResource(android.R.drawable.ic_media_pause);
		}
	}

	public void load(Task task)
	{
		if(noLoadingIsNeeded(task)) return;
		
		TraceApp.playerState.stopInterval();
		TraceApp.playerState.setActiveTask(task);
		
		displayTask();
		setPlayerButtonIconForCurrentState();

		Feedback.showToast("Task \"" + task.getName() + "\" loaded");
	}
	
	private boolean noLoadingIsNeeded(Task task) 
	{
		return (task == null || TraceApp.playerState.getActiveTask().getId() == task.getId() );	
	}

	private void displayTask() 
	{
		this.taskName.setText(TraceApp.playerState.getActiveTask().getName());
		this.planName.setText(TraceApp.playerState.getActiveTask().getPlan().getName());
	}

	@Override
	public void onResume()
    {  
	    super.onResume();
	    
    }	
}
