package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import com.buschmais.tinkerforge4jenkins.core.BrickletNotifier;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractBrickletNotifierFactory;
import com.tinkerforge.BrickletLCD20x4;

public class LCD20x4BrickletNotifierFactory extends AbstractBrickletNotifierFactory {

	@Override
	public BrickletNotifier create(String uid) {
		return new LCD20x4BrickletNotifier(new BrickletLCD20x4(uid));
	}

}
