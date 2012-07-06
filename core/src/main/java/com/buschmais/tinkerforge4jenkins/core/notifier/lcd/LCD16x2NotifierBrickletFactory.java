package com.buschmais.tinkerforge4jenkins.core.notifier.lcd;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD16X2ConfigurationType;
import com.tinkerforge.BrickletLCD16x2;

/**
 * Implementation of a notifier device factory for LCD 16x2 bricklets.
 * 
 * @author dirk.mahler
 */
public class LCD16x2NotifierBrickletFactory extends
		AbstractNotifierDeviceFactory {

	@Override
	public NotifierDevice<BrickletLCD16x2, LCD16X2ConfigurationType> create(
			String uid) {
		return new LCD16x2NotifierBricklet(uid, new BrickletLCD16x2(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "LCD\\ 16x2\\ Bricklet\\ 1\\.[0-9]";
	}

}
