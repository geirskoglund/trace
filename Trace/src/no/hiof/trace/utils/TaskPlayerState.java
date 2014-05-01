package no.hiof.trace.utils;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.model.Task;
import android.content.Context;
import android.content.Intent;

public class TaskPlayerState
{
	public static enum State {PLAYING, PAUSED, IDLE};
	public static enum Transition {TASK_IS_IN_DB, TASK_IS_NOT_IN_DB, BUTTON_PRESS, AUTO_PLAY_INVOKED};
	public final static String REFRESH_DATA_INTENT = "no.hiof.trace.REFRESH_DATA_INTENT"; 
	
	private State state = TaskPlayerState.State.IDLE;
	private Task activeTask = new Task();
	private Interval activeInterval = new Interval(); 
	private Context context;
	
	public TaskPlayerState() 
	{
		context = TraceApp.getAppContext();
		Interval interval = TraceApp.database().getNewestInterval();
		if(interval != null)
		{
			this.activeInterval = interval;
			this.activeTask = interval.getTask();
			
			if(activeInterval.isCompleted())
				state=State.PAUSED;
			else if(activeInterval.isRunning())
				state=State.PLAYING;
		}
		
		notifyUpdate();
	}
	
	public State getPlayerState()
	{
		return state;
	}
	
	private void notifyUpdate()
	{
		TraceApp.getAppContext().sendBroadcast(new Intent(REFRESH_DATA_INTENT));
	}
	
	public void setPlayerState(State state)
	{
		this.state=state;
		notifyUpdate();
	}
	
	public Task getActiveTask()
	{
		return activeTask;
	}
	
	public void setActiveTask(Task task)
	{
		stopInterval();
		this.state = task.existsInDatabase() ? State.PAUSED : State.IDLE;
		this.activeTask = task;
		notifyUpdate();
	}
	
	public void startInterval()
	{
//		if(!activeInterval.isRunning())
//		{
//			activeInterval = TraceApp.database().createIntervalWithStartTime(this.activeTask.getId());
//			this.state = State.PLAYING;
//			
//			notifyUpdate();
//		}
		startInterval(false);
	}
	
	public void startInterval(boolean autoRegister)
	{
		if(!activeInterval.isRunning())
		{
			activeInterval = TraceApp.database().createIntervalWithStartTime(this.activeTask.getId());
			activeInterval.setAutoRegistered(autoRegister);
			this.state = State.PLAYING;
			
			notifyUpdate();
		}
	}
	
	public void stopInterval()
	{
		if(activeInterval.isRunning())
		{
			activeInterval.stop();
			completeStoppingTasks();
		}
	}
	
	public void stopInterval(long elapsedSeconds)
	{
		if(activeInterval.isRunning())
		{
			activeInterval.stop(elapsedSeconds);
			completeStoppingTasks();
		}
	}
	
	private void completeStoppingTasks()
	{
		this.state = State.PAUSED;
		TraceApp.database().writeToDatabase(activeInterval);
		Feedback.showToast(context.getString(R.string.interval_saved));
		
		notifyUpdate();
	}
	
	public Interval getCurrentInterval()
	{
		return activeInterval;
	}
}
