package com.buschmais.tinkerforge4jenkins.core.notifier.lcd;

import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.LCD16X2ConfigurationType;
import com.tinkerforge.BrickletLCD16x2;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Implementation of a notifier device for LCD 16x2 bricklets.
 * 
 * @author dirk.mahler
 */
public class LCD16x2NotifierBricklet extends AbstractLCDNotifierBricklet<BrickletLCD16x2, LCD16X2ConfigurationType> {

    /**
     * The maximum number of rows that can be displayed.
     */
    private static final int MAXIMUM_ROWS = 2;

    /**
     * The maximum number of columns that can be displayed.
     */
    private static final int MAXIMUM_COLUMNS = 16;

    /**
     * Constructor.
     * 
     * @param uid
     *            The device uid.
     * @param device
     *            The device.
     */
    public LCD16x2NotifierBricklet(String uid, BrickletLCD16x2 device) {
        super(uid, device);
    }

    @Override
    protected int getMaximumRows() {
        return MAXIMUM_ROWS;
    }

    @Override
    protected int getMaximumColumns() {
        return MAXIMUM_COLUMNS;
    }

    @Override
    protected void addListener(AbstractLCDNotifierBricklet<BrickletLCD16x2, LCD16X2ConfigurationType> notifier) {
        getDevice().addListener(this.getDevice());
    }

    @Override
    protected void clearDisplay() {
        getDevice().clearDisplay();
    }

    @Override
    protected boolean isBacklightOn() throws TimeoutException {
        return getDevice().isBacklightOn();
    }

    @Override
    protected void backlightOff() {
        getDevice().backlightOff();
    }

    @Override
    protected void backlightOn() {
        getDevice().backlightOn();
    }

    @Override
    protected void writeLine(int line, int position, String text) {
        getDevice().writeLine((short) line, (short) position, text);
    }
}
