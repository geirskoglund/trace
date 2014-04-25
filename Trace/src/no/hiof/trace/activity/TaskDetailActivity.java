package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.adapter.IntervalListAdapter;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.model.Task;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TaskDetailActivity extends Activity 
{
	DatabaseManager database;
	Task task;
	
	TextView planAndTaskName;
	TextView taskDescription;
	TextView taskStatus;
	
	private IntervalListAdapter intervalListAdapter;
	private ListView intervalListView;
	private List<Interval> intervals;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		
		database = new DatabaseManager(this);
		
		setFieldVariables();
		fetchTask();
		displayTask();
		setupAdapter();
	}
	
	private void setupAdapter() 
	{
		//View view = findViewById(android.R.id.content);
		intervalListAdapter = new IntervalListAdapter(this);
		intervalListView = (ListView)this.findViewById(R.id.intervalList);
		intervalListView.setAdapter(intervalListAdapter);
	}

	private void setFieldVariables() 
	{
		planAndTaskName = (TextView) findViewById(R.id.task_detail_name);
		taskDescription = (TextView) findViewById(R.id.task_detail_description);
		taskStatus = (TextView) findViewById(R.id.task_detail_status);
	}

	private void fetchTask() 
	{
		long taskId = getIntent().getLongExtra("taskId", 0);
		task = database.getTask(taskId);
	}

	private void displayTask() 
	{
		planAndTaskName.setText(taskAndPlanCombined());
		taskDescription.setText(task.getDescription());
		taskStatus.setText(task.getStatus());
	}
	
	private String taskAndPlanCombined()
	{
		return String.format("%s - %s", task.getPlan().getName(),task.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_detail, menu);
		return true;
	}
	
	public void changeBackgroundColor(View view)
	{
		view.setBackgroundColor(Color.RED);
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    
	    intervals = task.getIntervals(); 
		intervalListAdapter.updateIntervals(intervals);
		intervalListAdapter.notifyDataSetChanged();
     }
	
}