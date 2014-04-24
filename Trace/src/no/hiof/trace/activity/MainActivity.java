package no.hiof.trace.activity;

import no.hiof.trace.adapter.SectionPagerAdapter;
import no.hiof.trace.contract.OnTaskLoadedListener;
import no.hiof.trace.db.model.Task;
import no.hiof.trace.fragment.CurrentPlanFragment;
import no.hiof.trace.fragment.TaskPlayerFragment;
import no.hiof.trace.utils.Feedback;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity implements OnTaskLoadedListener
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

	@Override
	public void onTaskLoadedListener(Task task) 
	{
		FragmentManager fragmentManager = getSupportFragmentManager();
	   	TaskPlayerFragment player = (TaskPlayerFragment) fragmentManager.findFragmentById(R.id.task_player);
		player.load(task);
	}
	
}
