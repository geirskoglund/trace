package no.hiof.trace.utils;

import no.hiof.trace.db.model.Task;

public class TaskPlayerState 
{
	public static enum State {PLAYING, PAUSED, IDLE};
	
	private State state = TaskPlayerState.State.IDLE;
	private Task activeTask = new Task();
	
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
		this.activeTask = task;
	}
	
	
}
