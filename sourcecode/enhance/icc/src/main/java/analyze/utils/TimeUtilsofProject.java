package main.java.analyze.utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.DocumentException;

import main.java.MyConfig;
import main.java.client.BaseClient;

public class TimeUtilsofProject {
	/**
	 * Analyze will terminate when timeout.
	 * 
	 * @param client
	 */
	public static void setTotalTimer(BaseClient client) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println(MyConfig.getInstance().getClient() + " time = "
						+ MyConfig.getInstance().getTimeLimit() + " minutes, timeout!");
				MyConfig.getInstance().setStopFlag(true);
				try {
					client.clientOutput();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}, MyConfig.getInstance().getTimeLimit() * 60 * 1000);

	}
}
