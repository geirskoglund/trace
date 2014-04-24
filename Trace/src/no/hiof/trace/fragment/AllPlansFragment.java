package no.hiof.trace.fragment;

import java.util.Date;
import java.util.List;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.PlanEditorActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.adapter.PlanListAdapter;
import no.hiof.trace.application.TraceApp;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.utils.Feedback;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AllPlansFragment extends Fragment
{
	OnTaskLoadedListener taskLoaderListener;
	private ListView allPlansListView;
	private PlanListAdapter planListAdapter;
	
	List<Plan> allPlans;
	DatabaseManager database;
	
	public AllPlansFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_all_plans, container, false);
		
		setHasOptionsMenu(true);
		
		database = new DatabaseManager(this.getActivity());
		
		planListAdapter = new PlanListAdapter(this.getActivity());
		
		allPlansListView = (ListView)v.findViewById(R.id.allPlansList);
		allPlansListView.setAdapter(planListAdapter);
		
		setListViewListener();
		
		return v;
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
				String toastText = selectedPlan.getName() + getActivity().getString(R.string.plan_was_activated);
				
				Feedback.showToast(toastText);
				
				selectedPlan.setLastActivatedTimestamp(new Date());
				
				database.updatePlan(selectedPlan);
				taskLoaderListener.onTaskLoadedListener(selectedPlan.getPrimaryTask());
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
}
