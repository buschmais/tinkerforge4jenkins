package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.ABORTED;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.FAILURE;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.UNSTABLE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayPortType;
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
	 * @param uid
	 *            The device uid.
	 * @param bricklet
	 *            The {@link BrickletDualRelay} instance.
	 */
	public DualRelayNotifierBricklet(String uid, BrickletDualRelay bricklet) {
		super(uid, bricklet);
	}

	@Override
	public void preUpdate() {
	}

	@Override
	public void postUpdate() {
		Map<Integer, DualRelayPortType> portConfigurations = new HashMap<Integer, DualRelayPortType>();
		DualRelayConfigurationType configuration = getConfiguration();
		if (configuration != null) {
			for (DualRelayPortType port : configuration.getPort()) {
				portConfigurations.put(Integer.valueOf(port.getId()), port);
			}
		}
		Map<Integer, Boolean> relayStates = new HashMap<Integer, Boolean>();
		for (int i = 1; i <= 2; i++) {
			Set<JobState> jobs = getJobsByBuildState(ABORTED, FAILURE, UNSTABLE);
			DualRelayPortType portConfiguration = portConfigurations
					.get(Integer.valueOf(i));
			if (portConfiguration != null) {
				jobs = filter(jobs, portConfiguration.getJobs());
			}
			boolean relayState = !jobs.isEmpty();
			relayStates.put(Integer.valueOf(i), Boolean.valueOf(relayState));
		}
		getDevice().setState(
				relayStates.get(Integer.valueOf(1)).booleanValue(),
				relayStates.get(Integer.valueOf(2)).booleanValue());
	}

	@Override
	public void updateFailed(String message) {
	}

}
