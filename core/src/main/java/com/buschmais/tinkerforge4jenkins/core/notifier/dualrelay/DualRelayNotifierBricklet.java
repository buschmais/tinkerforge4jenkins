package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayConfigurationType;
import com.tinkerforge.BrickletDualRelay;

/**
 * Implementation of a notifier device for dual relay bricklets.
 * 
 * @author dirk.mahler
 */
public class DualRelayNotifierBricklet extends
		AbstractNotifierDevice<BrickletDualRelay, DualRelayConfigurationType> {

	/**
	 * Constructor.
	 * 
	 * @param brickletDualRelay
	 *            The {@link BrickletDualRelay} instance.
	 */
	public DualRelayNotifierBricklet(BrickletDualRelay brickletDualRelay) {
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
