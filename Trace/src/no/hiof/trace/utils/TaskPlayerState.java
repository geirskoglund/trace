package no.hiof.trace.utils;

import no.hiof.trace.activity.R;
import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.model.Task;
import android.content.Context;
import android.content.Intent;

/**
 * @author Trace Inc.
 *A class to help keep track of states when registering time
 */
public class TaskPlayerState
{
	public static enum State {PLAYING, PAUSED, IDLE};
	public static enum Transition {TASK_IS_IN_DB, TASK_IS_NOT_IN_DB, BUTTON_PRESS, AUTO_PLAY_INVOKED};
	public final static String REFRESH_DATA_INTENT = "no.hiof.trace.REFRESH_DATA_INTENT"; 
	
	private State state = TaskPlayerState.State.IDLE;
	private Task activeTask = new Task();
	private Interval activeInterval = new Interval(); 
	private Context context;
	
	/**
	 * Constructor
	 */
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
	
	/**
	 * @return the state of the player
	 */
	public State getPlayerState()
	{
		return state;
	}
	
	private void notifyUpdate()
	{
		TraceApp.getAppContext().sendBroadcast(new Intent(REFRESH_DATA_INTENT));
	}
	
	/**
	 * @param state the state to set the player to
	 */
	public void setPlayerState(State state)
	{
		this.state=state;
		notifyUpdate();
	}
	
	/**
	 * @return the currently active Task
	 */
	public Task getActiveTask()
	{
		return activeTask;
	}
	
	/**
	 * @param task the Task to set as active
	 */
	public void setActiveTask(Task task)
	{
		stopInterval();
		this.state = task.existsInDatabase() ? State.PAUSED : State.IDLE;
		this.activeTask = task;
		notifyUpdate();
	}
	
	/**
	 * Starts a time slot interval
	 */
	public void startInterval()
	{
		startInterval(false);
	}
	
	/**
	 * Starts a new interval
	 * @param autoRegister the value of the intervals auto registered field
	 */
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
	
	/**
	 * Stop the active interval
	 */
	public void stopInterval()
	{
		if(activeInterval.isRunning())
		{
			activeInterval.stop();
			completeStoppingTasks();
		}
	}
	
	/**
	 * Stops the active interval at set amount of seconds
	 * @param elapsedSeconds the number of seconds to stop the active interval at
	 */
	public void stopInterval(long elapsedSeconds)
	{
		if(activeInterval.isRunning())
		{
			activeInterval.stop(elapsedSeconds);
			completeStoppingTasks();
		}
	}
	
	/**
	 * Completes the active interval by saving it to the database
	 */
	private void completeStoppingTasks()
	{
		this.state = State.PAUSED;
		TraceApp.database().writeToDatabase(activeInterval);
		Feedback.showToast(context.getString(R.string.interval_saved));
		
		notifyUpdate();
	}
	
	/**
	 * Gets the instance of the active interval
	 * @return the active Interval
	 */
	public Interval getCurrentInterval()
	{
		return activeInterval;
	}
}
