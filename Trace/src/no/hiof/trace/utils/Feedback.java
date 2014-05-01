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

/**
 * A class containing static methos for giving the user feedback by different means.
 * @author Trace Inc.
 *
 */
public class Feedback 
{
	public static final long SHORT_VIBRATION = 500;
	public static final long LONG_VIBRATION = 1000;
	
	/**
	 * A helper function for displaying Toast messages to the user.
	 * @param text The text to be displayed in the Tast
	 */
	public static void showToast(String text)
	{
		Context context = TraceApp.getAppContext();
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
	
	/**
	 * A helper function for vibrating a device to give feedback to the user
	 * @param vibrationDuration the duration the device should vibrate
	 */
	public static void vibrateDevice(long vibrationDuration)
	{
		Context context = TraceApp.getAppContext();
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibrationDuration);
	}
	
	/**
	 * A helper function for displaying notifications to the user.
	 * @param iconResource the drawable to be used as an icon in the notification
	 * @param title the title of the notification
	 * @param content the content of the notification
	 * @param intentToStart the intent to start when the user clicks the notification
	 * @param className the class name of the intent that starts when a user clicks the notification
	 */
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
	
	/**
	 * A helper function to build intents for notifications
	 * @param intentClass the class the intent should start
	 * @param extraName the key to the extra information the intent can hold
	 * @param extraValue the value of the extra information the intent can hold
	 * @return
	 */
	public static Intent buildNotificationIntent(Class<?> intentClass, String extraName, long extraValue)
	{
		Intent intent = new Intent(TraceApp.getAppContext(), intentClass);
		intent.putExtra(extraName, extraValue);
		
		return intent;
	}
}
