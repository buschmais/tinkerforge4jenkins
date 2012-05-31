package com.buschmais.tinkerforge4jenkins.core;

public interface BrickletNotifier {

	void preUpdate();
	
	void update(JobState state);
	
	void postUpdate();

	void updateFailed(String message);
		
}
