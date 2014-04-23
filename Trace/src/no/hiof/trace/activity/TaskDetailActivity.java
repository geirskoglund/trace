package no.hiof.trace.activity;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Task;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class TaskDetailActivity extends Activity 
{
	DatabaseManager database;
	Task task;
	
	TextView planAndTaskName;
	TextView taskDescription;
	TextView taskStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		
		database = new DatabaseManager(this);
		
		setFieldVariables();
		fetchTask();
		displayTask();
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
	
}