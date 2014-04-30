package no.hiof.trace.utils;

import no.hiof.trace.application.TraceApp;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

public class Feedback 
{
	public static final long SHORT_VIBRATION = 500;
	public static final long LONG_VIBRATION = 1000;
	
	public static void showToast(String text)
	{
		Context context = TraceApp.getAppContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	public static void vibrateDevice(long vibrationDuration)
	{
		Context context = TraceApp.getAppContext();
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibrationDuration);
	}
}
