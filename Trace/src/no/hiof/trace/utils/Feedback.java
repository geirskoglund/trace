package no.hiof.trace.utils;

import no.hiof.trace.application.TraceApp;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
	
	public static void showSmallNotification(int iconResource, String title, String content, Intent intentToStart, Class<?> className)
	{
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(TraceApp.getAppContext());
		notificationBuilder.setSmallIcon(iconResource);
		notificationBuilder.setContentTitle(title);
		notificationBuilder.setContentText(content);
		notificationBuilder.setAutoCancel(true);
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(TraceApp.getAppContext());
		stackBuilder.addParentStack(className);
		stackBuilder.addNextIntent(intentToStart);
		
		PendingIntent pendingintent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(pendingintent);
		
		NotificationManager notificationManager = (NotificationManager) TraceApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0,notificationBuilder.build());
	}
	
	public static Intent buildNotificationIntent(Class<?> intentClass, String extraName, long extraValue)
	{
		Intent intent = new Intent(TraceApp.getAppContext(), intentClass);
		intent.putExtra(extraName, extraValue);
		
		return intent;
	}
}
