package com.buschmais.tinkerforge4jenkins.core;

/**
 * Represents the state of Jenkins build.
 * 
 * @author dirk.mahler
 */
public enum BuildState {
	/**
	 * The last build has been aborted.
	 */
	ABORTED,
	/**
	 * The last build failed.
	 */
	FAILURE,

	/**
	 * The job has not been built.
	 */
	NOT_BUILT,
	/**
	 * The last build was successful.
	 */
	SUCCESS,
	/**
	 * The last build was unstable.
	 */
	UNSTABLE,
	/**
	 * The state of the last build is unknown.
	 */
	UNKNOWN;
}
