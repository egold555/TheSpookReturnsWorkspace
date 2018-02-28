package org.golde.java.game.scheduler;

import java.util.PriorityQueue;

import org.lwjgl.Sys;

/**
 * Can make delayed tasks and execute stuff so we do not hang main game thread.
 * @author Eric
 *
 */
public class Scheduler {

	private PriorityQueue<ScheduledTask> queue = new PriorityQueue<ScheduledTask>();
	
	public ScheduledTask runTaskLater(long when, Runnable what)
	{
		ScheduledTask task = new ScheduledTask(when + getCurrentTime(), false, 0, what);
		queue.add(task);
		return task;
	}
	
	public ScheduledTask runRepeatingTask(long when, long repeatInterval, Runnable what)
	{
		ScheduledTask task = new ScheduledTask(when + getCurrentTime(), true, repeatInterval, what);
		queue.add(task);
		return task;
	}
	
	public void cancelTask(ScheduledTask task)
	{
		queue.remove(task);
	}
	
	public void update()
	{
		long currentTime = getCurrentTime();
		
		while (true) {
			ScheduledTask next = queue.peek();
			if (next != null && next.getTimeToFire() <= currentTime) {
				queue.remove();
				runTask(next);
			}
			else {
				break;
			}
		}
	}
	
	private void runTask(ScheduledTask task)
	{
		task.run();
		
		if (task.isRepeating()) {
			task.delayRepeatTime();
			queue.add(task);
		}
	}
	
	private long getCurrentTime()
	{
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
