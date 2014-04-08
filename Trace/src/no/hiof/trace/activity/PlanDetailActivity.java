package no.hiof.trace.activity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class PlanDetailActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_detail);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plan_detail, menu);
		return true;
	}
	
	public void viewTaskDetails(View view)
	{
		Intent taskDetail = new Intent("no.hiof.action.TASK_DETAIL");
		startActivity(taskDetail);
	}

}
