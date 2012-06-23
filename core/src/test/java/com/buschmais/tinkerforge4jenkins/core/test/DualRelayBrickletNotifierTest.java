package com.buschmais.tinkerforge4jenkins.core.test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay.DualRelayNotifierBricklet;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.DualRelayPortType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;
import com.buschmais.tinkerforge4jenkins.core.test.util.JobStateBuilder;
import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Tests for the {@link DualRelayNotifierBricklet}.
 * 
 * @author dirk.mahler
 */
public class DualRelayBrickletNotifierTest extends AbstractBrickletNotifierTest {

	/**
	 * The name of the first job.
	 */
	private static final String JOBNAME_0 = "0";

	/**
	 * The mock.
	 */
	private BrickletDualRelay mock;

	/**
	 * The notifier under test.
	 */
	private DualRelayNotifierBricklet notifier;

	/**
	 * The configuration.
	 */
	private DualRelayConfigurationType configuration;

	/**
	 * Initialize the {@link DualRelayNotifierBricklet}.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Before
	public void createBricklet() throws TimeoutException {
		mock = Mockito.mock(BrickletDualRelay.class);
		notifier = new DualRelayNotifierBricklet(UID, mock);
		configuration = new DualRelayConfigurationType();
		configuration.setUid(UID);
		notifier.setConfiguration(configuration);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void allBuildsSuccessful() throws TimeoutException {
		test(false, false, BuildState.SUCCESS, BuildState.SUCCESS);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildNotBuilt() throws TimeoutException {
		test(false, false, BuildState.SUCCESS, BuildState.NOT_BUILT);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildUnknown() throws TimeoutException {
		test(false, false, BuildState.SUCCESS, BuildState.UNKNOWN);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildAborted() throws TimeoutException {
		test(true, true, BuildState.SUCCESS, BuildState.ABORTED);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildFailure() throws TimeoutException {
		test(true, true, BuildState.SUCCESS, BuildState.FAILURE);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void oneBuildUnstable() throws TimeoutException {
		test(true, true, BuildState.SUCCESS, BuildState.UNSTABLE);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void filterRelay1() throws TimeoutException {
		addFilter(1, JOBNAME_0);
		test(false, true, BuildState.SUCCESS, BuildState.FAILURE);
	}

	/**
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Test
	public void filterRelay2() throws TimeoutException {
		addFilter(2, JOBNAME_0);
		test(true, false, BuildState.SUCCESS, BuildState.FAILURE);
	}

	/**
	 * Add a job filter to a port.
	 * 
	 * @param port
	 *            The port.
	 * @param jobs
	 *            The names of the jobs.
	 */
	private void addFilter(int port, String... jobs) {
		DualRelayPortType portType = new DualRelayPortType();
		portType.setId(port);
		JobsType jobsType = portType.getJobs();
		if (jobsType == null) {
			jobsType = new JobsType();
			portType.setJobs(jobsType);
		}
		for (String job : jobs) {
			jobsType.getJob().add(job);
		}
		configuration.getPort().add(portType);
	}

	/**
	 * Verifies the expected behavior of the {@link DualRelayNotifierBricklet}.
	 * 
	 * @param relay1
	 *            The expected state of relay 1.
	 * @param relay2
	 *            The expected state if relay 2.
	 * @param buildStates
	 *            The {@link BuildState}s.
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	private void test(boolean relay1, boolean relay2, BuildState... buildStates)
			throws TimeoutException {
		notifier.preUpdate();
		int i = 0;
		for (BuildState buildState : buildStates) {
			notifier.update(JobStateBuilder.create(Integer.toString(i),
					buildState));
			i++;
		}
		notifier.postUpdate();
		verify(mock).setState(relay1, relay2);
		verifyNoMoreInteractions(mock);
	}
}
