package com.buschmais.tinkerforge4jenkins.core.test.util;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;

/**
 * Builder for {@link JobState} instances.
 * 
 * @author dirk.mahler
 */
public class JobStateBuilder {

	/**
	 * Private constructor.
	 */
	private JobStateBuilder() {
	}

	/**
	 * Creates a {@link JobState} instance.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param buildState
	 *            The {@link BuildState} of the job.
	 * @return The {@link JobState} instance.
	 */
	public static JobState create(String name, BuildState buildState) {
		JobState jobState = new JobState();
		jobState.setName(name);
		jobState.setBuildState(buildState);
		return jobState;
	}

}
