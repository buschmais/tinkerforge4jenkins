package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.tinkerforge.Device;

/**
 * Defines factory interface to create {@link NotifierDevice}s.
 * 
 * @author dirk.mahler
 */
public interface NotifierDeviceFactory {

	/**
	 * Indicates if this factory creates {@link NotifierDevice} instances
	 * supporting TinkerForge devices reporting the given identifier.
	 * 
	 * @param identifier
	 *            The identifier.
	 * @return <code>true</code> if the factory creates {@link NotifierDevice}
	 *         instances for the given identifier.
	 */
	boolean match(String identifier);

	/**
	 * Create an instance of a {@link NotifierDevice}.
	 * 
	 * @param uid
	 *            The uid of the TinkerForge device.
	 * @return The {@link NotifierDevice}.
	 */
	NotifierDevice<? extends Device, ? extends BrickletConfigurationType> create(
			String uid);
}
