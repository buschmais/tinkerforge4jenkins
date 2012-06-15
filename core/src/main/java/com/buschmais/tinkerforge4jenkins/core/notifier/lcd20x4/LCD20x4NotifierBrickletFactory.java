package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD20X4ConfigurationType;
import com.tinkerforge.BrickletLCD20x4;

/**
 * Implementation of a notifier device factory for LCD 20x4 bricklets.
 * 
 * @author dirk.mahler
 */
public class LCD20x4NotifierBrickletFactory extends
		AbstractNotifierDeviceFactory {

	@Override
	public NotifierDevice<BrickletLCD20x4, LCD20X4ConfigurationType> create(
			String uid) {
		return new LCD20x4NotifierBricklet(new BrickletLCD20x4(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "LCD\\ 20x4\\ Bricklet\\ 1\\.[0-9]";
	}

}
