package no.hiof.trace.view;

import java.lang.ref.WeakReference;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class TimerTextView extends TextView 
{
	long seconds = 0;
	final int MINUTE = 60;
	final int HOUR = MINUTE * 60;
	
	Thread timerThread;
	
	boolean timerRunning = false;

	/**
	 * TimerTextView constructor
	 * @param context
	 */
	public TimerTextView(Context context) 
	{
		super(context);
		setTimerText();
	}

	/**
	 * TimerTextView constructor
	 * @param context
	 * @param attrs
	 */
	public TimerTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		setTimerText();
	}

	/**
	 * TimerTextView constructor
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public TimerTextView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		setTimerText();
	}
	
	@Override
	protected void onDraw (Canvas canvas) 
	{
		super.onDraw(canvas);  
	}
	
	/**
	 * Reset the elapsed time to 0
	 */
	public void reset()
	{
		seconds = 0;
	}
	
	
	/**
	 * Starts the timing by starting a timing thread
	 */
	public void start()
	{
		if(timerRunning) 
			return;
		
		timerRunning = true;
		
		final TimerHandler handler = new TimerHandler(this);
		timerThread = new Thread(new Runnable()
		{
		    @Override
		    public void run() {
		        try {
		            while(true) 
		            {
		            	handler.sendEmptyMessage(0);
		                Thread.sleep(1000);
		            }
		        } catch (InterruptedException e) {
		            return;
		        }
		    }
		});

		timerThread.start();
	}
	
	
	/**
	 * Stops timing by interrupting the timer thread.
	 */
	public void stop()
	{
		if(!timerRunning)
			return;
		
		timerRunning = false;
		timerThread.interrupt();
	}
	
	/**
	 * @return A boolean telling if the timing is currently running
	 */
	public boolean isRunning()
	{
		return timerRunning;
	}
	
	/**
	 * @param startTime the time to start timing from
	 */
	public void setTime(Date startTime)
	{
		if(startTime==null)
		{
			seconds=0;
		}
		else
		{
			Date now = new Date();
			seconds = (now.getTime() - startTime.getTime())/1000;
		}
	}
	
	/**
	 * @param the time to start timing from set as seconds
	 */
	public void setTime(long seconds)
	{
		this.seconds = seconds;
	}
	
	/**
	 * @return the time in seconds
	 */
	public long getTimeInSeconds()
	{
		return this.seconds;
	}
	
	/**
	 * Sets the visible text as dictated by getTimerString()
	 */
	public void setTimerText()
	{
		this.setText(getTimerString());
	}
	
	/**
	 * @return the current elapsed time. Format: 01:/15:37
	 */
	public String getTimerString()
	{
		long currentValue = seconds;
		int hrs, mins, secs;
		
		hrs = (int) currentValue/HOUR;
		currentValue = currentValue%HOUR;
		
		mins = (int)currentValue/MINUTE;
		secs = (int)currentValue%MINUTE;
		
		if(hrs>0)
			return String.format("%s:%s:%s", leadingZero(hrs),leadingZero(mins),leadingZero(secs));
		else 
			return String.format("%s:%s", leadingZero(mins),leadingZero(secs));
	}
	
	/**
	 * Adds a leading zero to a number if it is lower than 10
	 * 
	 * @param number the number to add a leading zero to
	 * @return the input number with a leading zero added
	 */
	public String leadingZero(int number)
	{
		if(number<10)
			return "0"+number;
		else
			return ""+number;
					
	}
	
	static class TimerHandler extends Handler
	{
		WeakReference<TimerTextView> view;
		
		/**Constructor for TimerHandler
		 * @param view the view to create a WeakReference to
		 */
		TimerHandler(TimerTextView view)
		{
			this.view= new WeakReference<TimerTextView>((TimerTextView)view);
		}
		
		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
    	public void handleMessage(Message msg) 
    	{
			TimerTextView view = this.view.get();
    		view.setTimerText();
    		view.seconds++;
    	}
	}
}
