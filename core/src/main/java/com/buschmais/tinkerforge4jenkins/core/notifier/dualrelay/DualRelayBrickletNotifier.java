package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractDeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayConfigurationType;
import com.tinkerforge.BrickletDualRelay;

public class DualRelayBrickletNotifier extends
		AbstractDeviceNotifier<BrickletDualRelay, DualRelayConfigurationType> {

	public DualRelayBrickletNotifier(BrickletDualRelay brickletDualRelay) {
		super(brickletDualRelay);
	}

	@Override
	protected Class<DualRelayConfigurationType> getConfigurationType() {
		return DualRelayConfigurationType.class;
	}

	@Override
	public void preUpdate() {
	}

	@Override
	public void postUpdate() {
		if (getJobsByBuildState(BuildState.ABORTED).isEmpty()
				&& getJobsByBuildState(BuildState.FAILURE).isEmpty()
				&& getJobsByBuildState(BuildState.UNSTABLE).isEmpty()) {
			getDevice().setState(false, false);
		} else {
			getDevice().setState(true, true);
		}
	}

	@Override
	public void updateFailed(String message) {
	}

}
