package no.hiof.trace.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class TaskDetailActivity extends Activity implements OnClickListener
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_detail);
		System.out.println("Click");
		setListeners();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_detail, menu);
		return true;
	}

	private void setListeners()
	{
		View intervals = (View) findViewById(R.id.intervals);
		intervals.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View eventRaiser)
	{
		handleClicksForView(eventRaiser);
	}
	
	private void handleClicksForView(View view)
	{
		switch(view.getId())
		{
			case R.id.intervals:
				changeBackgroundColor(view);
				break;
		}
	}
	
	public void changeBackgroundColor(View view)
	{
		view.setBackgroundColor(Color.RED);
	}
	
}