package no.hiof.trace.fragment;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.PlanEditorActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CurrentPlanFragment extends Fragment
{
	private Plan currentPlan;
	private DatabaseManager database;
	private View rootView;
	
	public CurrentPlanFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.fragment_current_plan, container, false);
		
		setHasOptionsMenu(true);
		
		//TraceApp trace = (TraceApp)getActivity().getApplication();

		return rootView;
	}
	
	private void setFieldValues(View view) 
	{
		TextView planName = (TextView) view.findViewById(R.id.current_plan_name);
		TextView planDescription = (TextView) view.findViewById(R.id.current_plan_description);
		
		planName.setText(currentPlan.getName());
		planDescription.setText(currentPlan.getDescription());
	}

	public void viewTaskDetails(View view)
	{
		Intent intent = new Intent("no.hiof.trace.activity.TaskDetailActivity");
		startActivity(intent);
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    
		database = new DatabaseManager(this.getActivity());
		currentPlan = database.getActivePlan();
		setFieldValues(rootView);
	    
     }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
	    inflater.inflate(R.menu.swipe_current, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{

	    switch (item.getItemId()) {
	    case R.id.add_button:
	    	Log.d("TRACE-MA", "+ was pressed.");
	    	Intent newPlanPage = new Intent(this.getActivity(), PlanEditorActivity.class);
	    	startActivity(newPlanPage);
	    	return true;
	    case R.id.detail_button:
	    	Log.d("TRACE-MA", "Detail was pressed.");
	    	Intent detailPlanPage = new Intent(this.getActivity(),PlanDetailActivity.class);
	    	detailPlanPage.putExtra("planId", currentPlan.getId());
	    	startActivity(detailPlanPage);
	    	return true;
	    case R.id.edit_button:
	    	Log.d("TRACE-MA", "Edit was pressed.");
	    	Intent detailEditPage = new Intent(this.getActivity(),PlanEditorActivity.class);
	    	detailEditPage.putExtra("planId", currentPlan.getId());
	    	startActivity(detailEditPage);
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	} 
}
