package com.buschmais.tinkerforge4jenkins.core.notifier.common;

import java.util.regex.Pattern;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifierFactory;

public abstract class AbstractDeviceNotifierFactory implements DeviceNotifierFactory {

	@Override
	public boolean match(String identifier) {
		return Pattern.matches(getIdentifierPattern(), identifier);
	}
	
	protected abstract String getIdentifierPattern();
	
}
