package no.hiof.trace.activity;

import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	TextView ssidLabel;
	TextView nfcLabel;
	TextView gpsLabel;

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
		
		ssidLabel = (TextView) findViewById(R.id.ssid_label);
		gpsLabel = (TextView) findViewById(R.id.location_label);
		nfcLabel = (TextView) findViewById(R.id.nfc_label);
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
	
	
	private void displayAutoSelect()
	{
		invisibleizeAutoSelect();	
	
		if(plan.getAutoTrigger().equals("(NONE)"))
		{
		}
		else if(plan.getAutoTrigger().equals("SSID"))
		{
			ssidLabel.setVisibility(View.VISIBLE);
			planSSId.setVisibility(View.VISIBLE);
		}
		else if(plan.getAutoTrigger().equals("GPS"))
		{
			gpsLabel.setVisibility(View.VISIBLE);
			planLongitude.setVisibility(View.VISIBLE);
			planLatitude.setVisibility(View.VISIBLE);
		}
		else if(plan.getAutoTrigger().equals("NFC"))
		{
			nfcLabel.setVisibility(View.VISIBLE);
			planNFC.setVisibility(View.VISIBLE);
		}
	}
	
	public void invisibleizeAutoSelect()
	{
		gpsLabel.setVisibility(View.INVISIBLE);
		planLongitude.setVisibility(View.INVISIBLE);
		planLatitude.setVisibility(View.INVISIBLE);
		
		ssidLabel.setVisibility(View.INVISIBLE);
		planSSId.setVisibility(View.INVISIBLE);
		
		nfcLabel.setVisibility(View.INVISIBLE);
		planNFC.setVisibility(View.INVISIBLE);
	}
}
