package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD20X4ConfigurationType;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLCD20x4.ButtonPressedListener;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Implementation of a notifier device for LCD 20x4 bricklets.
 * 
 * @author dirk.mahler
 */
public class LCD20x4NotifierBricklet extends
		AbstractNotifierDevice<BrickletLCD20x4, LCD20X4ConfigurationType>
		implements ButtonPressedListener {

	/**
	 * The maximum number of rows that can be displayed.
	 */
	private static final int MAXIMUM_ROWS = 4;

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(LCD20x4NotifierBricklet.class);

	/**
	 * Constructor.
	 * 
	 * @param uid
	 *            The device uid.
	 * @param bricklet
	 *            The {@link BrickletLCD20x4} instance.
	 */
	public LCD20x4NotifierBricklet(String uid, BrickletLCD20x4 bricklet) {
		super(uid, bricklet);
		getDevice().addListener(this);
	}

	/**
	 * Switches the back light on or off.
	 * 
	 * @param state
	 *            The new state, <code>true</code> indicates that the back light
	 *            should be switched on.
	 */
	private void setBackLight(boolean state) {
		LOGGER.debug("switching backlight: " + state);
		try {
			if (!getDevice().isBacklightOn() == state) {
				if (state) {
					getDevice().backlightOn();
				} else {
					getDevice().backlightOff();
				}
			}
		} catch (TimeoutException e) {
			LOGGER.warn("Cannot switch backlight.", e);
		}
	}

	@Override
	public void preUpdate() {
		LOGGER.debug("clearing display before updating.");
		getDevice().clearDisplay();
		getDevice().writeLine((short) 0, (short) 0, "Updating status...");
	}

	@Override
	public void postUpdate() {
		LOGGER.debug("Clearing display before writing to device.");
		getDevice().clearDisplay();
		LCD20X4ConfigurationType configuration = getConfiguration();
		JobsType filter = null;
		if (configuration != null) {
			filter = configuration.getJobs();
		}
		Set<JobState> allJobs = filter(getJobStates().values(), filter);
		Set<JobState> successfulJobs = filter(
				getJobsByBuildState(BuildState.SUCCESS), filter);
		if (successfulJobs.size() == allJobs.size()) {
			LOGGER.debug("No information to be written to device.");
			setBackLight(false);
		} else {
			writeJobStates(allJobs);
			setBackLight(true);
		}
	}

	/**
	 * Writes the (non-successful) state of the given jobs to the LCD bricklet.
	 * 
	 * @param jobs
	 *            The {@link JobState}s.
	 */
	private void writeJobStates(Set<JobState> jobs) {
		LOGGER.debug("Writing job states to device.");
		// Sort the jobs by the severity of their build state.
		SortedSet<JobState> sortedJobs = new TreeSet<JobState>(
				new Comparator<JobState>() {

					@Override
					public int compare(JobState o1, JobState o2) {
						return o1.getBuildState().compareTo(o2.getBuildState());
					}
				});
		sortedJobs.addAll(jobs);
		Iterator<JobState> iterator = sortedJobs.iterator();
		int i = 0;
		while (iterator.hasNext() && i < MAXIMUM_ROWS) {
			JobState summary = iterator.next();
			if (!BuildState.SUCCESS.equals(summary.getBuildState())) {
				char symbol;
				switch (summary.getBuildState()) {
				case ABORTED:
					symbol = 'A';
					break;
				case NOT_BUILT:
					symbol = 'N';
					break;
				case UNSTABLE:
					symbol = 'U';
					break;
				case FAILURE:
					symbol = 'F';
					break;
				case UNKNOWN:
				default:
					symbol = '?';
				}
				String statusLine = symbol + " " + summary.getName();
				getDevice().writeLine((short) i, (short) 0, statusLine);
				i++;
			}
		}
	}

	@Override
	public void updateFailed(String message) {
		getDevice().clearDisplay();
		setBackLight(true);
		getDevice().writeLine((short) 0, (short) 0, "No status available");
		getDevice().writeLine((short) 1, (short) 0, message);
	}

	@Override
	public void buttonPressed(short button) {
	}
}
