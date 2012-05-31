package com.buschmais.tinkerforge4jenkins.core;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.IPConnection.TimeoutException;

public class DeviceRegistry {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DeviceRegistry.class);

	private static final ServiceLoader<DeviceNotifierFactory> serviceLoader = ServiceLoader
			.load(DeviceNotifierFactory.class);

	private String host;
	private int port;

	private List<DeviceNotifier> notifiers = new CopyOnWriteArrayList<DeviceNotifier>();

	public DeviceRegistry(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public List<DeviceNotifier> start() throws IOException {
		final IPConnection ipcon = new IPConnection(host, port);
		ipcon.enumerate(new EnumerateListener() {
			@Override
			public void enumerate(String uid, String name, short stackID,
					boolean isNew) {
				LOGGER.debug("Found device '{}' with uid '{}'", name.trim(),
						uid);
				for (DeviceNotifierFactory factory : serviceLoader) {
					if (factory.match(name.trim())) {
						LOGGER.info(
								"Registering notifier '{}' (uid='{}').",
								name.trim(), uid);
						DeviceNotifier notifier = factory.create(uid);
						notifiers.add(notifier);
						try {
							ipcon.addDevice(notifier.getDevice());
						} catch (TimeoutException e) {
							LOGGER.warn(String.format(
									"Cannot add device with uid '%s',", uid), e);
						}
					}
				}
			}
		});
		return notifiers;
	}

}
