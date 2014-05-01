package no.hiof.trace.activity;

import no.hiof.trace.adapter.SectionPagerAdapter;
import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.fragment.TaskPlayerFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends FragmentActivity implements OnTaskLoadedListener
{
	SectionPagerAdapter pageInteractionAdapter;
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Preparing the adapter connecting the pager
		pageInteractionAdapter = new SectionPagerAdapter(getSupportFragmentManager(), this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pageInteractionAdapter);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		viewPager.setOnPageChangeListener(null); 
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		setViewPagerListener();
	}
	
	/**
	 * Setting a listener to get notifications when a new page is set active
	 * in the pager. When a new page is active, the adapter should call the
	 * underlying fragments refresh data method.
	 */
	private void setViewPagerListener() 
	{
		viewPager.setOnPageChangeListener(new OnPageChangeListener() 
		{
			
			@Override
			public void onPageSelected(int position) 
			{
				pageInteractionAdapter.updateFragmentOnSection(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	/* 
	 * This method is a callback for fragments in the pager. It receives a task, and loads this task into
	 * the task player.
	 */
	@Override
	public void onTaskLoadedListener(Task task) 
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
	   	TaskPlayerFragment player = (TaskPlayerFragment) fragmentManager.findFragmentById(R.id.task_player);
		player.load(task);
	}
	
}
