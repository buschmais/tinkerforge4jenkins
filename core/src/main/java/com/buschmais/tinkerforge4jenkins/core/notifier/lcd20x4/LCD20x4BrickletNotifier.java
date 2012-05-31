package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import java.util.Iterator;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractBrickletNotifier;
import com.tinkerforge.BrickletLCD20x4;

public class LCD20x4BrickletNotifier extends AbstractBrickletNotifier {

	private BrickletLCD20x4 bricklet;

	public LCD20x4BrickletNotifier(BrickletLCD20x4 brickletLCD20x4) {
		this.bricklet = brickletLCD20x4;
	}

	@Override
	public void preUpdate() {
		bricklet.clearDisplay();
		bricklet.writeLine((short) 0, (short) 0, "Updating status...");
	}

	@Override
	public void postUpdate() {
		bricklet.clearDisplay();
		if (getJobsByBuildState(BuildState.ABORTED).isEmpty()
				&& getJobsByBuildState(BuildState.FAILURE).isEmpty()
				&& getJobsByBuildState(BuildState.UNKNOWN).isEmpty()
				&& getJobsByBuildState(BuildState.UNSTABLE).isEmpty()) {
			bricklet.backlightOff();
		} else {
			bricklet.backlightOn();
			Iterator<JobState> iterator = getJobState().values()
					.iterator();
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
					bricklet.writeLine((short) i, (short) 0, statusLine);
					i++;
				}
			}
		}
	}

	@Override
	public void updateFailed(String message) {
		bricklet.clearDisplay();
		bricklet.backlightOn();
		bricklet.writeLine((short) 0, (short) 0, "No status available");
		bricklet.writeLine((short) 1, (short) 0, message);
	}
}
