package com.buschmais.tinkerforge4jenkins.core.notifier.io4;

import static com.buschmais.tinkerforge4jenkins.core.BuildState.ABORTED;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.FAILURE;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.NOT_BUILT;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNKNOWN;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNSTABLE;
import static com.buschmais.tinkerforge4jenkins.core.notifier.io4.IO4NotifierBricklet.Pin.BUILDING;
import static com.buschmais.tinkerforge4jenkins.core.notifier.io4.IO4NotifierBricklet.Pin.GREEN;
import static com.buschmais.tinkerforge4jenkins.core.notifier.io4.IO4NotifierBricklet.Pin.RED;
import static com.buschmais.tinkerforge4jenkins.core.notifier.io4.IO4NotifierBricklet.Pin.YELLOW;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.IO4ConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;
import com.tinkerforge.BrickletIO4;

/**
 * Implementation of a notifier device for IO4 bricklets.
 * 
 * @author dirk.mahler
 */
public class IO4NotifierBricklet extends
		AbstractNotifierDevice<BrickletIO4, IO4ConfigurationType> {

	public enum Pin {

		GREEN(0), YELLOW(1), RED(2), BUILDING(3);

		private int number;

		Pin(int number) {
			this.number = number;
		}

		public short getValue() {
			return (short) (1 << number);
		}

	}

	/**
	 * Constructor.
	 * 
	 * @param uid
	 *            The uid of the device.
	 * @param device
	 *            The device.
	 */
	public IO4NotifierBricklet(String uid, BrickletIO4 device) {
		super(uid, device);
	}

	@Override
	public void preUpdate() {
		// configure the bricklet: output high on all pins

		getDevice().setConfiguration((short) getAllPiNValues(), 'o', true);
	}

	@Override
	public void postUpdate() {
		IO4ConfigurationType configuration = getConfiguration();
		JobsType filter;
		if (configuration != null) {
			filter = getConfiguration().getJobs();
		} else {
			filter = null;
		}
		Pin pin = null;
		if (!filter(getJobsByBuildState(FAILURE), filter).isEmpty()) {
			pin = RED;
			// red
		} else if (!getJobsByBuildState(ABORTED, NOT_BUILT, UNKNOWN, UNSTABLE)
				.isEmpty()) {
			// yellow
			pin = YELLOW;
		} else {
			// green
			pin = GREEN;
		}
		int value = pin.getValue();
		for (JobState jobState : filter(getJobStates().values(), filter)) {
			if (jobState.isBuilding()) {
				value = value | BUILDING.getValue();
			}
		}
		getDevice().setValue((short) value);
	}

	@Override
	public void updateFailed(String message) {
		getDevice().setValue((short) (getAllPiNValues()));
	}

	/**
	 * Returns the value to set on the bricklet to activate all pins.
	 * 
	 * @return The value to activate all pins.
	 */
	private short getAllPiNValues() {
		short value = 0;
		for (Pin state : Pin.values()) {
			value = (short) (value + state.getValue());
		}
		return (short) value;
	}
}