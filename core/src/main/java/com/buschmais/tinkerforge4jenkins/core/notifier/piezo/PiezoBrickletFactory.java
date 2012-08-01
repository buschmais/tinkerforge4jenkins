package com.buschmais.tinkerforge4jenkins.core.notifier.piezo;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.PiezoBuzzerConfigurationType;
import com.tinkerforge.BrickletPiezoBuzzer;

/**
 * Implementation of a notifier device factory for piezo buzzers..
 * 
 * @author dirk.mahler
 */
public class PiezoBrickletFactory extends AbstractNotifierDeviceFactory {

	@Override
	public NotifierDevice<BrickletPiezoBuzzer, PiezoBuzzerConfigurationType> create(
			String uid) {
		return new PiezoNotifierBricklet(uid, new BrickletPiezoBuzzer(uid));
	}

	@Override
	protected String getIdentifierPattern() {
		return "Piezo\\ Buzzer\\ Bricklet\\ 1\\.[0-9]";
	}
}
