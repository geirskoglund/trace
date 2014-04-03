package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CurrentPlanFragment extends Fragment
{
	public CurrentPlanFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_current_plan, container, false);
		return rootView;
	}
	
	public void viewTaskDetails(View view)
	{
		Intent intent = new Intent("no.hiof.trace.activity.TaskDetailActivity");
		startActivity(intent);
	}
}
