package com.buschmais.tinkerforge4jenkins.core;

import com.tinkerforge.Device;

public interface DeviceNotifier {

	void preUpdate();
	
	void update(JobState state);
	
	void postUpdate();

	void updateFailed(String message);
	
	Device getDevice();
		
}
