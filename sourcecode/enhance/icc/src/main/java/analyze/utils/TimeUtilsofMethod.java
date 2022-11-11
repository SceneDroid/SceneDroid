package main.java.analyze.utils;

import java.util.Timer;
import java.util.TimerTask;

import main.java.MyConfig;

public class TimeUtilsofMethod {

	private boolean methodTimeout = false;
	private Timer timer;

	public TimeUtilsofMethod() {
		timer = new Timer();
	}

	public void schedule() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				setMethodTimeout(true);
				System.out.println("Skip current method due to timeout of 1 minutes!");
			}
			// }, MyConfig.getInstance().getTimeLimit() * 5 *1000 );
		}, MyConfig.getInstance().getTimeLimit() * 1000);

	}

	public void cancel() {
		timer.cancel();
	}

	/**
	 * @return the methodTimeout
	 */
	public boolean isMethodTimeout() {
		return methodTimeout;
	}

	/**
	 * @param methodTimeout
	 *            the methodTimeout to set
	 */
	public void setMethodTimeout(boolean methodTimeout) {
		this.methodTimeout = methodTimeout;
	}
}
