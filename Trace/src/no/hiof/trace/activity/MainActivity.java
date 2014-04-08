package no.hiof.trace.activity;

import no.hiof.trace.adapter.SectionPagerAdapter;
import no.hiof.trace.db.DatabaseManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity
{
	SectionPagerAdapter pageInteractionAdapter;
	ViewPager viewPager;
	DatabaseManager databaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pageInteractionAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pageInteractionAdapter);
		
		databaseManager = new DatabaseManager(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void viewTaskDetails(View view)
	{
		Intent taskDetails = new Intent("no.hiof.action.TASK_DETAIL");
		startActivity(taskDetails);
	}
	
	public void viewPlanDetails(View view)
	{
		Intent planDetails = new Intent("no.hiof.action.PLAN_DETAIL");
		startActivity(planDetails);
	}
}
