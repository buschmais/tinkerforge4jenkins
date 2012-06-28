package com.buschmais.tinkerforge4jenkins.core.test;

import static com.buschmais.tinkerforge4jenkins.core.BuildState.ABORTED;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.FAILURE;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.NOT_BUILT;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.SUCCESS;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNKNOWN;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNSTABLE;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4.LCD20x4NotifierBricklet;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD20X4ConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.util.JobStateBuilder;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Tests for the {@link LCD20x4NotifierBricklet}.
 * 
 * @author dirk.mahler
 */
public class LCD20x4BrickletNotifierTest extends AbstractBrickletNotifierTest {

	/**
	 * The job name prefix.
	 */
	private static final String JOBNAME_PREFIX = "Job_";

	/**
	 * The mock.
	 */
	private BrickletLCD20x4 mock;

	/**
	 * The notifier under test.
	 */
	private LCD20x4NotifierBricklet notifier;

	/**
	 * The configuration.
	 */
	private LCD20X4ConfigurationType configuration;

	/**
	 * Initialize the {@link LCD20X4ConfigurationType}.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Before
	public void createBricklet() throws TimeoutException {
		mock = Mockito.mock(BrickletLCD20x4.class);
		notifier = new LCD20x4NotifierBricklet(UID, mock);
		configuration = new LCD20X4ConfigurationType();
		configuration.setUid(UID);
		notifier.setConfiguration(configuration);
	}

	/**
	 * The generic lifecyle of updating job states.
	 */
	@Test
	public void updateLifecycle() {
		notifier.preUpdate();
		verify(mock).writeLine((short) 0, (short) 0, "Updating status...");
		verify(mock).clearDisplay();
		notifier.update(JobStateBuilder.create(JOBNAME_PREFIX + "0", FAILURE));
		verify(mock).clearDisplay();
		notifier.postUpdate();
		verify(mock, times(2)).clearDisplay();
	}

	/**
	 * All builds are successful.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void allBuildsSuccessful() throws TimeoutException {
		update(SUCCESS, SUCCESS);
		verify(mock, never()).backlightOn();
		verify(mock, never()).backlightOff();
	}

	/**
	 * One build is aborted.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildAborted() throws TimeoutException {
		update(SUCCESS, ABORTED);
		verify(mock).backlightOn();
		verify(mock, never()).backlightOff();
		verify(mock).writeLine((short) 0, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "1", ABORTED));
	}

	/**
	 * Maximum rows and order of severity are considered.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void maxRowsAndSortingBySeverity() throws TimeoutException {
		update(SUCCESS, UNKNOWN, NOT_BUILT, ABORTED, UNSTABLE, FAILURE);
		verify(mock, atMost(1)).backlightOn();
		verify(mock, never()).backlightOff();
		verify(mock).writeLine((short) 0, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "5", FAILURE));
		verify(mock).writeLine((short) 1, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "4", UNSTABLE));
		verify(mock).writeLine((short) 2, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "3", ABORTED));
		verify(mock).writeLine((short) 3, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "1", UNKNOWN));
	}

	/**
	 * A build becomes unstable.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void buildBecomesUnstable() throws TimeoutException {
		update(SUCCESS);
		verify(mock, never()).backlightOn();
		update(FAILURE);
		verify(mock).backlightOn();
		verify(mock, never()).backlightOff();
		verify(mock).writeLine((short) 0, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "0", FAILURE));
	}

	/**
	 * A build becomes stable.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void buildBecomesStable() throws TimeoutException {
		update(UNSTABLE);
		verify(mock).writeLine((short) 0, (short) 0,
				notifier.createStatusLine(JOBNAME_PREFIX + "0", UNSTABLE));
		verify(mock).backlightOn();
		stub(mock.isBacklightOn()).toReturn(Boolean.TRUE);
		update(SUCCESS);
		verify(mock, times(4)).clearDisplay();
		verify(mock).backlightOff();
	}

	/**
	 * Updates the state of the {@link LCD20x4NotifierBricklet}.
	 * 
	 * @param buildStates
	 *            The {@link BuildState}s.
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	private void update(BuildState... buildStates) throws TimeoutException {
		notifier.preUpdate();
		int i = 0;
		for (BuildState buildState : buildStates) {
			notifier.update(JobStateBuilder.create(
					JOBNAME_PREFIX + Integer.toString(i), buildState));
			i++;
		}
		notifier.postUpdate();
	}

}
