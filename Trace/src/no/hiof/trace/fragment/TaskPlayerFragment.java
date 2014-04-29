package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.activity.TaskDetailActivity;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.utils.Feedback;
import no.hiof.trace.utils.TaskPlayerState.State;
import no.hiof.trace.view.TimerTextView;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskPlayerFragment extends Fragment implements OnClickListener
{
	ImageView button;
	TextView planName;
	TextView taskName;
	TimerTextView timer;
	
	LinearLayout infoBox;
	public TaskPlayerFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_task_player, container, false);
		
		setViewVariables(view);
		updateBasedOnState();
		
		button.setOnClickListener(this);
		infoBox.setOnClickListener(this);
		displayTask();
		return view;
	}
	
	private void setViewVariables(View view) 
	{
		button = (ImageView)view.findViewById(R.id.task_player_button);
		planName = (TextView)view.findViewById(R.id.task_player_plan_name);
		taskName = (TextView)view.findViewById(R.id.task_player_task_name);
		timer = (TimerTextView)view.findViewById(R.id.task_player_timer);
		infoBox = (LinearLayout)view.findViewById(R.id.task_player_info_box);
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
		
		updateBasedOnState();
	}
	
	private void updateBasedOnState() 
	{
		switch (TraceApp.playerState.getPlayerState())
		{
			case IDLE: 
				button.setImageResource(R.drawable.ic_action_play_dark); //temporary
				timer.stop();
				timer.setVisibility(View.INVISIBLE);
				return;
			case PAUSED:
				button.setImageResource(R.drawable.ic_action_play);
				timer.stop();
				timer.setVisibility(View.VISIBLE);
				return;
			case PLAYING:
				button.setImageResource(R.drawable.ic_action_stop);
				timer.setTime(TraceApp.playerState.getCurrentInterval().getStartTime());
				timer.setVisibility(View.VISIBLE);
				timer.start();
				return;
		}
	}

	public void load(Task task)
	{
		if(noLoadingIsNeeded(task)) return;
		
		TraceApp.playerState.stopInterval();
		TraceApp.playerState.setActiveTask(task);
		
		displayTask();
		timer.setTime(TraceApp.playerState.getCurrentInterval().getStartTime());
		updateBasedOnState();

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
		Task task = TraceApp.playerState.getActiveTask();
		if(task==null || task.getId()==0)
			return;
		
		Intent showTaskDetail = new Intent(TraceApp.getAppContext() , TaskDetailActivity.class);
		showTaskDetail.putExtra("taskId", task.getId());
		showTaskDetail.putExtra("planId", task.getPlanId());
		startActivity(showTaskDetail);
	}
}
