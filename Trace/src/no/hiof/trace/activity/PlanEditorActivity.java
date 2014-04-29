package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.sensor.WifiReciever;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PlanEditorActivity extends Activity implements OnItemSelectedListener
{	
	private Plan plan;
	private DatabaseManager database;
	
	private EditText planName;
	private EditText planDescription;
	private EditText planNFC;
	private EditText planLocation;
	private CheckBox planAuto;
	private Spinner planSSId;
	private Spinner planStatusSpinner;
	private Spinner autoSelectionSpinner;
	
	private TextView ssidLabel;
	private TextView locationLabel;
	private TextView nfcLabel;
	
	
	private ArrayAdapter<String> statusAdapter;
	private ArrayAdapter<String> autoSelectionAdapter;
	private ArrayAdapter<String> ssidAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_editor);
		
		database = new DatabaseManager(this);
		
		setFieldVariables();
		
		hideLabelsAndEditBoxes();
		
		setupSpinner();
		setupAutoSelectionSpinner();
		setupSsidSpinner();
		
		createPlan();
		populatePlanDataFields();
	}

	private void setFieldVariables() 
	{
		planName = (EditText) findViewById(R.id.edit_plan_name);
		planDescription = (EditText) findViewById(R.id.edit_plan_description);
		planSSId = (Spinner) findViewById(R.id.edit_plan_ssid);
		planNFC = (EditText) findViewById(R.id.edit_plan_nfc);
		planLocation = (EditText) findViewById(R.id.edit_plan_location);
		planAuto = (CheckBox) findViewById(R.id.edit_plan_auto);	
		planStatusSpinner = (Spinner) findViewById(R.id.edit_plan_status_spinner);
		autoSelectionSpinner = (Spinner) findViewById(R.id.edit_plan_auto_selection_spinner);
		
		ssidLabel = (TextView) findViewById(R.id.ssid_label);
		locationLabel = (TextView) findViewById(R.id.location_label);
		nfcLabel = (TextView) findViewById(R.id.nfc_label);
	}
	
	private void hideLabelsAndEditBoxes()
	{
		ssidLabel.setVisibility(View.INVISIBLE);
		locationLabel.setVisibility(View.INVISIBLE);
		nfcLabel.setVisibility(View.INVISIBLE);
		
		planSSId.setVisibility(View.INVISIBLE);
		planNFC.setVisibility(View.INVISIBLE);
		planLocation.setVisibility(View.INVISIBLE);
	}

	private void setupSpinner() 
	{
		List<String> legalStatuses = database.getPlanStatusValues();
		statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, legalStatuses);
		statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		planStatusSpinner.setAdapter(statusAdapter);
	}
	
	private void setupAutoSelectionSpinner()
	{
		String[] legalautoSelections = getResources().getStringArray(R.array.auto_select_array);
		autoSelectionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, legalautoSelections);
		autoSelectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		autoSelectionSpinner.setAdapter(autoSelectionAdapter);
		autoSelectionSpinner.setOnItemSelectedListener(this);
	}
	
	private void setupSsidSpinner()
	{
		ssidAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, WifiReciever.getAllStoredSsid());
		ssidAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		planSSId.setAdapter(ssidAdapter);
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
		//planSSId.setText(plan.getSsid());
		planNFC.setText(plan.getNfc());
		planAuto.setChecked(plan.getAutoRegister());
		
		int spinnerPosition = statusAdapter.getPosition(plan.getStatus());
		planStatusSpinner.setSelection(spinnerPosition);
		
		int autoSelectionSpinnerPosition = autoSelectionAdapter.getPosition(plan.getAutoTrigger());
		autoSelectionSpinner.setSelection(autoSelectionSpinnerPosition);
		System.out.println("AUTOTRIGGER: "+plan.getAutoTrigger());
		
		int ssidSpinnerPosition = ssidAdapter.getPosition(plan.getSsid());
		planSSId.setSelection(ssidSpinnerPosition);
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
		plan.setSsid(planSSId.getSelectedItem().toString());
		plan.setNfc(planNFC.getText().toString());
		plan.setAutoRegister(planAuto.isChecked());
		
		plan.setAutoTrigger(autoSelectionSpinner.getSelectedItem().toString());
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

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
	{
		System.out.println("####################################################################");
		System.out.println("CLICKED: "+position);
		System.out.println(autoSelectionAdapter.getItem(position));
		
		if(autoSelectionAdapter.getItem(position).equals("(NONE)"))
		{
			hideLabelsAndEditBoxes();
		}
		else if(autoSelectionAdapter.getItem(position).equals("SSID"))
		{
			hideLabelsAndEditBoxes();
			ssidLabel.setVisibility(View.VISIBLE);
			planSSId.setVisibility(View.VISIBLE);
		}
		else if(autoSelectionAdapter.getItem(position).equals("NFC"))
		{
			hideLabelsAndEditBoxes();
			nfcLabel.setVisibility(View.VISIBLE);
			planNFC.setVisibility(View.VISIBLE);
		}
		else if(autoSelectionAdapter.getItem(position).equals("GPS"))
		{
			hideLabelsAndEditBoxes();
			locationLabel.setVisibility(View.VISIBLE);
			planLocation.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
		// TODO Auto-generated method stub
		
	}
	

}
