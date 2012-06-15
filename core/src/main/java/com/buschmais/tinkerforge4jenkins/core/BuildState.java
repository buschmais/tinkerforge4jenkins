package com.buschmais.tinkerforge4jenkins.core;

/**
 * Represents the state of Jenkins build.
 * 
 * @author dirk.mahler
 */
public enum BuildState {
	ABORTED, FAILURE, NOT_BUILT, SUCCESS, UNSTABLE, UNKNOWN;
}
