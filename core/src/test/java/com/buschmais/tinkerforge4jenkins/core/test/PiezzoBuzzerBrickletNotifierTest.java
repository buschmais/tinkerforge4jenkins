package com.buschmais.tinkerforge4jenkins.core.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.buschmais.tinkerforge4jenkins.core.notifier.piezo.PiezoBuzzerNotifierBricklet;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.PiezoBuzzerConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.util.JobStateBuilder;
import com.tinkerforge.BrickletPiezoBuzzer;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Tests for the {@link PiezzoBuzzerBrickletNotifier}.
 * 
 * @author dirk.mahler
 */
public class PiezzoBuzzerBrickletNotifierTest extends
		AbstractBrickletNotifierTest {

	/**
	 * The mock.
	 */
	private BrickletPiezoBuzzer mock;

	/**
	 * The notifier under test.
	 */
	private PiezoBuzzerNotifierBricklet notifier;

	/**
	 * The configuration.
	 */
	private PiezoBuzzerConfigurationType configuration;

	/**
	 * Initialize.
	 * 
	 * @throws TimeoutException
	 *             If a timeout occurs.
	 */
	@Before
	public void createBricklet() throws TimeoutException {
		mock = Mockito.mock(BrickletPiezoBuzzer.class);
		notifier = new PiezoBuzzerNotifierBricklet(UID, mock);
		configuration = new PiezoBuzzerConfigurationType();
		configuration.setUid(UID);
		notifier.setConfiguration(configuration);
	}

	@Test
	public void buildFailure() {
		notifier.preUpdate();
		notifier.update(JobStateBuilder.create("1", BuildStateType.SUCCESS,
				false));
		notifier.postUpdate();
	}
}
