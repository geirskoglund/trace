package no.hiof.trace.activity;

import java.util.List;

import no.hiof.trace.adapter.SectionPagerAdapter;
import no.hiof.trace.db.DatabaseManager;
import no.hiof.trace.db.model.Plan;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity
{
	SectionPagerAdapter pageInteractionAdapter;
	ViewPager viewPager;
	//DatabaseManager databaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pageInteractionAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pageInteractionAdapter);
	}
	
}
