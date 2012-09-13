package com.buschmais.tinkerforge4jenkins.client;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.registry.NotifierDeviceRegistry;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.AbstractBrickletConfigurationType;
import com.tinkerforge.Device;

/**
 * Scheduled task which polls the current state of a Jenkins server and
 * publishes it to the connected bricklets.
 * 
 * @author dirk.mahler
 */
public class PublisherTask extends Thread {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PublisherTask.class);

	/**
	 * The {@link NotifierDeviceRegistry}.
	 */
	private NotifierDeviceRegistry deviceRegistry;

	/**
	 * The {@link JenkinsHttpClient}.
	 */
	private JenkinsHttpClient jenkinsHttpClient;

	/**
	 * Constructor.
	 * 
	 * @param jenkinsHttpClient
	 *            The {@link JenkinsHttpClient}.
	 * @param deviceRegistry
	 *            The {@link NotifierDeviceRegistry}.
	 */
	public PublisherTask(JenkinsHttpClient jenkinsHttpClient,
			NotifierDeviceRegistry deviceRegistry) {
		this.jenkinsHttpClient = jenkinsHttpClient;
		this.deviceRegistry = deviceRegistry;
	}

	@Override
	public void run() {
		LOGGER.debug("Updating status from Jenkins.");
		Collection<NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType>> notifierDevices = deviceRegistry
				.getNotifierDevices();
		for (NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType> notifierDevice : notifierDevices) {
			notifierDevice.preUpdate();
		}
		List<JobState> states = null;
		try {
			states = jenkinsHttpClient.getJobStates();
		} catch (IOException e) {
			LOGGER.warn("Cannot get job states.", e);
			for (NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType> notifierDevice : notifierDevices) {
				notifierDevice.updateFailed(e.getMessage());
			}
		}
		if (states != null) {
			LOGGER.debug(states.toString());
			LOGGER.debug("Publishing status to devices.");
			for (JobState state : states) {
				for (NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType> notifierDevice : notifierDevices) {
					notifierDevice.update(state);
				}
			}
		}
		for (NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType> notifierDevice : notifierDevices) {
			notifierDevice.postUpdate();
		}
	}
}
