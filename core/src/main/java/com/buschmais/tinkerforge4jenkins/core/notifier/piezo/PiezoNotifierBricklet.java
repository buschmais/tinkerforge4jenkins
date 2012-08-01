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
public class PiezoNotifierBricklet
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
	public PiezoNotifierBricklet(String uid, BrickletPiezoBuzzer device) {
		super(uid, device);
	}

	@Override
	public void preUpdate() {
		this.previousJobStates = new TreeMap<String, JobState>(getJobStates());
	}

	@Override
	public void postUpdate() {
		SortedSet<BuildStateType> changedBuildStates = new TreeSet<BuildStateType>();
		for (JobState jobState : filter(getJobStates().values(),
				getConfiguration().getJobs())) {
			BuildStateType previousState = previousJobStates.get(
					jobState.getName()).getBuildState();
			if (!previousState.equals(jobState.getBuildState())) {
				changedBuildStates.add(jobState.getBuildState());
			}
			if (!changedBuildStates.isEmpty()) {
				BuildStateType buildState = changedBuildStates.iterator()
						.next();
				String morseCode;
				switch (buildState) {
				case FAILURE:
					morseCode = getConfiguration().getOnFailure();
					break;
				case UNSTABLE:
					morseCode = getConfiguration().getOnUnstable();
					break;
				case ABORTED:
					morseCode = getConfiguration().getOnAborted();
					break;
				case UNKNOWN:
					morseCode = getConfiguration().getOnUnknown();
					break;
				case NOT_BUILT:
					morseCode = getConfiguration().getOnNotBuilt();
					break;
				case SUCCESS:
					morseCode = getConfiguration().getOnSuccess();
					break;
				default:
					throw new IllegalStateException("Unknown build state: "
							+ buildState);
				}
				if (!morseCode.isEmpty()) {
					getDevice().morseCode(morseCode);
				}
			}
		}
	}

	@Override
	public void updateFailed(String message) {
	}

}