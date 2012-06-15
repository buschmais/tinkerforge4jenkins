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

public class DeviceRegistry {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DeviceRegistry.class);

	private static final ServiceLoader<DeviceNotifierFactory> serviceLoader = ServiceLoader
			.load(DeviceNotifierFactory.class);

	private TinkerForgeConfigurationType configuration;

	private Map<String, DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifiers = new ConcurrentHashMap<String, DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>>();

	public DeviceRegistry(TinkerForgeConfigurationType configuration) {
		this.configuration = configuration;
	}

	public Collection<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> start()
			throws IOException {
		ConnectionConfigurationType connectionConfiguration = configuration
				.getConnection();
		String host = "localhost";
		int port = 4223;
		if (connectionConfiguration != null) {
			host = connectionConfiguration.getHost();
			port = connectionConfiguration.getPort();
		}
		LOGGER.info("Connecting to '{}:{}'.", host, port);
		final IPConnection ipcon = new IPConnection(host, port);
		ipcon.enumerate(new EnumerateListener() {

			@Override
			public void enumerate(String uid, String name, short stackID,
					boolean isNew) {
				if (isNew) {
					LOGGER.info("Device '{}' with uid '{}' added.",
							name.trim(), uid);
					for (DeviceNotifierFactory factory : serviceLoader) {
						if (factory.match(name.trim())) {
							LOGGER.info(
									"Registering notifier '{}' (uid='{}').",
									name.trim(), uid);
							DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier = factory
									.create(uid);
							Bricklets brickletsConfiguration = configuration
									.getBricklets();
							if (brickletsConfiguration != null) {
								for (BrickletConfigurationType brickletConfiguration : brickletsConfiguration
										.getDualRelayOrLcd20X4()) {
									if (uid.equals(brickletConfiguration
											.getUid())) {
										notifier.setConfiguration(brickletConfiguration);
									}
								}
							}
							notifiers.put(uid, notifier);
							try {
								ipcon.addDevice(notifier.getDevice());
							} catch (TimeoutException e) {
								LOGGER.warn(
										String.format(
												"Cannot add device with uid '%s',",
												uid), e);
							}
						}
					}
				} else {
					LOGGER.info("Device '{}' with uid '{}' removed.",
							name.trim(), uid);
					if (notifiers.remove(uid) != null) {
						LOGGER.info("Unregistering notifier '{}' (uid='{}').",
								name.trim(), uid);
					}
				}
			}

		});
		return notifiers.values();
	}

}
