package com.buschmais.tinkerforge4jenkins.core.registry;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.NotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType.Bricklets;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Enumeration listener for TinkerForge devices.
 * 
 * @author dirk.mahler
 */
final class NotifierDeviceEnumerateListener implements EnumerateListener {

	/**
	 * The service loader for the {@link NotifierDeviceFactory}s.
	 */
	private static final ServiceLoader<NotifierDeviceFactory> NOTIFIERDEVICEFACTORY_SERVICELOADER = ServiceLoader
			.load(NotifierDeviceFactory.class);

	/**
	 * The ip connection.
	 */
	private final IPConnection ipcon;

	/**
	 * The configuration of the notifier devices.
	 */
	private TinkerForgeConfigurationType configuration;

	/**
	 * The currently active devices.
	 */
	private Map<String, NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices = new ConcurrentHashMap<String, NotifierDevice<? extends Device, ? extends BrickletConfigurationType>>();

	/**
	 * Constructor.
	 * 
	 * @param ipConnection
	 *            The ip connection to the TinkerForge devices.
	 * @param configuration
	 *            The configuration of the notifier devices.
	 */
	NotifierDeviceEnumerateListener(IPConnection ipConnection,
			TinkerForgeConfigurationType configuration) {
		this.ipcon = ipConnection;
		this.configuration = configuration;
	}

	@Override
	public void enumerate(String uid, String name, short stackID, boolean isNew) {
		if (isNew) {
			NotifierDeviceRegistry.LOGGER.info(
					"Device '{}' with uid '{}' added.", name.trim(), uid);
			// Iterate over all factories and match the given name.
			for (NotifierDeviceFactory factory : NOTIFIERDEVICEFACTORY_SERVICELOADER) {
				if (factory.match(name.trim())) {
					NotifierDeviceRegistry.LOGGER.info(
							"Registering notifier '{}' (uid='{}').",
							name.trim(), uid);
					// Create the notifier device.
					NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifier = factory
							.create(uid);
					// Find the configuration for the device.
					if (this.configuration != null) {
						Bricklets brickletsConfiguration = this.configuration
								.getBricklets();
						if (brickletsConfiguration != null) {
							for (BrickletConfigurationType brickletConfiguration : brickletsConfiguration
									.getDualRelayOrLcd20X4()) {
								if (uid.equals(brickletConfiguration.getUid())) {
									// Apply the configuration.
									notifier.setConfiguration(brickletConfiguration);
								}
							}
						}
					}
					// Add the device.
					this.notifierDevices.put(uid, notifier);
					try {
						ipcon.addDevice(notifier.getDevice());
					} catch (TimeoutException e) {
						NotifierDeviceRegistry.LOGGER.warn(String.format(
								"Cannot add device with uid '%s',", uid), e);
					}
				}
			}
		} else {
			// Remove the device.
			NotifierDeviceRegistry.LOGGER.info(
					"Device '{}' with uid '{}' removed.", name.trim(), uid);
			if (this.notifierDevices.remove(uid) != null) {
				NotifierDeviceRegistry.LOGGER.info(
						"Unregistering notifier '{}' (uid='{}').", name.trim(),
						uid);
			}
		}
	}

	/**
	 * Returns a map containing all {@link NotifierDevice}s devices identified
	 * by their uid.
	 * 
	 * @return The map of {@link NotifierDevice}s.
	 */
	public Map<String, NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> getNotifierDevices() {
		return notifierDevices;
	}

}