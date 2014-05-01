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

	public TimerTextView(Context context) 
	{
		super(context);
		setTimerText();
	}

	public TimerTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		setTimerText();
	}

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
	
	public void reset()
	{
		seconds = 0;
	}
	
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
	
	public void stop()
	{
		if(!timerRunning)
			return;
		
		timerRunning = false;
		timerThread.interrupt();
	}
	
	public boolean isRunning()
	{
		return timerRunning;
	}
	
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
	
	public void setTime(long seconds)
	{
		this.seconds = seconds;
	}
	
	public long getTimeInSeconds()
	{
		return this.seconds;
	}
	
	public void setTimerText()
	{
		this.setText(getTimerString());
	}
	
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
		
		TimerHandler(TimerTextView view)
		{
			this.view= new WeakReference<TimerTextView>((TimerTextView)view);
		}
		
		@Override
    	public void handleMessage(Message msg) 
    	{
			TimerTextView view = this.view.get();
    		view.setTimerText();
    		view.seconds++;
    	}
		
		
	}
}
