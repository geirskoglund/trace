package no.hiof.trace.fragment;

import java.util.List;

import no.hiof.trace.activity.PlanDetailActivity;
import no.hiof.trace.activity.PlanEditorActivity;
import no.hiof.trace.activity.R;
import no.hiof.trace.activity.TaskDetailActivity;
import no.hiof.trace.adapter.TaskListAdapter;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.contract.DatasetRefresh;
import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.utils.Feedback;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CurrentPlanFragment extends Fragment implements DatasetRefresh
{
	OnTaskLoadedListener taskLoaderListener;
	
	private ListView tasksListView;
	private TaskListAdapter taskListAdapter;
	
	private Plan currentPlan;
	private DatabaseManager database;
	
	private TextView planName;
	private TextView planDescription;
	
	private RelativeLayout mainLayout;
	private RelativeLayout overlayLayout;
	private RelativeLayout taskListOverlay;
	
	public CurrentPlanFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_current_plan, container, false);
		
		database = new DatabaseManager(this.getActivity());
		setupListViewAdapter(view);
		setListViewListeners();
		setHasOptionsMenu(true);
		
		setFieldVariables(view);
		updatePlanData();

		return view;
	}
	
	private void setupListViewAdapter(View view)
	{
		taskListAdapter = new TaskListAdapter(this.getActivity());
		tasksListView = (ListView) view.findViewById(R.id.planTasksList);
		tasksListView.setAdapter(taskListAdapter);
	}
	
	private void setFieldVariables(View view)
	{
		planName = (TextView) view.findViewById(R.id.current_plan_name);
		planDescription = (TextView) view.findViewById(R.id.current_plan_description);
		
		mainLayout = (RelativeLayout) view.findViewById(R.id.main);
		overlayLayout = (RelativeLayout) view.findViewById(R.id.overlay);
		taskListOverlay = (RelativeLayout) view.findViewById(R.id.taskListOverlay);
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
				showTaskDetail.putExtra("planId", currentPlan.getId());
				startActivity(showTaskDetail);
			}	
		});
		
		tasksListView.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int index, long arg3) 
			{
				Task selectedTask = taskListAdapter.getTask(index);
				if(selectedTask.isOpen())
				{
					taskLoaderListener.onTaskLoadedListener(selectedTask);
					return true;
				}
				
				Feedback.showToast(getActivity().getString(R.string.cannot_load_a_closed_task));
				Feedback.vibrateDevice(Feedback.SHORT_VIBRATION);
				return true;
			}
		});
	}
	
	@Override
	public void onResume()
    {  
	    super.onResume();
	    updatePlanData();
	    updateTaskData();
     }
	
	private void updatePlanData()
	{
		currentPlan = database.getActivePlan();
		
		setFieldValues();
	}
	
	private void setFieldValues() 
	{
		mainLayout.setVisibility(View.VISIBLE);
		tasksListView.setVisibility(View.VISIBLE);
		overlayLayout.setVisibility(View.INVISIBLE);
		taskListOverlay.setVisibility(View.INVISIBLE);
		
		planName.setText(currentPlan.getName());
		planDescription.setText(currentPlan.getDescription());
		
		if(!currentPlan.existsInDatabase())
		{
			mainLayout.setVisibility(View.GONE);
			overlayLayout.setVisibility(View.VISIBLE);
		}
		else if(currentPlan.getTasks().size()==0)
		{
			taskListOverlay.setVisibility(View.VISIBLE);
		}
	}

	private void updateTaskData()
	{
		taskListAdapter.updateTasks(currentPlan.getTasks());
		taskListAdapter.notifyDataSetChanged();
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
	    switch (item.getItemId()) 
	    {
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
	public void refreshData() 
	{
		//Feedback.showToast("oppdatert");
		if(currentPlan!=null)
		{
			new RefreshPlanDataTask().execute();
		}
	}
	
	private class RefreshPlanDataTask extends AsyncTask<Void, Void, Plan>
	{

		@Override
		protected Plan doInBackground(Void... params) 
		{	
			return database.getActivePlan();
		}

		@Override
		protected void onPostExecute(Plan result) 
		{
			currentPlan = result;
			setFieldValues();
			new RefreshTasklistDataTask().execute(result);
		}
	}
	
	private class RefreshTasklistDataTask extends AsyncTask<Plan, Void, List<Task>>
	{

		@Override
		protected List<Task> doInBackground(Plan... params) 
		{
			return params[0].getTasks();
		}
		
		@Override
		protected void onPostExecute(List<Task> result) 
		{
			taskListAdapter.updateTasks(result);
			taskListAdapter.notifyDataSetChanged();
		}


	
	}
}
