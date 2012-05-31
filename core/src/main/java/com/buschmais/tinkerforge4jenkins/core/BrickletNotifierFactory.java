package com.buschmais.tinkerforge4jenkins.core;


public interface BrickletNotifierFactory {

	boolean match(String identifier);
	
	BrickletNotifier create(String uid);
	
}
