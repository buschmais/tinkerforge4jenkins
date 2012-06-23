package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayConfigurationType;
import com.tinkerforge.BrickletDualRelay;

/**
 * Implementation of a notifier device factory for dual relays.
 * 
 * @author dirk.mahler
 */
public class DualRelayNotifierBrickletFactory extends
		AbstractNotifierDeviceFactory {

	@Override
	public NotifierDevice<BrickletDualRelay, DualRelayConfigurationType> create(
			String uid) {
		return new DualRelayNotifierBricklet(uid, new BrickletDualRelay(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "Dual\\ Relay\\ Bricklet\\ 1\\.[0-9]";
	}

}
