package com.buschmais.tinkerforge4jenkins.core;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ConfigurationType;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.EnumerateListener;
import com.tinkerforge.IPConnection.TimeoutException;

public class DeviceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistry.class);

    private static final ServiceLoader<DeviceNotifierFactory> serviceLoader = ServiceLoader.load(DeviceNotifierFactory.class);

    private String host;
    private int port;
    private ConfigurationType.Bricklets brickletsConfiguration;

    private List<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifiers =
            new CopyOnWriteArrayList<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>>();

    public DeviceRegistry(String host, int port, ConfigurationType.Bricklets brickletsConfiguration) {
        this.host = host;
        this.port = port;
        this.brickletsConfiguration = brickletsConfiguration;
    }

    public List<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> start() throws IOException {
        final IPConnection ipcon = new IPConnection(host, port);
        ipcon.enumerate(new EnumerateListener() {
            @Override
            public void enumerate(String uid, String name, short stackID, boolean isNew) {
                LOGGER.debug("Found device '{}' with uid '{}'", name.trim(), uid);
                for (DeviceNotifierFactory factory : serviceLoader) {
                    if (factory.match(name.trim())) {
                        LOGGER.info("Registering notifier '{}' (uid='{}').", name.trim(), uid);
                        DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> notifier = factory.create(uid);
                        if (brickletsConfiguration != null) {
                            for (BrickletConfigurationType brickletConfiguration : brickletsConfiguration.getDualRelayOrLcd20X4()) {
                                if (uid.equals(brickletConfiguration.getUid())) {
                                }
                            }
                        }
                        notifiers.add(notifier);
                        try {
                            ipcon.addDevice(notifier.getDevice());
                        } catch (TimeoutException e) {
                            LOGGER.warn(String.format("Cannot add device with uid '%s',", uid), e);
                        }
                    }
                }
            }
        });
        return notifiers;
    }

}
