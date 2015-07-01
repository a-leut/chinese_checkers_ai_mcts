package gameagent;

import java.util.concurrent.atomic.AtomicBoolean;

public class MoveTimer extends Thread {
	private AtomicBoolean done;
	private int timeout;
	
	public MoveTimer(int duration) {
		done = new AtomicBoolean(false);
		timeout = duration;
	}
	
	public Boolean isDone() {
		return done.get();
	}
	
	@Override
	public void run() {
		try {
			sleep(timeout);
		} catch(InterruptedException ex) {
		}
	    done.set(true);
	}
}