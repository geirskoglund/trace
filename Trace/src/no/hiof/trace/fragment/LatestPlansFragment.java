package no.hiof.trace.fragment;

import java.util.Date;
import java.util.List;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.PlanEditorActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.adapter.PlanListAdapter;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.contract.DatasetRefresh;
import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.utils.Feedback;
import android.app.Activity;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class LatestPlansFragment extends Fragment implements DatasetRefresh
{
	OnTaskLoadedListener taskLoaderListener;
	private ListView latestPlansListView;
	private PlanListAdapter planListAdapter;
	private final int LATEST_PLANS_QTY = 3;
	
	private List<Plan> latestPlans;
	DatabaseManager database = TraceApp.database();
	
	public LatestPlansFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_latest_plans, container, false);
		
		setHasOptionsMenu(true);
		setupAdapter(view);
		setListViewListener();
		
		return view;
	}

	private void setupAdapter(View view) 
	{
		planListAdapter = new PlanListAdapter(this.getActivity());
		latestPlansListView = (ListView)view.findViewById(R.id.latestPlansList);
		latestPlansListView.setAdapter(planListAdapter);
		
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
				if(selectedPlan.isOpen())
				{
					String toastText = getActivity().getString(R.string.capital_plan)+"\"" + selectedPlan.getName() + "\"" + getActivity().getString(R.string.plan_was_activated);
					
					Feedback.showToast(toastText);
					
					selectedPlan.setLastActivatedTimestamp(new Date());
					
					database.updatePlan(selectedPlan);
					taskLoaderListener.onTaskLoadedListener(selectedPlan.getPrimaryTask());
					
					return true;
				}
				
				Feedback.showToast(getActivity().getString(R.string.cannot_set_a_closed_plan_as_active));
				Feedback.vibrateDevice(Feedback.SHORT_VIBRATION);
				
				return true;
			}
			
		});
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
	
	@Override
    public void onAttach(Activity activity) 
	{
        super.onAttach(activity);
        
        try
        {
        	taskLoaderListener = (OnTaskLoadedListener) activity;
        }
        catch(ClassCastException e)
        {
        	throw new ClassCastException(activity.toString() + " must implement OnTaskLoadedListener.");
        }
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}
}
