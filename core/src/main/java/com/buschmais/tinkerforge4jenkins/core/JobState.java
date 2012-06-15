package com.buschmais.tinkerforge4jenkins.core;

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
	private BuildState buildState;

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
	public BuildState getBuildState() {
		return buildState;
	}

	/**
	 * Set the state of the last build.
	 * 
	 * @param buildState
	 *            the state to set
	 */
	public void setBuildState(BuildState buildState) {
		this.buildState = buildState;
	}

	@Override
	public String toString() {
		return "JobState [name=" + name + ", status=" + buildState + "]";
	}

}
