package com.buschmais.tinkerforge4jenkins.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.DeviceRegistry;
import com.buschmais.tinkerforge4jenkins.core.JobState;

public class TinkerForge4JenkinsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerForge4JenkinsClient.class);

    public static void main(String[] args) {
        LOGGER.info("Starting TinkerForge4Jenkins Client.");
        if (args.length != 1) {
            LOGGER.error("You must specify an URL.");
            System.exit(1);
        }
        String url = args[0];
        int updateInterval = 30;
        LOGGER.info("Polling '{}' with an interval of {}s.", url, Integer.toString(updateInterval));
        DeviceRegistry deviceRegistry = new DeviceRegistry("localhost", 4223);
        List<DeviceNotifier> notifiers = null;
        try {
            notifiers = deviceRegistry.start();
        } catch (IOException e) {
            LOGGER.warn("Cannot connect to devices.", e);
            System.exit(1);
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Publisher(url, notifiers), 0, updateInterval, TimeUnit.SECONDS);
    }

    private static class Publisher implements Runnable {

        private String url;
        private List<DeviceNotifier> notifiers;
        private JenkinsJsonClient jenkinsStatusReader = new JenkinsJsonClient();

        Publisher(String url, List<DeviceNotifier> notifier) {
            this.url = url;
            this.notifiers = notifier;
        }

        @Override
        public void run() {
            LOGGER.debug("Updating status...");
            for (DeviceNotifier notifier : notifiers) {
                notifier.preUpdate();
            }
            List<JobState> states = null;
            try {
                states = jenkinsStatusReader.getJobStates(url);
            } catch (Exception e) {
                LOGGER.warn("Cannot get job states.", e);
                for (DeviceNotifier notifier : notifiers) {
                    notifier.updateFailed(e.getMessage());
                }
            }
            if (states != null) {
                LOGGER.debug(states.toString());
                for (JobState state : states) {
                    for (DeviceNotifier notifier : notifiers) {
                        notifier.update(state);
                    }
                }
            }
            for (DeviceNotifier notifier : notifiers) {
                notifier.postUpdate();
            }
        }

    }
}
