package com.buschmais.tinkerforge4jenkins.core.notifier.lcd;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;
import com.buschmais.tinkerforge4jenkins.core.notifier.common.AbstractNotifierDevice;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.AbstractLCDConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JobsType;
import com.tinkerforge.BrickletLCD20x4;
import com.tinkerforge.BrickletLCD20x4.ButtonPressedListener;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection.TimeoutException;

/**
 * Abstract base implementation for LCD notifier devices.
 * 
 * @author dirk.mahler
 */
public abstract class AbstractLCDNotifierBricklet<T extends Device, C extends AbstractLCDConfigurationType> extends AbstractNotifierDevice<T, C> implements ButtonPressedListener {

    /**
     * {@link Comparator} implementation comparing {@link JobState}s by severity of their {@link BuildState}.
     * 
     * @author dirk.mahler
     */
    private static class JobStateSeverityComparator implements Comparator<JobState>, Serializable {
        /**
         * The serial version UID.
         */
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(JobState o1, JobState o2) {
            return o1.getBuildState().compareTo(o2.getBuildState());
        }
    }

    /**
     * The suffix to use if displayed job names do not fit to the size of the LCD.
     */
    private static final String LONG_JOBNAME_SUFFIX = ">";

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLCDNotifierBricklet.class);

    /**
     * Constructor.
     * 
     * @param uid
     *            The device uid.
     * @param bricklet
     *            The LCD device.
     */
    public AbstractLCDNotifierBricklet(String uid, T device) {
        super(uid, device);
        addListener(this);
    }

    protected abstract void addListener(AbstractLCDNotifierBricklet<T, C> notifier);

    /**
     * Return the maximum number of rows that can be dislayed on the bricklet.
     * 
     * @return The number of rows.
     */
    protected abstract int getMaximumRows();

    /**
     * Return the maximum number of columns that can be dislayed on the bricklet.
     * 
     * @return The number of columns.
     */
    protected abstract int getMaximumColumns();

    /**
     * Clear the display.
     */
    protected abstract void clearDisplay();

    /**
     * Return if the backlight is on.
     * 
     * @return <code>true</code> if the backlight is on.
     * @throws TimeoutException
     *             If a timeout occured.
     */
    protected abstract boolean isBacklightOn() throws TimeoutException;

    /**
     * Switch the backlight off.
     */
    protected abstract void backlightOff();

    /**
     * Switch the backlight on.
     */
    protected abstract void backlightOn();

    /**
     * Write a text to the bricklet.
     * 
     * @param line
     *            The line (row).
     * @param position
     *            The position (column).
     * @param text
     *            The text.
     */
    protected abstract void writeLine(int line, int position, String text);

    /**
     * Switches the back light on or off.
     * 
     * @param state
     *            The new state, <code>true</code> indicates that the back light should be switched on.
     */
    private void setBackLight(boolean state) {
        LOGGER.debug("switching backlight: " + state);
        try {
            if (!isBacklightOn() == state) {
                if (state) {
                    backlightOn();
                } else {
                    backlightOff();
                }
            }
        } catch (TimeoutException e) {
            LOGGER.warn("Cannot switch backlight.", e);
        }
    }

    @Override
    public void preUpdate() {
        LOGGER.debug("clearing display before updating.");
        clearDisplay();
        writeLine((short) 0, (short) 0, "Updating status...");
    }

    @Override
    public void postUpdate() {
        LOGGER.debug("Clearing display before writing to device.");
        clearDisplay();
        C configuration = getConfiguration();
        JobsType filter = null;
        if (configuration != null) {
            filter = configuration.getJobs();
        }
        Set<JobState> allJobs = filter(getJobStates().values(), filter);
        Set<JobState> successfulJobs = filter(getJobsByBuildState(BuildState.SUCCESS), filter);
        if (successfulJobs.size() == allJobs.size()) {
            LOGGER.debug("No information to be written to device.");
            setBackLight(false);
        } else {
            writeJobStates(allJobs);
            setBackLight(true);
        }
    }

    /**
     * Writes the (non-successful) state of the given jobs to the LCD bricklet.
     * 
     * @param jobs
     *            The {@link JobState}s.
     */
    private void writeJobStates(Set<JobState> jobs) {
        LOGGER.debug("Writing job states to device.");
        // Sort the jobs by the severity of their build state.
        SortedSet<JobState> sortedJobs = new TreeSet<JobState>(new JobStateSeverityComparator());
        sortedJobs.addAll(jobs);
        Iterator<JobState> iterator = sortedJobs.iterator();
        int i = 0;
        while (iterator.hasNext() && i < getMaximumRows()) {
            JobState summary = iterator.next();
            if (!BuildState.SUCCESS.equals(summary.getBuildState())) {
                String statusLine = createStatusLine(summary.getName(), summary.getBuildState());
                writeLine((short) i, (short) 0, statusLine);
                i++;
            }
        }
    }

    @Override
    public void updateFailed(String message) {
        clearDisplay();
        setBackLight(true);
        writeLine((short) 0, (short) 0, "No status available");
        writeLine((short) 1, (short) 0, message);
    }

    @Override
    public void buttonPressed(short button) {
    }

    /**
     * Returns the status line to display on the {@link BrickletLCD20x4} for the given job name and {@link BuildState}.
     * 
     * @param jobName
     *            The job name.
     * @param buildState
     *            The {@link BuildState}.
     * @return The message.
     */
    public String createStatusLine(String jobName, BuildState buildState) {
        String displayedJobName;
        int maximumColumns = getMaximumColumns();
        if (jobName.length() > maximumColumns - 2) {
            displayedJobName = jobName.substring(0, maximumColumns - LONG_JOBNAME_SUFFIX.length() - 2) + LONG_JOBNAME_SUFFIX;
        } else {
            displayedJobName = jobName;
        }
        return getBuildStateSymbol(buildState) + " " + displayedJobName;
    }

    /**
     * Returns the symbol to display for the given {@link BuildState}.
     * 
     * @param buildState
     *            The {@link BuildState}.
     * @return The symbol or '?' if the state is unknown.
     */
    private char getBuildStateSymbol(BuildState buildState) {
        switch (buildState) {
            case ABORTED:
                return 'A';
            case NOT_BUILT:
                return 'N';
            case UNSTABLE:
                return 'U';
            case FAILURE:
                return 'F';
            case UNKNOWN:
            default:
                return '?';
        }
    }
}