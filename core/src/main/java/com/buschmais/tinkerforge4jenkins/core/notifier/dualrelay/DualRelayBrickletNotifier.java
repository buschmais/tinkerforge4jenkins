package com.buschmais.tinkerforge4jenkins.core.notifier.dualrelay;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractBrickletNotifier;
import com.tinkerforge.BrickletDualRelay;

public class DualRelayBrickletNotifier extends AbstractBrickletNotifier {

    private BrickletDualRelay bricklet;

    public DualRelayBrickletNotifier(BrickletDualRelay brickletDualRelay) {
        this.bricklet = brickletDualRelay;
    }

    @Override
    public void preUpdate() {
    }

    @Override
    public void postUpdate() {
        if (getJobsByBuildState(BuildState.ABORTED).isEmpty() && getJobsByBuildState(BuildState.FAILURE).isEmpty()
                && getJobsByBuildState(BuildState.UNSTABLE).isEmpty()) {
            bricklet.setState(false, false);
        } else {
            bricklet.setState(true, true);
        }
    }

    @Override
    public void updateFailed(String message) {
    }

}
