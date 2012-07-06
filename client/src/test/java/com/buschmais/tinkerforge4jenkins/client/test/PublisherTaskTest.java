package com.buschmais.tinkerforge4jenkins.client.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.buschmais.tinkerforge4jenkins.client.JenkinsHttpClient;
import com.buschmais.tinkerforge4jenkins.client.PublisherTask;
import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.NotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.AbstractBrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.util.JobStateBuilder;
import com.tinkerforge.Device;

/**
 * Tests for the publisher task.
 * 
 * @author dirk.mahler
 */
public class PublisherTaskTest {

	private JenkinsHttpClient jenkinsHttpClient;

	private PublisherTask publisherTask;

	private NotifierDevice<Device, AbstractBrickletConfigurationType> notifierDevice;

	/**
	 * Initializes the required mocks.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		jenkinsHttpClient = mock(JenkinsHttpClient.class);
		notifierDevice = mock(NotifierDevice.class);
		List<NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType>> notifiers = new ArrayList<NotifierDevice<? extends Device, ? extends AbstractBrickletConfigurationType>>();
		notifiers.add(notifierDevice);
		publisherTask = new PublisherTask(jenkinsHttpClient, notifiers);
	}

	/**
	 * Successful polling of and publishing.
	 * 
	 * @throws IOException
	 *             If polling fails.
	 */
	@Test
	public void pollAndPublishStates() throws IOException {
		JobState jobState = JobStateBuilder.create("Job 1", BuildState.SUCCESS);
		when(jenkinsHttpClient.getJobStates()).thenReturn(
				Arrays.asList(new JobState[] { jobState }));
		publisherTask.run();
		verify(notifierDevice).preUpdate();
		verify(notifierDevice).update(jobState);
		verify(notifierDevice).postUpdate();
	}

	/**
	 * Polling fails with an error.
	 * 
	 * @throws IOException
	 *             If polling fails.
	 */
	@Test
	public void pollAndPublishError() throws IOException {
		String message = "error message";
		IOException exception = new IOException(message);
		when(jenkinsHttpClient.getJobStates()).thenThrow(exception);
		publisherTask.run();
		verify(notifierDevice).preUpdate();
		verify(notifierDevice).updateFailed(message);
		verify(notifierDevice).postUpdate();
	}
}
