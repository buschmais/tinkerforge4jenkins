package com.buschmais.tf4jenkins.notifier;

import java.util.Iterator;

import com.buschmais.tf4jenkins.JobStatus;
import com.buschmais.tf4jenkins.JobSummary;
import com.tinkerforge.BrickletLCD20x4;

public class LCD20x4BrickletNotifier extends StatefulBrickletNotifier {

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
		if (getJobsByStatus(JobStatus.ABORTED).isEmpty()
				&& getJobsByStatus(JobStatus.FAILURE).isEmpty()
				&& getJobsByStatus(JobStatus.UNKNOWN).isEmpty()
				&& getJobsByStatus(JobStatus.UNSTABLE).isEmpty()) {
			bricklet.backlightOff();
		} else {
			bricklet.backlightOn();
			Iterator<JobSummary> iterator = getJobSummaries().values()
					.iterator();
			int i = 0;
			while (iterator.hasNext() && i < 4) {
				JobSummary summary = iterator.next();
				if (!JobStatus.SUCCESS.equals(summary.getStatus())) {
					char symbol;
					switch (summary.getStatus()) {
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
