package com.buschmais.tf4jenkins.notifier;

import com.buschmais.tf4jenkins.JobStatus;
import com.tinkerforge.BrickletDualRelay;

public class DualRelayBrickletNotifier extends StatefulBrickletNotifier {

	private BrickletDualRelay bricklet;

	public DualRelayBrickletNotifier(BrickletDualRelay brickletDualRelay) {
		this.bricklet = brickletDualRelay;
	}

	@Override
	public void preUpdate() {
	}

	@Override
	public void postUpdate() {
		if (getJobsByStatus(JobStatus.ABORTED).isEmpty()
				&& getJobsByStatus(JobStatus.FAILURE).isEmpty()
				&& getJobsByStatus(JobStatus.UNKNOWN).isEmpty()
				&& getJobsByStatus(JobStatus.UNSTABLE).isEmpty()) {
			bricklet.setState(false, false);
		} else {
			bricklet.setState(true, true);
		}
	}

	@Override
	public void updateFailed(String message) {
	}

}
