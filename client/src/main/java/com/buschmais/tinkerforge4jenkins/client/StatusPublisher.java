package com.buschmais.tinkerforge4jenkins.client;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.tinkerforge.Device;

public class StatusPublisher implements Runnable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(StatusPublisher.class);

	private Collection<NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices;
	private JenkinsHttpClient jenkinsHttpClient;

	public StatusPublisher(
			JenkinsHttpClient jenkinsHttpClient,
			Collection<NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices) {
		this.notifierDevices = notifierDevices;
		this.jenkinsHttpClient = jenkinsHttpClient;
	}

	@Override
	public void run() {
		LOGGER.debug("Updating status...");
		for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
			notifierDevice.preUpdate();
		}
		List<JobState> states = null;
		try {
			states = jenkinsHttpClient.getJobStates();
		} catch (Exception e) {
			LOGGER.warn("Cannot get job states.", e);
			for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
				notifierDevice.updateFailed(e.getMessage());
			}
		}
		if (states != null) {
			LOGGER.debug(states.toString());
			for (JobState state : states) {
				for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
					notifierDevice.update(state);
				}
			}
		}
		for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
			notifierDevice.postUpdate();
		}
	}

}
