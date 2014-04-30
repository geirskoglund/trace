package no.hiof.trace.adapter;

import java.util.Collections;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.utils.DateHelper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IntervalListAdapter extends BaseAdapter
{
	List<Interval> intervals = Collections.emptyList();
	Context context;
	
	public IntervalListAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return intervals.size();
	}

	@Override
	public Interval getItem(int index) 
	{
		return intervals.get(index);
	}

	@Override
	public long getItemId(int index) 
	{
		return intervals.get(index).getId();
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) 
	{
		if(view==null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.listitem_interval, viewGroup, false);
		}
		
		Interval interval = intervals.get(index);
		
		TextView intervalLabel = (TextView)view.findViewById(R.id.listitem_interval_label);
		TextView intervalTime = (TextView)view.findViewById(R.id.listitem_interval_time);
		
		String dateString = DateHelper.getDateString(interval.getStartTime());
		
		intervalLabel.setText(dateString);
		intervalTime.setText(interval.getElapsedTime().toString());
		return view;
	}
	
	public void updateIntervals(List<Interval> intervals)
	{
		this.intervals = intervals;
	}
	
	public Interval getInterval(int index)
	{
		return intervals.get(index);
	}

}
