package com.buschmais.tf4jenkins;

public interface BrickletNotifier {

	void preUpdate();
	
	void setSummary(JobSummary summary);
	
	void postUpdate();

	void updateFailed(String message);
		
}
