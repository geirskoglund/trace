package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Task;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
		long taskId = getIntent().getLongExtra("taskId", 0);
		this.task = database.getTask(taskId);
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
	
	
}
