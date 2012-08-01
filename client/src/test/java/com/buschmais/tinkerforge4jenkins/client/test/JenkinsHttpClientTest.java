package com.buschmais.tinkerforge4jenkins.client.test;

import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.ABORTED;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.FAILURE;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.NOT_BUILT;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.SUCCESS;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.UNKNOWN;
import static com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType.UNSTABLE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

import com.buschmais.tinkerforge4jenkins.client.JenkinsHttpClient;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JenkinsConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;

/**
 * Tests for the Jenkins HTTP client.
 */
public class JenkinsHttpClientTest {

	private HttpClient httpClientMock;

	@Before
	public void init() {
		httpClientMock = mock(HttpClient.class);
	}

	/**
	 * Get all jobs and retrieve the state of the last build.
	 * 
	 * @throws IOException
	 *             If the test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void lastBuilds() throws IOException {
		JenkinsConfigurationType configuration = new JenkinsConfigurationType();
		JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(
				configuration, httpClientMock);
		when(
				httpClientMock.execute(any(HttpGet.class),
						any(ResponseHandler.class))).thenReturn(
				get("jobs.json"), get("lastBuild1_SUCCESS.json"),
				get("lastBuild2_FAILURE.json"),
				get("lastBuild3_UNSTABLE.json"),
				get("lastBuild4_ABORTED.json"),
				get("lastBuild5_NOT_BUILT.json"),
				get("lastBuild6_UNKNOWN.json"));
		List<JobState> jobStates = jenkinsHttpClient.getJobStates();
		assertEquals(6, jobStates.size());
		Map<String, BuildStateType> expectedStates = new HashMap<String, BuildStateType>();
		expectedStates.put("Job1", SUCCESS);
		expectedStates.put("Job2", FAILURE);
		expectedStates.put("Job3", UNSTABLE);
		expectedStates.put("Job4", ABORTED);
		expectedStates.put("Job5", NOT_BUILT);
		expectedStates.put("Job6", UNKNOWN);
		for (JobState jobState : jobStates) {
			Assert.assertEquals(jobState.getBuildState(),
					expectedStates.get(jobState.getName()));
		}
	}

	/**
	 * Get filtered jobs and retrieve the state of the last build.
	 * 
	 * @throws IOException
	 *             If the test fails.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void lastBuildsWithFilter() throws IOException {
		JenkinsConfigurationType configuration = new JenkinsConfigurationType();
		JobsType jobsType = new JobsType();
		jobsType.getJob().add("Job1");
		configuration.setJobs(jobsType);
		JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(
				configuration, httpClientMock);
		when(
				httpClientMock.execute(any(HttpGet.class),
						any(ResponseHandler.class))).thenReturn(
				get("jobs.json"), get("lastBuild1_SUCCESS.json"));
		List<JobState> jobStates = jenkinsHttpClient.getJobStates();
		assertEquals(1, jobStates.size());
		Map<String, BuildStateType> expectedStates = new HashMap<String, BuildStateType>();
		expectedStates.put("Job1", SUCCESS);
		for (JobState jobState : jobStates) {
			Assert.assertEquals(jobState.getBuildState(),
					expectedStates.get(jobState.getName()));
		}
	}

	/**
	 * Returns the content of a classpath resource as {@link String}.
	 * 
	 * @param name
	 *            The name of the classpath resource.
	 * @return The content of the resource.
	 * @throws IOException
	 *             If the resource cannot be read.
	 */
	private String get(String name) throws IOException {
		return IOUtils.toString(JenkinsHttpClientTest.class
				.getResourceAsStream("/" + name));
	}
}
