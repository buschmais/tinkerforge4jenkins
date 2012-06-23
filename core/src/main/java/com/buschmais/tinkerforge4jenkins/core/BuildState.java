package com.buschmais.tinkerforge4jenkins.core;

/**
 * Represents the state of Jenkins build.
 * <p>
 * The order represents the severity of the state
 * </p>
 * 
 * @author dirk.mahler
 */
public enum BuildState {
	/**
	 * The last build failed.
	 */
	FAILURE,

	/**
	 * The last build was unstable.
	 */
	UNSTABLE,

	/**
	 * The last build has been aborted.
	 */
	ABORTED,

	/**
	 * The state of the last build is unknown.
	 */
	UNKNOWN,

	/**
	 * The job has not been built.
	 */
	NOT_BUILT,

	/**
	 * The last build was successful.
	 */
	SUCCESS;
}
