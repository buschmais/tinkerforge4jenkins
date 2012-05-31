package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractDeviceNotifierFactory;
import com.tinkerforge.BrickletDualRelay;

public class DualRelayBrickletNotifierFactory extends AbstractDeviceNotifierFactory {

	@Override
	public DeviceNotifier create(String uid) {
		return new DualRelayBrickletNotifier(new BrickletDualRelay(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "Dual\\ Relay\\ Bricklet\\ 1\\.[0-9]";
	}

}
