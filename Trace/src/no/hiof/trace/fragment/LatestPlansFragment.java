package no.hiof.trace.fragment;

import java.util.Date;
import java.util.List;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.PlanEditorActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.adapter.PlanListAdapter;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class LatestPlansFragment extends Fragment
{
	private ListView latestPlansListView;
	private PlanListAdapter planListAdapter;
	private final int LATEST_PLANS_QTY = 3;
	
	private List<Plan> latestPlans;
	DatabaseManager database;
	
	public LatestPlansFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_latest_plans, container, false);
		
		setHasOptionsMenu(true);
		
		database = new DatabaseManager(this.getActivity());
		
		planListAdapter = new PlanListAdapter(this.getActivity());
		
		latestPlansListView = (ListView)rootView.findViewById(R.id.latestPlansList);
		latestPlansListView.setAdapter(planListAdapter);
		
		setListViewListener();
		
		return rootView;
	}

	private void setListViewListener() 
	{
		latestPlansListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) 
			{
				Plan selectedPlan = planListAdapter.getPlan(index);
				navigateToPlanDetails(selectedPlan.getId());
				//navigateToPlanDetails(index);
			}
		});
		
		latestPlansListView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) 
			{
				Plan selectedPlan = planListAdapter.getPlan(index);
				String toastText = selectedPlan.getName() + getActivity().getString(R.string.plan_was_activated);
				
				showToast(toastText);
				
				selectedPlan.setLastActivatedTimestamp(new Date());
				
				database.updatePlan(selectedPlan);
				
				return true;
			}
			
		});
	}

	private void showToast(String text)
	{
		Context context = this.getActivity().getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	private void navigateToPlanDetails(long planId) 
	{
		Intent showPlanDetails = new Intent(this.getActivity(),PlanDetailActivity.class);
		showPlanDetails.putExtra("planId", planId);
		startActivity(showPlanDetails);
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    
	    latestPlans = database.getLatestPlans(LATEST_PLANS_QTY);
	    planListAdapter.updatePlans(latestPlans);
	    planListAdapter.notifyDataSetChanged();
     }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
	    inflater.inflate(R.menu.swipe_listings, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{

	    switch (item.getItemId()) {
	        case R.id.add_button:                
	        	Intent newPlanPage = new Intent(this.getActivity(), PlanEditorActivity.class);
		    	startActivity(newPlanPage);
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	} 
	
}
