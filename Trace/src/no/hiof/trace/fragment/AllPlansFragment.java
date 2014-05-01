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

public class AllPlansFragment extends Fragment implements DatasetRefresh
{
	OnTaskLoadedListener taskLoaderListener;
	private ListView allPlansListView;
	private PlanListAdapter planListAdapter;
	
	List<Plan> allPlans;
	DatabaseManager database = TraceApp.database();
	
	public AllPlansFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_all_plans, container, false);
		
		setHasOptionsMenu(true);
		setupAdapter(view);
		setListViewListener();
		
		return view;
	}
	
	private void setupAdapter(View view) 
	{
		planListAdapter = new PlanListAdapter(this.getActivity());
		allPlansListView = (ListView)view.findViewById(R.id.allPlansList);
		allPlansListView.setAdapter(planListAdapter);
	}

	public void setListViewListener()
	{
		allPlansListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3) 
			{
				Plan selectedPlan = planListAdapter.getPlan(index);
				navigateToPlanDetails(selectedPlan.getId());
			}
		});
		
		allPlansListView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int index, long id) 
			{
				Plan selectedPlan = planListAdapter.getPlan(index);
				
				if(selectedPlan.isOpen())
				{
					String toastText = getActivity().getString(R.string.capital_plan) + "\"" + selectedPlan.getName() + "\"" + getActivity().getString(R.string.plan_was_activated);
					
					Feedback.showToast(toastText);
					
					selectedPlan.setLastActivatedTimestamp(new Date());
					
					database.writeToDatabase(selectedPlan);
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
	    
	    allPlans = database.getAllPlans(); 
		planListAdapter.updatePlans(allPlans);
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
