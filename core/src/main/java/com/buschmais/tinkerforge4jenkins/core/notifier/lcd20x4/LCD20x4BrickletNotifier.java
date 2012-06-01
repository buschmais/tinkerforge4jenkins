package com.buschmais.tinkerforge4jenkins.core.notifier.lcd20x4;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractDeviceNotifier;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLCD20x4.ButtonPressedListener;
import com.tinkerforge.IPConnection.TimeoutException;

public class LCD20x4BrickletNotifier extends AbstractDeviceNotifier<BrickletLCD20x4> implements ButtonPressedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LCD20x4BrickletNotifier.class);

    public LCD20x4BrickletNotifier(BrickletLCD20x4 brickletLCD20x4) {
        super(brickletLCD20x4);
        brickletLCD20x4.addListener(this);
    }

    private void setBackLight(boolean state) {
        try {
            if (!getDevice().isBacklightOn() == state) {
                if (state) {
                    getDevice().backlightOn();
                } else {
                    getDevice().backlightOff();
                }
            }
        } catch (TimeoutException e) {
            LOGGER.warn("Cannot switch backlight.", e);
        }
    }

    @Override
    public void preUpdate() {
        getDevice().clearDisplay();
        getDevice().writeLine((short) 0, (short) 0, "Updating status...");
    }

    @Override
    public void postUpdate() {
        getDevice().clearDisplay();
        if (getJobsByBuildState(BuildState.SUCCESS).size() == getJobStates().size()) {
            setBackLight(false);
        } else {
            setBackLight(true);
            Iterator<JobState> iterator = getJobStates().values().iterator();
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
        setBackLight(true);
        getDevice().writeLine((short) 0, (short) 0, "No status available");
        getDevice().writeLine((short) 1, (short) 0, message);
    }

    @Override
    public void buttonPressed(short button) {
    }
}
