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
		
		taskName.setText(task.getName());
		taskDescription.setText(task.getDescription());
		
		return view;
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
