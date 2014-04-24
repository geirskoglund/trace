package no.hiof.trace.utils;

import no.hiof.trace.application.TraceApp;
import no.hiof.trace.db.model.Interval;
import no.hiof.trace.db.model.Task;

public class TaskPlayerState 
{
	public static enum State {PLAYING, PAUSED, IDLE};
	public static enum Transition {TASK_IS_IN_DB, TASK_IS_NOT_IN_DB, BUTTON_PRESS, AUTO_PLAY_INVOKED};
	
	private State state = TaskPlayerState.State.IDLE;
	private Task activeTask = new Task();
	private Interval activeInterval = new Interval(); 
	
	public State getPlayerState()
	{
		return state;
	}
	
	public void setPlayerState(State state)
	{
		this.state=state;
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
	}
	
	public void startInterval()
	{
		if(!activeInterval.isRunning())
		{
			activeInterval = TraceApp.database().createIntervalWithStartTime(this.activeTask.getId());
			this.state = State.PLAYING;
		}
	}
	
	public void stopInterval()
	{
		if(activeInterval.isRunning())
		{
			activeInterval.stop();
			this.state = State.PAUSED;
			TraceApp.database().updateInterval(activeInterval);
		}
	}
	
	
}
