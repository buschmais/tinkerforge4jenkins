package com.buschmais.tinkerforge4jenkins.core;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BuildStateType;

/**
 * Represents a state of a Jenkins job.
 * 
 * @author dirk.mahler
 */
public class JobState {

	/**
	 * The name of the job.
	 */
	private String name;

	/**
	 * The state of the last build.
	 */
	private BuildStateType buildState;

	/**
	 * Indicates if the job is currently building.
	 */
	private boolean building;

	/**
	 * Return the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the state of the last build.
	 * 
	 * @return the state
	 */
	public BuildStateType getBuildState() {
		return buildState;
	}

	/**
	 * Set the state of the last build.
	 * 
	 * @param buildState
	 *            the state to set
	 */
	public void setBuildState(BuildStateType buildState) {
		this.buildState = buildState;
	}

	/**
	 * Return if the job is currently building.
	 * 
	 * @return <code>true</code> if the job is currently building.
	 */
	public boolean isBuilding() {
		return building;
	}

	/**
	 * Set if the job is currently building.
	 * 
	 * @param building
	 *            <code>true</code> if the job is currently building.
	 */
	public void setBuilding(boolean building) {
		this.building = building;
	}

	@Override
	public String toString() {
		return "JobState [name=" + name + ", status=" + buildState
				+ ", building=" + building + "]";
	}

}
