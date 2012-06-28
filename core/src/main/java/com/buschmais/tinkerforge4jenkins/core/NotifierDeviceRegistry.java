package com.buschmais.tinkerforge4jenkins.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ConnectionConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType.Bricklets;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * A registry for the detected TinkerForge devices.
 * 
 * @author dirk.mahler
 */
public class NotifierDeviceRegistry {

	/**
	 * Enumeration listener for TinkerForge devices.
	 * 
	 * @author dirk.mahler
	 */
	private final class NotifierDeviceEnumerateListener implements
			EnumerateListener {

		/**
		 * The ip connection.
		 */
		private final IPConnection ipcon;

		/**
		 * Constructor.
		 * 
		 * @param ipConnection
		 *            The ip connection to the TinkerForge devices.
		 */
		private NotifierDeviceEnumerateListener(IPConnection ipConnection) {
			this.ipcon = ipConnection;
		}

		@Override
		public void enumerate(String uid, String name, short stackID,
				boolean isNew) {
			if (isNew) {
				LOGGER.info("Device '{}' with uid '{}' added.", name.trim(),
						uid);
				// Iterate over all factories and match the given name.
				for (NotifierDeviceFactory factory : NOTIFIERDEVICEFACTORY_SERVICELOADER) {
					if (factory.match(name.trim())) {
						LOGGER.info("Registering notifier '{}' (uid='{}').",
								name.trim(), uid);
						// Create the notifier device.
						NotifierDevice<? extends Device, ? extends BrickletConfigurationType> notifier = factory
								.create(uid);
						// Find the configuration for the device.
						if (configuration != null) {
							Bricklets brickletsConfiguration = configuration
									.getBricklets();
							if (brickletsConfiguration != null) {
								for (BrickletConfigurationType brickletConfiguration : brickletsConfiguration
										.getDualRelayOrLcd20X4()) {
									if (uid.equals(brickletConfiguration
											.getUid())) {
										// Apply the configuration.
										notifier.setConfiguration(brickletConfiguration);
									}
								}
							}
						}
						// Add the device.
						notifierDevices.put(uid, notifier);
						try {
							ipcon.addDevice(notifier.getDevice());
						} catch (TimeoutException e) {
							LOGGER.warn(String.format(
									"Cannot add device with uid '%s',", uid), e);
						}
					}
				}
			} else {
				// Remove the device.
				LOGGER.info("Device '{}' with uid '{}' removed.", name.trim(),
						uid);
				if (notifierDevices.remove(uid) != null) {
					LOGGER.info("Unregistering notifier '{}' (uid='{}').",
							name.trim(), uid);
				}
			}
		}
	}

	/**
	 * The default host for connecting to the TinkerForge devices.
	 */
	private static final String TINKERFORGE_DEFAULT_HOST = "localhost";

	/**
	 * The default port for connecting to the TinkerForge devices.
	 */
	private static final int TINKERFORGE_DEFAULT_PORT = 4223;

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NotifierDeviceRegistry.class);

	/**
	 * The service loader for the {@link NotifierDeviceFactory}s.
	 */
	private static final ServiceLoader<NotifierDeviceFactory> NOTIFIERDEVICEFACTORY_SERVICELOADER = ServiceLoader
			.load(NotifierDeviceFactory.class);

	/**
	 * The configuration for the TinkerForge devices.
	 */
	private TinkerForgeConfigurationType configuration;

	/**
	 * The currently active devices.
	 */
	private Map<String, NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> notifierDevices = new ConcurrentHashMap<String, NotifierDevice<? extends Device, ? extends BrickletConfigurationType>>();

	/**
	 * Constructor.
	 * 
	 * @param configuration
	 *            The configuration.
	 */
	public NotifierDeviceRegistry(TinkerForgeConfigurationType configuration) {
		this.configuration = configuration;
	}

	/**
	 * Starts the registry and returns the collection of connected devices.
	 * <p>
	 * The collection is updated if devices are added or removed (plug and
	 * play).
	 * </p>
	 * 
	 * @return The collection of devices.
	 * @throws IOException
	 *             If there is a communication problem with the TinkerForge
	 *             devices.
	 */
	public Collection<NotifierDevice<? extends Device, ? extends BrickletConfigurationType>> start()
			throws IOException {
		String host = TINKERFORGE_DEFAULT_HOST;
		int port = TINKERFORGE_DEFAULT_PORT;
		if (configuration != null) {
			ConnectionConfigurationType connectionConfiguration = configuration
					.getConnection();
			if (connectionConfiguration != null) {
				host = connectionConfiguration.getHost();
				port = connectionConfiguration.getPort();
			}
		}
		LOGGER.info("Connecting to '{}:{}'.", host, port);
		// Create the IP connection
		final IPConnection ipcon = new IPConnection(host, port);
		// Use the enumeration listener for device management.
		ipcon.enumerate(new NotifierDeviceEnumerateListener(ipcon));
		return notifierDevices.values();
	}

}
