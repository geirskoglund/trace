package no.hiof.trace.adapter;

import java.util.Collections;
import java.util.List;

import no.hiof.trace.activity.R;
import no.hiof.trace.db.model.Plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlanListAdapter extends BaseAdapter
{
	List<Plan> plans = Collections.emptyList();
	Context context;
	
	public PlanListAdapter(Context context)
	{
		this.context = context;
	}
	
	@Override
	public int getCount() 
	{
		return plans.size();
	}

	@Override
	public Plan getItem(int index) 
	{
		return plans.get(index);
	}

	@Override
	public long getItemId(int index) 
	{
		return plans.get(index).getId();
	}

	@Override
	public View getView(int index, View view, ViewGroup viewGroup) 
	{
		if(view==null)
		{
			view = LayoutInflater.from(context).inflate(R.layout.listitem_plan, viewGroup, false);
		}
		
		Plan plan = plans.get(index);
		
		TextView planName = (TextView)view.findViewById(R.id.listitem_plan_name);
		TextView planDescription = (TextView)view.findViewById(R.id.listitem_plan_desc);
		
		float alphaOpenFactor = 1f;
		float alphaClosedFactor = 0.5f;
		  
		planName.setAlpha(alphaOpenFactor);
		planDescription.setAlpha(alphaOpenFactor);
		  
		if(planHasClosedStatus(plan))
		{
			planName.setAlpha(alphaClosedFactor);
		    planDescription.setAlpha(alphaClosedFactor);
		}
		  
		planName.setText(plan.getName());
		planDescription.setText(plan.getDescription());
		
		planName.setText(plan.getName());
		planDescription.setText(plan.getDescription());
		
		return view;
	}
	
	public boolean planHasClosedStatus(Plan plan)
	{
		return plan.getStatus().equals("Closed");
	}
	
	public void updatePlans(List<Plan> plans)
	{
		this.plans = plans;
	}
	
	public Plan getPlan(int index)
	{
		return plans.get(index);
	}

}
