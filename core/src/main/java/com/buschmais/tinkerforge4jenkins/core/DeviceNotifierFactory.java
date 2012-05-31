package com.buschmais.tinkerforge4jenkins.core;


public interface DeviceNotifierFactory {

	boolean match(String identifier);
	
	DeviceNotifier create(String uid);
	
}
