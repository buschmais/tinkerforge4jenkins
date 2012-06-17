package com.buschmais.tinkerforge4jenkins.client;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.tinkerforge.Device;

/**
 * 
 * @author dirk.mahler
 * 
 */
public class StatusPublisher implements Runnable {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(StatusPublisher.class);

	/**
	 * The collection of {@link NotifierDevice}s.
	 */
	private Collection<NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices;

	/**
	 * The {@link JenkinsHttpClient}.
	 */
	private JenkinsHttpClient jenkinsHttpClient;

	/**
	 * Constructor.
	 * 
	 * @param jenkinsHttpClient
	 *            The {@link JenkinsHttpClient}.
	 * @param notifierDevices
	 *            The collection of {@link NotifierDevice}s.
	 */
	public StatusPublisher(
			JenkinsHttpClient jenkinsHttpClient,
			Collection<NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices) {
		this.notifierDevices = notifierDevices;
		this.jenkinsHttpClient = jenkinsHttpClient;
	}

	@Override
	public void run() {
		LOGGER.debug("Updating status from Jenkins.");
		for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
			notifierDevice.preUpdate();
		}
		List<JobState> states = null;
		try {
			states = jenkinsHttpClient.getJobStates();
		} catch (IOException e) {
			LOGGER.warn("Cannot get job states.", e);
			for (NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifierDevice : notifierDevices) {
				notifierDevice.updateFailed(e.getMessage());
			}
		}
		if (states != null) {
			LOGGER.debug(states.toString());
			LOGGER.debug("Publishing status to devices.");
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
