package com.buschmais.tinkerforge4jenkins.core.test;

import static com.buschmais.tinkerforge4jenkins.core.BuildState.ABORTED;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.FAILURE;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.NOT_BUILT;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.SUCCESS;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNKNOWN;
import static com.buschmais.tinkerforge4jenkins.core.BuildState.UNSTABLE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay.DualRelayNotifierBricklet;
import com.buschmais.tinkerforge4jenkins.core.notifier.io4.IO4NotifierBricklet;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.IO4ConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;
import com.buschmais.tinkerforge4jenkins.core.util.JobStateBuilder;
import com.tinkerforge.BrickletIO4;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Tests for the {@link DualRelayNotifierBricklet}.
 * 
 * @author dirk.mahler
 */
public class IO4BrickletNotifierTest extends AbstractBrickletNotifierTest {

	/**
	 * The name of the first job.
	 */
	private static final String JOBNAME_0 = "0";

	/**
	 * The mock.
	 */
	private BrickletIO4 mock;

	/**
	 * The notifier under test.
	 */
	private IO4NotifierBricklet notifier;

	/**
	 * The configuration.
	 */
	private IO4ConfigurationType configuration;

	/**
	 * Initialize the {@link IONotifierBricklet}.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Before
	public void createBricklet() throws TimeoutException {
		mock = Mockito.mock(BrickletIO4.class);
		notifier = new IO4NotifierBricklet(UID, mock);
		configuration = new IO4ConfigurationType();
		configuration.setUid(UID);
		notifier.setConfiguration(configuration);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void allBuildsSuccessful() throws TimeoutException {
		test(new boolean[] { true, false, false, false }, SUCCESS, SUCCESS);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildAborted() throws TimeoutException {
		test(new boolean[] { false, true, false, false }, SUCCESS, ABORTED);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildNotBuilt() throws TimeoutException {
		test(new boolean[] { false, true, false, false }, SUCCESS, NOT_BUILT);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildUnknown() throws TimeoutException {
		test(new boolean[] { false, true, false, false }, SUCCESS, UNKNOWN);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildUnstable() throws TimeoutException {
		test(new boolean[] { false, true, false, false }, SUCCESS, UNSTABLE);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildFailed() throws TimeoutException {
		test(new boolean[] { false, false, true, false }, SUCCESS, FAILURE);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void filter() throws TimeoutException {
		addFilter(JOBNAME_0);
		test(new boolean[] { true, false, false, false }, SUCCESS);
	}

	/**
	 * Add a job filter.
	 * 
	 * @param jobs
	 *            The names of the jobs.
	 */
	private void addFilter(String... jobs) {
		JobsType jobsType = configuration.getJobs();
		if (jobsType == null) {
			jobsType = new JobsType();
			configuration.setJobs(jobsType);
		}
		for (String job : jobs) {
			jobsType.getJob().add(job);
		}
	}

	/**
	 * Verifies the expected behavior of the {@link IO4NotifierBricklet}.
	 * 
	 * @param pins
	 *            The expected state of relay 1.
	 * @param buildStates
	 *            The {@link BuildState}s.
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	private void test(boolean[] pins, BuildState... buildStates)
			throws TimeoutException {
		notifier.preUpdate();
		int i = 0;
		for (BuildState buildState : buildStates) {
			notifier.update(JobStateBuilder.create(Integer.toString(i),
					buildState, false));
			i++;
		}
		notifier.postUpdate();
		int expectedValue = 0;
		for (int p = 0; p < pins.length; p++) {
			if (pins[p]) {
				expectedValue = expectedValue + (1 << p);
			}
		}
		verify(mock).setConfiguration((short) 15, 'o', true);
		verify(mock).setValue((short) expectedValue);
		verifyNoMoreInteractions(mock);
	}
}
