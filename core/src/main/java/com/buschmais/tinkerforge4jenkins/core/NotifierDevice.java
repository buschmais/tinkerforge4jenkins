package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
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
public interface NotifierDevice<T extends Device, C extends BrickletConfigurationType> {

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
	void setConfiguration(BrickletConfigurationType configuration);

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
	 */
	void updateFailed(String message);

}
