package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import java.util.Iterator;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractDeviceNotifier;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLCD20x4.ButtonPressedListener;

public class LCD20x4BrickletNotifier extends
		AbstractDeviceNotifier<BrickletLCD20x4> implements ButtonPressedListener {

	public LCD20x4BrickletNotifier(BrickletLCD20x4 brickletLCD20x4) {
		super(brickletLCD20x4);
		brickletLCD20x4.addListener(this);
	}

	@Override
	public void preUpdate() {
		getDevice().clearDisplay();
		getDevice().writeLine((short) 0, (short) 0, "Updating status...");
	}

	@Override
	public void postUpdate() {
		getDevice().clearDisplay();
		if (getJobsByBuildState(BuildState.ABORTED).isEmpty()
				&& getJobsByBuildState(BuildState.FAILURE).isEmpty()
				&& getJobsByBuildState(BuildState.UNKNOWN).isEmpty()
				&& getJobsByBuildState(BuildState.UNSTABLE).isEmpty()) {
			getDevice().backlightOff();
		} else {
			getDevice().backlightOn();
			Iterator<JobState> iterator = getJobState().values().iterator();
			int i = 0;
			while (iterator.hasNext() && i < 4) {
				JobState summary = iterator.next();
				if (!BuildState.SUCCESS.equals(summary.getBuildState())) {
					char symbol;
					switch (summary.getBuildState()) {
					case ABORTED:
						symbol = 'A';
						break;
					case NOT_BUILT:
						symbol = 'N';
						break;
					case UNSTABLE:
						symbol = 'U';
						break;
					case FAILURE:
						symbol = 'F';
						break;
					case UNKNOWN:
					default:
						symbol = '?';
					}
					String statusLine = symbol + " " + summary.getName();
					getDevice().writeLine((short) i, (short) 0, statusLine);
					i++;
				}
			}
		}
	}

	@Override
	public void updateFailed(String message) {
		getDevice().clearDisplay();
		getDevice().backlightOn();
		getDevice().writeLine((short) 0, (short) 0, "No status available");
		getDevice().writeLine((short) 1, (short) 0, message);
	}

	@Override
	public void buttonPressed(short button) {
	}
}
