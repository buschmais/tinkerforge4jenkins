package com.buschmais.tinkerforge4jenkins.core.notifier.piezo;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.PiezoBuzzerConfigurationType;
import com.tinkerforge.BrickletPiezoBuzzer;

/**
 * Implementation of a notifier device for piezo bricklets.
 * 
 * @author dirk.mahler
 */
public class PiezoBuzzerNotifierBricklet
		extends
		AbstractNotifierDevice<BrickletPiezoBuzzer, PiezoBuzzerConfigurationType> {

	/**
	 * A map holding the {@link JobState}s identified by their names.
	 */
	private SortedMap<String, JobState> previousJobStates = new TreeMap<String, JobState>();

	/**
	 * Constructor.
	 * 
	 * @param uid
	 *            The uid of the device.
	 * @param device
	 *            The device.
	 */
	public PiezoBuzzerNotifierBricklet(String uid, BrickletPiezoBuzzer device) {
		super(uid, device);
	}

	@Override
	public void preUpdate() {
		this.previousJobStates = new TreeMap<String, JobState>(getJobStates());
	}

	@Override
	public void postUpdate() {
		// Determine all build states which have changed and add them to a set
		// ordered by their priority (descending).
		SortedSet<BuildStateType> changedBuildStates = new TreeSet<BuildStateType>();
		PiezoBuzzerConfigurationType configuration = getConfiguration();
		for (JobState jobState : filter(getJobStates().values(),
				configuration.getJobs())) {
			JobState previousJobState = previousJobStates.get(jobState
					.getName());
			if (previousJobState == null
					|| !previousJobState.getBuildState().equals(
							jobState.getBuildState())) {
				changedBuildStates.add(jobState.getBuildState());
			}
		}
		// If there were changes...
		if (!changedBuildStates.isEmpty()) {
			// ...select the state with the highest priority and send the
			// configured morse code to the bricklet.
			BuildStateType buildState = changedBuildStates.iterator().next();
			String morseCode;
			switch (buildState) {
			case FAILURE:
				morseCode = configuration.getOnFailure();
				break;
			case UNSTABLE:
				morseCode = configuration.getOnUnstable();
				break;
			case ABORTED:
				morseCode = configuration.getOnAborted();
				break;
			case UNKNOWN:
				morseCode = configuration.getOnUnknown();
				break;
			case NOT_BUILT:
				morseCode = configuration.getOnNotBuilt();
				break;
			case SUCCESS:
				morseCode = configuration.getOnSuccess();
				break;
			default:
				throw new IllegalStateException("Unknown build state: "
						+ buildState);
			}
			if (morseCode != null && !morseCode.isEmpty()) {
				getDevice().morseCode(morseCode);
			}
		}
	}

	@Override
	public void updateFailed(String message) {
	}

}