package no.hiof.trace.activity;

import java.util.Locale;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.fragment.AllPlansFragment;
import no.hiof.trace.fragment.CurrentPlanFragment;
import no.hiof.trace.fragment.LatestPlansFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
/*
//Start testing
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//End testing
*/


public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	DatabaseManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),dm);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		///////////TESTING//////////////////
		dm = new DatabaseManager(this);
		//dm.getTables();
		//dm.getAllPlanStatus();
		//dm.getAllTaskStatus();
		//addPlans();
		//dm.getAllPlans();
		//Log.d("Plan 1",dm.getPlan(1).toString());
		//////////END TESTING///////////////
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		DatabaseManager dm = null;
		
		public SectionsPagerAdapter(FragmentManager fm, DatabaseManager dm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			Fragment fragment = null;
			//Bundle bundle = new Bundle();
			//bundle.putSerializable("DatabaseManager", (Serializable)dm);
			switch(position)
			{
				case 0:
					fragment = new CurrentPlanFragment();
					break;
				case 1:
					fragment = new LatestPlansFragment();
					break;
				case 2:
					fragment = new AllPlansFragment();
					break;
				default:
					fragment = new CurrentPlanFragment();
			}
			
			//fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount()
		{
			return 3; //Three main fragments shown in the main activity.
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_current_section).toUpperCase(l);
				case 1:
					return getString(R.string.title_latest_plans_section).toUpperCase(l);
				case 2:
					return getString(R.string.title_all_plans_section).toUpperCase(l);
			}
			return null;
		}
	}
	
//////////////START TESTING////////////////////////
	private void addPlans()
	{
		//dm.addPlan(new Plan("Jobb","Plan for jobb",true));
		//dm.addPlan(new Plan("Fotball","Plan for trenerjobb",false));
	
	}

	private void addStatuses()
	{
		//dm.addPlanStatus("Plan Closed");
		//dm.addPlanStatus("Plan Archived");
		//dm.addPlanStatus("Plan Open");
		//dm.addPlanStatus("Plan Pending");
		//dm.addPlanStatus("Plan Paused");
		/*
		dm.addTaskStatus("Task Closed");
		dm.addTaskStatus("Task Archived");
		dm.addTaskStatus("Task Open");
		dm.addTaskStatus("Task Pending");
		dm.addTaskStatus("Task Paused");*/
	}

	private void deleteStatus()
	{
		//dm.deletePlanStatus("Plan Paused", "Plan Archived");
		//dm.deletePlanStatus("Plan Archived");
	}

/////////////////END TESTING////////////////////////////
}
