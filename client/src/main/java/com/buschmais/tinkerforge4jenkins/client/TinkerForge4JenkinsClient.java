package com.buschmais.tinkerforge4jenkins.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.DeviceRegistry;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ObjectFactory;
import com.tinkerforge.Device;

public class TinkerForge4JenkinsClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TinkerForge4JenkinsClient.class);

	public static void main(String[] args) {
		LOGGER.info("Starting TinkerForge4Jenkins Client.");
		if (args.length != 1) {
			LOGGER.error("You must specify an URL.");
			System.exit(1);
		}
		String url = args[0];
		int updateInterval = 30;
		ConfigurationType configuration = null;
		try {
			configuration = getConfiguration();
		} catch (IOException e) {
			logErrorAndExit("Cannot open configuration file.", e);
		} catch (JAXBException e) {
			logErrorAndExit("Cannot read configuration file.", e);
		}
		LOGGER.info("Polling '{}' with an interval of {}s.", url,
				Integer.toString(updateInterval));
		DeviceRegistry deviceRegistry = new DeviceRegistry("localhost", 4223,
				configuration.getBricklets());
		List<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifiers = null;
		try {
			notifiers = deviceRegistry.start();
		} catch (IOException e) {
			logErrorAndExit("Cannot connect to devices.", e);
		}
		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(new StatusPublisher(
				configuration.getJenkins(), notifiers), 0, updateInterval,
				TimeUnit.SECONDS);
	}

	private static void logErrorAndExit(String message, Throwable e) {
		LOGGER.error(message, e);
		System.exit(1);
	}

	private static ConfigurationType getConfiguration() throws JAXBException,
			FileNotFoundException {
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		InputStream is = new FileInputStream("tinkerforge4jenkins.xml");
		JAXBElement<ConfigurationType> element = jaxbContext
				.createUnmarshaller().unmarshal(new StreamSource(is),
						ConfigurationType.class);
		return element.getValue();
	}
}
