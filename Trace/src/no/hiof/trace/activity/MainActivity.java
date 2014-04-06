package no.hiof.trace.activity;

import java.util.Locale;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import no.hiof.trace.fragment.AllPlansFragment;
import no.hiof.trace.fragment.CurrentPlanFragment;
import no.hiof.trace.fragment.LatestPlansFragment;
import android.content.Intent;
import no.hiof.trace.fragment.AllPlansFragment;
import no.hiof.trace.fragment.CurrentPlanFragment;
import no.hiof.trace.fragment.LatestPlansFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class MainActivity extends FragmentActivity
{
	SectionsPagerAdapter sectionsPagerAdapter;
	ViewPager viewPager;
	DatabaseManager dm;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);
		
		dm = new DatabaseManager(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch(position)
			{
				case 0:
					return new CurrentPlanFragment();
				case 1:
					return new LatestPlansFragment();
				case 2:
					return new AllPlansFragment();
			}
			
			//An error occurs. Must handle differently
			return null;
		}

		@Override
		public int getCount()
		{
			return 3; //Three fragments
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
					return getString(R.string.title_latest_section).toUpperCase(l);
				case 2:
					return getString(R.string.title_all_section).toUpperCase(l);
			}
			//An error occurs. Must handle differently
			return null;
		}
	}

	public void viewTaskDetails(View view)
	{
		Intent intent = new Intent("no.hiof.action.TASK_DETAIL");
		startActivity(intent);
	}
	
	public void viewPlanDetails(View view)
	{
		Intent intent = new Intent("no.hiof.action.PLAN_DETAIL");
		startActivity(intent);
	}
}
