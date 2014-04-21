package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class PlanEditorActivity extends Activity 
{	
	private Plan plan;
	private DatabaseManager database;
	
	private EditText planName;
	private EditText planDescription;
	private EditText planSSId;
	private EditText planNFC;
	private CheckBox planAuto;
	private Spinner planStatusSpinner;
	
	private ArrayAdapter<String> statusAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_editor);
		
		database = new DatabaseManager(this);
		
		setFieldVariables();
		
		setupSpinner();
		
		createPlan();
		populatePlanDataFields();
	}

	private void setFieldVariables() 
	{
		planName = (EditText) findViewById(R.id.edit_plan_name);
		planDescription = (EditText) findViewById(R.id.edit_plan_description);
		planSSId = (EditText) findViewById(R.id.edit_plan_ssid);
		planNFC = (EditText) findViewById(R.id.edit_plan_nfc);
		planAuto = (CheckBox) findViewById(R.id.edit_plan_auto);	
		planStatusSpinner = (Spinner) findViewById(R.id.edit_plan_status_spinner);
	}

	private void setupSpinner() 
	{
		List<String> legalStatuses = database.getPlanStatusValues();
		statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, legalStatuses);
		statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		planStatusSpinner.setAdapter(statusAdapter);
	}

	private void createPlan() 
	{
		long planId = getIntent().getLongExtra("planId", 0);
		this.plan = database.getPlan(planId);
	}

	private void populatePlanDataFields() 
	{
		planName.setText(plan.getName());
		planDescription.setText(plan.getDescription());
		planSSId.setText(plan.getSsid());
		planNFC.setText(plan.getNfc());
		planAuto.setChecked(plan.getAutoRegister());
		
		int spinnerPosition = statusAdapter.getPosition(plan.getStatus());
		planStatusSpinner.setSelection(spinnerPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plan_editor, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
	    {
		case R.id.save_button:
			updatePlanData();
			saveChanges();
			showPlanDetails();
			break;
	    }
		return true;
	}

	private void updatePlanData() 
	{
		plan.setName(planName.getText().toString());
		plan.setDescription(planDescription.getText().toString());
		plan.setSsid(planSSId.getText().toString());
		plan.setNfc(planNFC.getText().toString());
		plan.setAutoRegister(planAuto.isChecked());
		
		plan.setStatus(planStatusSpinner.getSelectedItem().toString());
	}

	private void saveChanges() 
	{
		if(fieldsAreOk())
		{
			//plan.setId(database.addPlan(plan));
			long planId = database.writeToDatabase(plan); // .addPlan(plan);
			plan.setId(planId);
		}
	}

	private boolean fieldsAreOk() 
	{
		return plan.getName().length()>0; //&& plan.getDescription().length()>0;
	}

	private void showPlanDetails() 
	{
		Intent showPlanDetails = new Intent(this,PlanDetailActivity.class);
		showPlanDetails.putExtra("planId", plan.getId());
		showPlanDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(showPlanDetails);
	}
	

}
