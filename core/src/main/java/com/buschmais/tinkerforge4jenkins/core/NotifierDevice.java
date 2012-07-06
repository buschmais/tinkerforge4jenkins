package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.AbstractBrickletConfigurationType;
import com.tinkerforge.Device;

/**
 * Defines the interface for a notifier device.
 * 
 * @author dirk.mahler
 * 
 * @param <T>
 *            The type of the underlying TinkerForge device.
 * @param <C>
 *            The configuration type.
 */
public interface NotifierDevice<T extends Device, C extends AbstractBrickletConfigurationType> {

	/**
	 * Return the underlying device.
	 * 
	 * @return The device.
	 */
	T getDevice();

	/**
	 * Set the configuration of the device.
	 * 
	 * @param configuration
	 *            The configuration.
	 */
	void setConfiguration(AbstractBrickletConfigurationType configuration);

	/**
	 * Lifecycle callback which is called before any status updates are promoted
	 * using {@link #update(JobState)}.
	 */
	void preUpdate();

	/**
	 * Updates the state of a job.
	 * 
	 * @param state
	 *            The state.
	 */
	void update(JobState state);

	/**
	 * Lifecycle callback which is called after all status updates are promoted
	 * using {@link #update(JobState)}.
	 */
	void postUpdate();

	/**
	 * Indicates that there was a problem updating the job states from Jenkins.
	 * 
	 * @param message
	 *            A message indicating the reason.
	 */
	void updateFailed(String message);

}
