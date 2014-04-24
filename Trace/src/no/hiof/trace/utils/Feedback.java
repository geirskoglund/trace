package no.hiof.trace.utils;

import no.hiof.trace.application.TraceApp;
import android.content.Context;
import android.widget.Toast;

public class Feedback 
{
	public static void showToast(String text)
	{
		Context context = TraceApp.getAppContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
