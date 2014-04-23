package no.hiof.trace.fragment;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Task;
import android.R.anim;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskPlayerFragment extends Fragment
{
	DatabaseManager database;
	final boolean PLAY = true;
	final boolean PAUSE = true;
	boolean playerStatus;
	
	ImageView button;
	TextView planName;
	TextView taskName;
	
	Task task = new Task();
	
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
		
		setPlayerButtonIcon();
		
		showToast("Pressed");
	}
	
	private void showToast(String text)
	{
		Context context = this.getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private void navigateToPlanDetails(long planId) 
	{
		Intent showPlanDetails = new Intent(this.getActivity(),PlanDetailActivity.class);
		showPlanDetails.putExtra("planId", planId);
		startActivity(showPlanDetails);
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    
     }
		
}
