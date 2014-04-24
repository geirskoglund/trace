package no.hiof.trace.contract;

import no.hiof.trace.db.model.Task;

public interface OnTaskLoadedListener 
{
	public void onTaskLoadedListener(Task task);
}
