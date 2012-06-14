package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.tinkerforge.Device;

public interface DeviceNotifierFactory {

	boolean match(String identifier);

	DeviceNotifier<? extends Device, ? extends BrickletConfigurationType> create(
			String uid);
}
