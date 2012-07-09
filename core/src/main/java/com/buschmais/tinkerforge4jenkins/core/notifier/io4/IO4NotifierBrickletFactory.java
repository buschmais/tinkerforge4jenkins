package com.buschmais.tinkerforge4jenkins.core.notifier.io4;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.IO4ConfigurationType;
import com.tinkerforge.BrickletIO4;

/**
 * Implementation of a notifier device factory for IO4.
 * 
 * @author dirk.mahler
 */
public class IO4NotifierBrickletFactory extends AbstractNotifierDeviceFactory {

	@Override
	public NotifierDevice<BrickletIO4, IO4ConfigurationType> create(String uid) {
		return new IO4NotifierBricklet(uid, new BrickletIO4(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "IO-4\\ Bricklet\\ 1\\.[0-9]";
	}

}
