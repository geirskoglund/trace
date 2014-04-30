package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class TaskEditorActivity extends Activity 
{
	private Task task;
	private EditText taskName;
	private EditText taskDescription;
	private Spinner taskStatusSpinner;
	
	private DatabaseManager database;
	private ArrayAdapter<String> statusAdapter;
	
	AlertDialog.Builder alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_editor);
		
		database = new DatabaseManager(this);
		setFieldVariables();
		setupSpinner();
		createTask();
		populateTaskDataFields();
	}
	
	private void setFieldVariables() 
	{
		taskName = (EditText) findViewById(R.id.edit_task_name);
		taskDescription = (EditText) findViewById(R.id.edit_task_description);
		taskStatusSpinner = (Spinner) findViewById(R.id.edit_task_status_spinner);
	}

	private void setupSpinner() 
	{
		List<String> legalStatuses = database.getTaskStatusValues();
		statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, legalStatuses);
		statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		taskStatusSpinner.setAdapter(statusAdapter);
	}

	private void createTask() 
	{
		long planId = getIntent().getLongExtra("planId",0);
		long taskId = getIntent().getLongExtra("taskId", 0);
		this.task = database.getTask(taskId);
		if(task.getPlanId()==0)
			task.setPlanId(planId);
	}

	private void populateTaskDataFields() 
	{
		taskName.setText(task.getName());
		taskDescription.setText(task.getDescription());
		int spinnerPosition = statusAdapter.getPosition(task.getStatus());
		taskStatusSpinner.setSelection(spinnerPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
	    {
			case R.id.save_button:
				updateTaskData();
				saveChanges();
//				showTaskDetails();
				break;
	    }
		return true;
	}

	private void updateTaskData() 
	{
		task.setName(taskName.getText().toString());
		task.setDescription(taskDescription.getText().toString());
		task.setStatus(taskStatusSpinner.getSelectedItem().toString());
	}

	private void saveChanges() 
	{
		if(fieldsAreOk() && !isDefault())
		{
			long taskId = database.writeToDatabase(task);
			task.setId(taskId);
			showTaskDetails();
		}
		else if(fieldsAreOk() && isDefault())
		{
			if(task.isOpen())
			{
				long taskId = database.writeToDatabase(task);
				task.setId(taskId);
				showTaskDetails();
				return;
			}
			
			Task newDefaultTask = null;
			Plan taskPlan = task.getPlan();
			
			for(Task currentTask: taskPlan.getTasks())
			{
				if(!(currentTask.getId()==task.getId()) && currentTask.isOpen())
				{
					newDefaultTask=currentTask;
					break;
				}
			}
			
			if(newDefaultTask!=null)
			{
				taskPlan.setPrimaryTask(newDefaultTask);
				long taskPlanId = database.writeToDatabase(taskPlan);
				taskPlan.setId(taskPlanId);
				
				long taskId = database.writeToDatabase(task);
				task.setId(taskId);
				showTaskDetails();
			}
			else
			{
				//HANDLE CLOSING OF PLAN
				buildAlertDialog();
				alertDialog.show();
			}
			
//			long taskId = database.writeToDatabase(task);
//			task.setId(taskId);
		}
	}
	
	private void buildAlertDialog()
	{
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.app_name);
		alertDialog.setMessage(R.string.closing_task_close_plan);
		
		alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				Plan taskPlan = task.getPlan();
				
				taskPlan.removePrimaryTask();
				taskPlan.setStatus("Closed");
				long taskPlanId = database.writeToDatabase(taskPlan);
				taskPlan.setId(taskPlanId);
				
				long taskId = database.writeToDatabase(task);
				task.setId(taskId);
				
				showTaskDetails();
			}
		});
		
		alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				Plan taskPlan = task.getPlan();
				
				taskPlan.removePrimaryTask();
				long taskPlanId = database.writeToDatabase(taskPlan);
				taskPlan.setId(taskPlanId);
				
				long taskId = database.writeToDatabase(task);
				task.setId(taskId);
				
				showTaskDetails();
			}
		});
	}

	private boolean fieldsAreOk() 
	{
		return task.getName().length()>0 && task.getPlanId()>0;
	}
	
	public boolean isDefault()
	{
		return task.getPlan().getPrimaryTaskId()==task.getId();
	}
	
	private void showTaskDetails() 
	{
		Intent showTaskDetails = new Intent(this, TaskDetailActivity.class);
		showTaskDetails.putExtra("planId", task.getPlanId());
		showTaskDetails.putExtra("taskId", task.getId());
		showTaskDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(showTaskDetails);
	}
	
}
