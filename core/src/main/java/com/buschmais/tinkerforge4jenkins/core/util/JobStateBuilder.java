package com.buschmais.tinkerforge4jenkins.core.util;

import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType;

/**
 * Builder for {@link JobState} instances.
 * 
 * @author dirk.mahler
 */
public final class JobStateBuilder {

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
	 *            The {@link BuildStateType} of the job.
	 * @param building
	 *            <code>true</code> if the job is currently building.
	 * @return The {@link JobState} instance.
	 */
	public static JobState create(String name, BuildStateType buildState,
			boolean building) {
		JobState jobState = new JobState();
		jobState.setName(name);
		jobState.setBuildState(buildState);
		jobState.setBuilding(building);
		return jobState;
	}

	/**
	 * Creates a {@link JobState} instance.
	 * 
	 * @param name
	 *            The name of the job.
	 * @param buildState
	 *            The string representing the {@link BuildStateType} of the job.
	 * @param building
	 *            <code>true</code> if the job is currently building.
	 * @return The {@link JobState} instance.
	 */
	public static JobState create(String name, String buildState,
			boolean building) {
		return create(name, BuildStateType.valueOf(buildState), building);
	}
}
