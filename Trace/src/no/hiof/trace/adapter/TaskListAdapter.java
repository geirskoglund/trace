package no.hiof.trace.adapter;

import java.util.Collections;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.db.model.Task;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskListAdapter extends BaseAdapter
{
	List<Task> tasks = Collections.emptyList();
	Context context;
	
	public TaskListAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return tasks.size();
	}

	@Override
	public Task getItem(int index) 
	{
		return tasks.get(index);
	}

	@Override
	public long getItemId(int index) 
	{
		return tasks.get(index).getId();
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) 
	{
		if(view==null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.listitem_task, viewGroup, false);
		}
		
		Task task = tasks.get(index);
		
		TextView taskName = (TextView)view.findViewById(R.id.listitem_task_name);
		TextView taskDescription = (TextView)view.findViewById(R.id.listitem_task_desc);
		ImageView defaultIcon = (ImageView)view.findViewById(R.id.listitem_task_default);
		
		float alphaOpenFactor = 1f;
		float alphaClosedFactor = 0.5f;
		  
		taskName.setAlpha(alphaOpenFactor);
		taskDescription.setAlpha(alphaOpenFactor);
		  
		if(!task.isOpen())
		{
			taskName.setAlpha(alphaClosedFactor);
			taskDescription.setAlpha(alphaClosedFactor);
		}
		
		taskName.setText(task.getName());
		taskDescription.setText(task.getDescription());
		defaultIcon.setVisibility(determineDefaultIconVisibility(task));
		
		return view;
	}

	private int determineDefaultIconVisibility(Task task)
	{
		Task defaultPlanTask = task.getPlan().getPrimaryTask();
		
		if(defaultPlanTask==null)
		{
			return View.GONE;
		}
		else if(task.getId() == defaultPlanTask.getId())
		{
			return View.VISIBLE;
		}
		
		return View.GONE;
	}
	
	public void updateTasks(List<Task> tasks)
	{
		this.tasks = tasks;
	}
	
	public Task getTask(int index)
	{
		return tasks.get(index);
	}

}
