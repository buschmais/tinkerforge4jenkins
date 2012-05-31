package com.buschmais.tinkerforge4jenkins.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.JsonNode;

import com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay.DualRelayBrickletNotifier;
import com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4.LCD20x4BrickletNotifier;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;

public class StatusPublisher {

	private static final String host = "localhost";
	private static final int port = 4223;

	public static void main(String[] args) throws Exception {
		new StatusPublisher().publish();
	}

	public void publish() throws Exception {
		IPConnection ipcon = new IPConnection(host, port);
		ipcon.enumerate(new EnumerateListener() {
			@Override
			public void enumerate(String uid, String name, short stackID,
					boolean isNew) {
				System.out.println(uid + " (" + name.trim() + ")");

			}
		});
		BrickletLCD20x4 lcd = new BrickletLCD20x4("8M9");
		ipcon.addDevice(lcd);
		BrickletDualRelay relay = new BrickletDualRelay("7x1");
		ipcon.addDevice(relay);
		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(1);
		List<BrickletNotifier> notifiers = new ArrayList<BrickletNotifier>();
		notifiers.add(new DualRelayBrickletNotifier(relay));
		notifiers.add(new LCD20x4BrickletNotifier(lcd));
		scheduledExecutorService.scheduleAtFixedRate(new Publisher(notifiers),
				0, 30, TimeUnit.SECONDS);
		ipcon.joinThread();
	}

	private static class Publisher implements Runnable {

		private List<BrickletNotifier> notifiers;

		Publisher(List<BrickletNotifier> notifier) {
			this.notifiers = notifier;
		}

		@Override
		public void run() {
			System.out.println("Updating status...");
			for (BrickletNotifier notifier : notifiers) {
				notifier.preUpdate();
			}
			List<JobState> states = null;
			try {
				states = getJobStates();
			} catch (Exception e) {
				for (BrickletNotifier notifier : notifiers) {
					notifier.updateFailed(e.getMessage());
				}
				e.printStackTrace();
			}
			if (states != null) {
				System.out.println(states);
				for (JobState state : states) {
					for (BrickletNotifier notifier : notifiers) {
						notifier.update(state);
					}
				}
			}
			for (BrickletNotifier notifier : notifiers) {
				notifier.postUpdate();
			}
		}

		private List<JobState> getJobStates() throws IOException {
			List<JobState> result = new ArrayList<JobState>();
			JenkinsJsonReader jsonReader = new JenkinsJsonReader();
			String url = "http://localhost:9090/jenkins";
			JsonNode node = jsonReader.read(url);
			JsonNode jobsNode = node.get("jobs");
			if (jobsNode != null) {
				for (JsonNode jobNode : jobsNode) {
					String jobName = jobNode.get("name").getTextValue();
					String jobUrl = jobNode.get("url").getTextValue();
					JsonNode lastBuildNode = jsonReader.read(jobUrl
							+ "lastBuild");
					String buildState = lastBuildNode.get("result")
							.getTextValue();
					JobState state = new JobState();
					state.setName(jobName);
					if (buildState != null) {
						state.setBuildState(BuildState.valueOf(buildState));
					} else {
						state.setBuildState(BuildState.UNKNOWN);
					}
					result.add(state);
				}
			}
			return result;
		}
	}
}
