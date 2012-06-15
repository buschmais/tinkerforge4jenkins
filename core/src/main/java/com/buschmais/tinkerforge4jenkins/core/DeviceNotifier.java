package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.tinkerforge.Device;

public interface DeviceNotifier<T extends Device, C extends BrickletConfigurationType> {

	void preUpdate();

	void update(JobState state);

	void postUpdate();

	void updateFailed(String message);

	T getDevice();

	void setConfiguration(BrickletConfigurationType configuration);

	BrickletConfigurationType getConfiguration();

}
