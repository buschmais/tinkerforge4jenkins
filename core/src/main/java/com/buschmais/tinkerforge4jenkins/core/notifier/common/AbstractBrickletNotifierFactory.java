package com.buschmais.tinkerforge4jenkins.core.notifier.common;

import com.buschmais.tinkerforge4jenkins.core.BrickletNotifierFactory;

public abstract class AbstractBrickletNotifierFactory implements BrickletNotifierFactory {

	@Override
	public boolean match(String identifier) {
		return false;
	}
	
}
