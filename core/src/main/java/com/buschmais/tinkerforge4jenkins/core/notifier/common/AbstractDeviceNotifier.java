package com.buschmais.tinkerforge4jenkins.core.notifier.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.tinkerforge.Device;

public abstract class AbstractDeviceNotifier<T extends Device> implements
		DeviceNotifier {

	private T device;

	private SortedMap<String, JobState> jobStates = new TreeMap<String, JobState>();

	private Map<BuildState, Set<JobState>> jobStatesByBuildState = new HashMap<BuildState, Set<JobState>>();

	protected AbstractDeviceNotifier(T device) {
		this.device = device;
		for (BuildState buildState : BuildState.values()) {
			jobStatesByBuildState.put(buildState, new HashSet<JobState>());
		}
	}

	@Override
	public T getDevice() {
		return device;
	}

	@Override
	public void update(JobState state) {
		JobState previousState = jobStates.put(state.getName(), state);
		if (previousState != null) {
			jobStatesByBuildState.get(previousState.getBuildState()).remove(
					previousState);
		}
		jobStatesByBuildState.get(state.getBuildState()).add(state);
	}

	/**
	 * @return the jobSummaries
	 */
	public SortedMap<String, JobState> getJobStates() {
		return jobStates;
	}

	/**
	 * @return the failedJobs
	 */
	public Set<JobState> getJobsByBuildState(BuildState buildState) {
		return jobStatesByBuildState.get(buildState);
	}

}