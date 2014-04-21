package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class PlanDetailActivity extends Activity
{
	
	DatabaseManager database;
	Plan plan;
	
	TextView planName;
	TextView planDescription;
	TextView planSSId;
	TextView planNFC;
	TextView planLatitude;
	TextView planLongitude;
	TextView planStatus;
	CheckBox planAuto;

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
	}

	private void setFieldVariables() 
	{
		planName = (TextView)findViewById(R.id.plan_detail_name);
		planDescription = (TextView)findViewById(R.id.plan_detail_description);
		planSSId = (TextView)findViewById(R.id.plan_detail_ssid);
		planNFC = (TextView)findViewById(R.id.plan_detail_nfc);
		planStatus = (TextView)findViewById(R.id.plan_detail_status);
		planLatitude = (TextView)findViewById(R.id.plan_detail_lat);
		planLongitude = (TextView)findViewById(R.id.plan_detail_lon);
		planAuto = (CheckBox)findViewById(R.id.plan_detail_auto);
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
		planSSId.setText(plan.getSsid());
		planNFC.setText(plan.getNfc());
		planStatus.setText(plan.getStatus());
		planLatitude.setText(""+plan.getLat());
		planLongitude.setText(""+plan.getLon());
		planAuto.setChecked(plan.getAutoRegister());
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
	
//	public void viewTaskDetails(View view)
//	{
//		Intent taskDetail = new Intent("no.hiof.action.TASK_DETAIL");
//		startActivity(taskDetail);
//	}

}
