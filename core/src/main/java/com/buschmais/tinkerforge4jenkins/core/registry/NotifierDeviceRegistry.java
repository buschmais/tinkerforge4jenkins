package com.buschmais.tinkerforge4jenkins.core.registry;

import java.io.IOException;
import java.util.Collection;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.NotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.AbstractBrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ConnectionConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;

/**
 * A registry for the detected TinkerForge devices.
 * 
 * @author dirk.mahler
 */
public class NotifierDeviceRegistry {

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
	 * The TinkerForge IP connection.
	 */
	private IPConnection ipConnection;

	/**
	 * The configuration for the TinkerForge devices.
	 */
	private TinkerForgeConfigurationType configuration;

	/**
	 * The {@link NotifierDeviceEnumerateListener}.
	 */
	private NotifierDeviceEnumerateListener listener;

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
	 * Starts the registry.
	 * 
	 * @throws IOException
	 *             If there is a communication problem with the TinkerForge
	 *             devices.
	 */
	public void start() throws IOException {
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
		ipConnection = new IPConnection(host, port);
		// initialize the enumeration listener for device management.
		listener = new NotifierDeviceEnumerateListener(ipConnection,
				ServiceLoader.load(NotifierDeviceFactory.class), configuration);
		ipConnection.enumerate(listener);
	}

	/**
	 * Returns the collection of connected devices.
	 * <p>
	 * The collection provides a "live" view on connected devices, i.e. it is
	 * updated every time if devices are added or removed (plug and play).
	 * </p>
	 * 
	 * @return The collection of devices.
	 */
	public Collection<NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType>> getNotifierDevices() {
		if (listener == null) {
			throw new IllegalStateException(
					"The device registry has not been started.");
		}
		return listener.getNotifierDevices().values();
	}

	/**
	 * Stops the registry.
	 */
	public void stop() {
		if (ipConnection != null) {
			LOGGER.info("Destroying ip connection.");
			ipConnection.destroy();
		}
		listener = null;
	}

}
