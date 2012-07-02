package com.buschmais.tinkerforge4jenkins.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.NotifierDeviceFactory;
import com.buschmais.tinkerforge4jenkins.core.registry.NotifierDeviceEnumerateListener;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.TinkerForgeConfigurationType.Bricklets;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Tests for the {@link NotifierDeviceEnumerateListener}.
 * 
 * @author dirk.mahler
 * 
 */
public class NotifierDeviceEnumerateListenerTest {

	private static final String MOCKDEVICE_UID = "123";

	private static final String MOCKDEVICE_IDENTIFIER = "MockDevice 1.0";

	/**
	 * The listener under test.
	 */
	private NotifierDeviceEnumerateListener listener;

	/**
	 * The {@link IPConnection}.
	 */
	private IPConnection ipConnection;

	/**
	 * The {@link Iterable} representing the service loader.
	 */
	private Iterable<NotifierDeviceFactory> serviceLoader;

	/**
	 * The {@link TinkerForgeConfigurationType}.
	 */
	private TinkerForgeConfigurationType configuration;

	/**
	 * The {@link Bricklets}.
	 */
	private Bricklets bricklets;

	/**
	 * The {@link BrickletConfigurationType}.
	 */
	private BrickletConfigurationType brickletConfiguration;

	/**
	 * The {@link NotifierDeviceFactory}.
	 */
	private NotifierDeviceFactory deviceFactory;

	/**
	 * The {@link NotifierDevice}.
	 */
	private NotifierDevice notifierDevice;

	/**
	 * The {@link Device}.
	 */
	private Device bricklet;

	/**
	 * The Iterator over {@link NotifierDeviceFactory}s.
	 */
	private Iterator<NotifierDeviceFactory> deviceFactoryIterator;

	/**
	 * Initialize the mocks.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		ipConnection = mock(IPConnection.class);
		serviceLoader = mock(Iterable.class);
		configuration = mock(TinkerForgeConfigurationType.class);
		bricklets = mock(Bricklets.class);
		brickletConfiguration = mock(BrickletConfigurationType.class);
		deviceFactory = mock(NotifierDeviceFactory.class);
		notifierDevice = mock(NotifierDevice.class);
		bricklet = mock(Device.class);
		deviceFactoryIterator = mock(Iterator.class);

		when(serviceLoader.iterator()).thenReturn(deviceFactoryIterator);
		when(deviceFactoryIterator.hasNext()).thenReturn(true, false);
		when(deviceFactoryIterator.next()).thenReturn(deviceFactory);
		when(deviceFactory.match(MOCKDEVICE_IDENTIFIER)).thenReturn(true);
		when(deviceFactory.create(MOCKDEVICE_UID)).thenReturn(notifierDevice);
		when(notifierDevice.getDevice()).thenReturn(bricklet);
	}

	/**
	 * Add a {@link NotifierDevice} without any existing configuration.
	 * 
	 * @throws TimeoutException
	 *             If the test fails.
	 */
	@Test
	public void addNotifierWithoutConfiguration() throws TimeoutException {
		listener = new NotifierDeviceEnumerateListener(ipConnection,
				serviceLoader, null);
		listener.enumerate(MOCKDEVICE_UID, MOCKDEVICE_IDENTIFIER, (short) 0, true);

		verify(ipConnection).addDevice(bricklet);
		assertEquals(1, listener.getNotifierDevices().size());
		assertEquals(notifierDevice, listener.getNotifierDevices().get(MOCKDEVICE_UID));
	}

	/**
	 * Add a {@link NotifierDevice} with an existing configuration.
	 * 
	 * @throws TimeoutException
	 *             If the test fails.
	 */
	@Test
	public void addNotifierWithMatchingConfiguration() throws TimeoutException {
		when(configuration.getBricklets()).thenReturn(bricklets);
		when(bricklets.getDualRelayOrLcd20X4()).thenReturn(
				Arrays.asList(brickletConfiguration));
		when(brickletConfiguration.getUid()).thenReturn(MOCKDEVICE_UID);

		listener = new NotifierDeviceEnumerateListener(ipConnection,
				serviceLoader, configuration);
		listener.enumerate(MOCKDEVICE_UID, MOCKDEVICE_IDENTIFIER, (short) 0, true);

		verify(notifierDevice).setConfiguration(brickletConfiguration);
		verify(ipConnection).addDevice(bricklet);
		assertEquals(1, listener.getNotifierDevices().size());
		assertEquals(notifierDevice, listener.getNotifierDevices().get(MOCKDEVICE_UID));
	}

	/**
	 * Add a {@link NotifierDevice} with a configuration that does not match the
	 * device UID.
	 * 
	 * @throws TimeoutException
	 *             If the test fails.
	 */
	@Test
	public void addNotifierWithNonMatchingConfiguration()
			throws TimeoutException {
		when(configuration.getBricklets()).thenReturn(bricklets);
		when(bricklets.getDualRelayOrLcd20X4()).thenReturn(
				Arrays.asList(brickletConfiguration));
		when(brickletConfiguration.getUid()).thenReturn("456");

		listener = new NotifierDeviceEnumerateListener(ipConnection,
				serviceLoader, configuration);
		listener.enumerate(MOCKDEVICE_UID, MOCKDEVICE_IDENTIFIER, (short) 0, true);

		verify(notifierDevice, never()).setConfiguration(brickletConfiguration);
		verify(ipConnection).addDevice(bricklet);
		assertEquals(1, listener.getNotifierDevices().size());
		assertEquals(notifierDevice, listener.getNotifierDevices().get(MOCKDEVICE_UID));
	}

	/**
	 * Add and remove a {@link NotifierDevice}.
	 * 
	 * @throws TimeoutException
	 *             If the test fails.
	 */
	@Test
	public void addAndRemoveNotifier() throws TimeoutException {
		listener = new NotifierDeviceEnumerateListener(ipConnection,
				serviceLoader, configuration);
		listener.enumerate(MOCKDEVICE_UID, MOCKDEVICE_IDENTIFIER, (short) 0, true);

		verify(notifierDevice, never()).setConfiguration(brickletConfiguration);
		verify(ipConnection).addDevice(bricklet);
		assertEquals(1, listener.getNotifierDevices().size());
		assertEquals(notifierDevice, listener.getNotifierDevices().get(MOCKDEVICE_UID));

		listener.enumerate(MOCKDEVICE_UID, MOCKDEVICE_IDENTIFIER, (short) 0, false);
		assertEquals(0, listener.getNotifierDevices().size());
		assertFalse(listener.getNotifierDevices().containsKey(MOCKDEVICE_UID));
	}

	/**
	 * Add an unsupported {@link NotifierDevice}.
	 * 
	 * @throws TimeoutException
	 *             If the test fails.
	 */
	@Test
	public void addUnsupportedNotifier() throws TimeoutException {
		listener = new NotifierDeviceEnumerateListener(ipConnection,
				serviceLoader, null);
		listener.enumerate(MOCKDEVICE_UID, "UnsupportedDevice 1.0", (short) 0, true);

		verify(ipConnection, never()).addDevice(bricklet);
		assertEquals(0, listener.getNotifierDevices().size());
		assertFalse(listener.getNotifierDevices().containsKey(MOCKDEVICE_UID));
	}

}
