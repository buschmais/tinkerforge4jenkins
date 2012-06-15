package com.buschmais.tinkerforge4jenkins.client;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JenkinsConfigurationType;
import com.tinkerforge.Device;

public class StatusPublisher implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(StatusPublisher.class);

	private JenkinsConfigurationType jenkinsConfiguration;
	private Collection<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifiers;
	private JenkinsJsonClient jenkinsStatusReader;

	public StatusPublisher(
			JenkinsConfigurationType jenkinsConfiguration,
			Collection<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifier) {
		this.jenkinsConfiguration = jenkinsConfiguration;
		this.notifiers = notifier;
		this.jenkinsStatusReader = new JenkinsJsonClient(jenkinsConfiguration);
	}

	@Override
	public void run() {
		LOGGER.debug("Updating status...");
		for (DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier : notifiers) {
			notifier.preUpdate();
		}
		List<JobState> states = null;
		try {
			states = jenkinsStatusReader.getJobStates(jenkinsConfiguration
					.getUrl());
		} catch (Exception e) {
			LOGGER.warn("Cannot get job states.", e);
			for (DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier : notifiers) {
				notifier.updateFailed(e.getMessage());
			}
		}
		if (states != null) {
			LOGGER.debug(states.toString());
			for (JobState state : states) {
				for (DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier : notifiers) {
					notifier.update(state);
				}
			}
		}
		for (DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier : notifiers) {
			notifier.postUpdate();
		}
	}

}
