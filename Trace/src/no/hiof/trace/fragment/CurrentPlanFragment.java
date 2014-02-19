package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.db.DatabaseManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CurrentPlanFragment extends Fragment
{
	DatabaseManager dm;
	
	public CurrentPlanFragment(){}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		return inflater.inflate(R.layout.fragment_current_plan, container, false);
	}
	
}
