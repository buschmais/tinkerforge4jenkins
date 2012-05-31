package com.buschmais.tinkerforge4jenkins.core;

public class JobState {

	private String name;

	private BuildState buildState;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public BuildState getBuildState() {
		return buildState;
	}

	/**
	 * @param buildState
	 *            the status to set
	 */
	public void setBuildState(BuildState buildState) {
		this.buildState = buildState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JobState [name=" + name + ", status=" + buildState + "]";
	}

}
