package no.hiof.trace.activity;

import no.hiof.trace.adapter.TaskListAdapter;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PlanDetailActivity extends Activity
{
	DatabaseManager database;
	Plan plan;
	
	private ListView tasksListView;
	private TaskListAdapter taskListAdapter;
	
	TextView planName;
	TextView planDescription;
	TextView firstAutoLabel;
	TextView secondAutoLabel;
	ImageView planStatus;
	CheckBox planAuto;
	
	TextView autoTypeLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_detail);
		
		database = new DatabaseManager(this);
		setFieldVariables();
		makeStatusReadOnly();
		fetchPlan();
		displayPlan();
		displayAutoSelect();
		setupAdapter();
		setListViewListeners();
	}

	private void setFieldVariables() 
	{
		planName = (TextView)findViewById(R.id.plan_detail_name);
		planDescription = (TextView)findViewById(R.id.plan_detail_description);
		planStatus = (ImageView)findViewById(R.id.status_icon);
		firstAutoLabel = (TextView)findViewById(R.id.first_auto);
		secondAutoLabel = (TextView)findViewById(R.id.second_auto);
		planAuto = (CheckBox)findViewById(R.id.plan_detail_auto);
		
		autoTypeLabel = (TextView) findViewById(R.id.auto_label);
	}

	private void makeStatusReadOnly() 
	{
		planAuto.setEnabled(false);
	}

	private void fetchPlan() 
	{
		long planId = getIntent().getLongExtra("planId", 0);
		plan = database.getPlan(planId) ;
	}

	private void displayPlan() 
	{
		planName.setText(plan.getName());
		planDescription.setText(plan.getDescription());
		planAuto.setChecked(plan.getAutoRegister());
		setStatusIconVisibility(plan.getStatus());
	}
	
	private void setStatusIconVisibility(String status)
	{
		if(status.equals("Open"))
		{
			planStatus.setVisibility(View.INVISIBLE);
		}
		else
		{
			planStatus.setVisibility(View.VISIBLE);
		}
	}
	
	private void setupAdapter()
	{
		taskListAdapter = new TaskListAdapter(this);
		tasksListView = (ListView) this.findViewById(R.id.planTasksList);
		tasksListView.setAdapter(taskListAdapter);
	}
	
	private void setListViewListeners()
	{
		tasksListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) 
			{
				Task selectedTask = taskListAdapter.getTask(index);
				
				Intent showTaskDetail = new Intent(TraceApp.getAppContext() , TaskDetailActivity.class);
				showTaskDetail.putExtra("taskId", selectedTask.getId());
				showTaskDetail.putExtra("planId", plan.getId());
				startActivity(showTaskDetail);
			}	
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.plan_detail, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{

	    switch (item.getItemId()) {
	        case R.id.edit_button:                
	        	Intent editPlanPage = new Intent(this, PlanEditorActivity.class);
		    	editPlanPage.putExtra("planId", plan.getId());
	        	startActivity(editPlanPage);
	            return true;
	        case R.id.task_editor_button:
	        	Intent newTaskPage = new Intent(this,TaskEditorActivity.class);
	        	newTaskPage.putExtra("planId", plan.getId());
	        	startActivity(newTaskPage);
	        	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
	private void displayAutoSelect()
	{
		invisibleizeAutoSelect();	
		
		if(plan.getAutoTrigger().equals("(NONE)"))
		{
			autoTypeLabel.setText("No auto selection");
		}
		else if(plan.getAutoTrigger().equals("WiFi"))
		{
			autoTypeLabel.setText("WiFi");
			firstAutoLabel.setText(plan.getSsid());
			
			autoTypeLabel.setVisibility(View.VISIBLE);
			firstAutoLabel.setVisibility(View.VISIBLE);
		}
		else if(plan.getAutoTrigger().equals("GPS"))
		{
			autoTypeLabel.setText("GPS");
			firstAutoLabel.setText(""+plan.getLat());
			secondAutoLabel.setText(""+plan.getLon());
			
			autoTypeLabel.setVisibility(View.VISIBLE);
			secondAutoLabel.setVisibility(View.VISIBLE);
			firstAutoLabel.setVisibility(View.VISIBLE);
		}
		else if(plan.getAutoTrigger().equals("NFC"))
		{
			autoTypeLabel.setText("NFC-tag");
			firstAutoLabel.setText(plan.getNfc());
			
			autoTypeLabel.setVisibility(View.VISIBLE);
			firstAutoLabel.setVisibility(View.VISIBLE);
		}
	}
	
	public void invisibleizeAutoSelect()
	{
		autoTypeLabel.setVisibility(View.INVISIBLE);
		secondAutoLabel.setVisibility(View.INVISIBLE);
		firstAutoLabel.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    updateTaskData();
     }
	
	private void updateTaskData()
	{
		taskListAdapter.updateTasks(plan.getTasks());
		taskListAdapter.notifyDataSetChanged();
	}
	
}
