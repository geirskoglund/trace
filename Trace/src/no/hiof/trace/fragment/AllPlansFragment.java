package no.hiof.trace.fragment;

import no.hiof.trace.activity.R;
import no.hiof.trace.db.DatabaseManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AllPlansFragment extends Fragment
{
	DatabaseManager dm;
	
	public AllPlansFragment(){}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//dm = (DatabaseManager)savedInstanceState.getSerializable("DatabaseManager");
		//Log.d("DM",dm.toString());
		return inflater.inflate(R.layout.fragment_all_plans, container, false);
	}
	
	private void loadAllPlans()
	{
		
	}
	
	public void setDatabaseManager(DatabaseManager dm)
	{
		this.dm = dm;
	}
}
