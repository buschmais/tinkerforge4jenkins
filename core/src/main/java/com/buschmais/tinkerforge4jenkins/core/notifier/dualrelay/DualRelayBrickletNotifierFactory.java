package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.BrickletNotifier;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractBrickletNotifierFactory;
import com.tinkerforge.BrickletDualRelay;

public class DualRelayBrickletNotifierFactory extends AbstractBrickletNotifierFactory {

	@Override
	public BrickletNotifier create(String uid) {
		return new DualRelayBrickletNotifier(new BrickletDualRelay(uid));
	}

}
