package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractDeviceNotifierFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD20X4ConfigurationType;
import com.tinkerforge.BrickletLCD20x4;

public class LCD20x4BrickletNotifierFactory extends
		AbstractDeviceNotifierFactory {

	@Override
	public DeviceNotifier<BrickletLCD20x4, LCD20X4ConfigurationType> create(
			String uid) {
		return new LCD20x4BrickletNotifier(new BrickletLCD20x4(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "LCD\\ 20x4\\ Bricklet\\ 1\\.[0-9]";
	}

}
