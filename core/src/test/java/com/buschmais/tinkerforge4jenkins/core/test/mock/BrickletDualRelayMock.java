package com.buschmais.tinkerforge4jenkins.core.test.mock;

import com.tinkerforge.BrickletDualRelay;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Mock for the {@link BrickletDualRelay}.
 * 
 * @author dirk.mahler
 */
public class BrickletDualRelayMock extends BrickletDualRelay {

	private boolean relay1;

	private boolean relay2;

	public BrickletDualRelayMock(String uid) {
		super(uid);
	}

	@Override
	public void setState(boolean relay1, boolean relay2) {
		this.relay1 = relay1;
		this.relay2 = relay2;
	}

	@Override
	public State getState() throws TimeoutException {
		State state = new State();
		state.relay1 = relay1;
		state.relay2 = relay2;
		return state;
	}

}
