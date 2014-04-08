package no.hiof.trace.utils;

import android.view.View;
import android.view.View.OnClickListener;

public class GenericEventListener implements OnClickListener 
{

	private int callerId;
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		this.callerId = v.getId();
	}
	
	public int callerId()
	{
		return callerId;
	}
	
}
