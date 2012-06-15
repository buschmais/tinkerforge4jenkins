package com.buschmais.tinkerforge4jenkins.core.notifier.common;

import java.util.regex.Pattern;

import com.buschmais.tinkerforge4jenkins.core.NotifierDeviceFactory;

/**
 * Abstract base implementation for {@link NotifierDeviceFactory}s.
 * 
 * @author dirk.mahler
 */
public abstract class AbstractNotifierDeviceFactory implements
		NotifierDeviceFactory {

	@Override
	public boolean match(String identifier) {
		return Pattern.matches(getIdentifierPattern(), identifier);
	}

	/**
	 * Returns a regular expression for matching the device identifiers of this
	 * factory.
	 * 
	 * @return The regular expression.
	 */
	protected abstract String getIdentifierPattern();

}
