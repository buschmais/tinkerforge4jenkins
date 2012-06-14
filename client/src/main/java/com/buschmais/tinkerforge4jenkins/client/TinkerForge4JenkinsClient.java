package com.buschmais.tinkerforge4jenkins.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.buschmais.tinkerforge4jenkins.core.DeviceNotifier;
import com.buschmais.tinkerforge4jenkins.core.DeviceRegistry;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.BrickletConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.JenkinsConfigurationType;
import com.buschmais.tinkerforge4jenkins.core.schema.configuration.v1.ObjectFactory;
import com.tinkerforge.Device;

public class TinkerForge4JenkinsClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TinkerForge4JenkinsClient.class);

    public static void main(String[] args) {
        LOGGER.info("Starting TinkerForge4Jenkins Client.");
        String configurationFileName = "tinkerforge4jenkins.xml";
        if (args.length == 1) {
            configurationFileName = args[0];
        }
        ConfigurationType configuration = null;
        try {
            configuration = getConfiguration(configurationFileName);
        } catch (IOException e) {
            logErrorAndExit("Cannot open configuration file.", e);
        } catch (JAXBException e) {
            logErrorAndExit("Cannot read configuration file.", e);
        } catch (SAXException e) {
            logErrorAndExit("Cannot read configuration file.", e);
        }
        JenkinsConfigurationType jenkinsConfiguration = configuration.getJenkins();
        LOGGER.info("Polling '{}' with an interval of {}s.", jenkinsConfiguration.getUrl(), Integer
                .toString(jenkinsConfiguration.getUpdateInterval()));
        DeviceRegistry deviceRegistry = new DeviceRegistry("localhost", 4223, configuration.getBricklets());
        List<DeviceNotifier<? extends Device, ? extends BrickletConfigurationType>> notifiers = null;
        try {
            notifiers = deviceRegistry.start();
        } catch (IOException e) {
            logErrorAndExit("Cannot connect to devices.", e);
        }
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new StatusPublisher(configuration.getJenkins(), notifiers), 0, jenkinsConfiguration
                .getUpdateInterval(), TimeUnit.SECONDS);
    }

    private static void logErrorAndExit(String message, Throwable e) {
        LOGGER.error(message, e);
        System.exit(1);
    }

    private static ConfigurationType getConfiguration(String fileName) throws JAXBException, FileNotFoundException, SAXException {
        File file = new File(fileName);
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        InputStream is = new FileInputStream(file);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream schemaStream = TinkerForge4JenkinsClient.class.getResourceAsStream("/META-INF/xsd/tf4j_configuration_1_0.xsd");
        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(schemaStream));
        unmarshaller.setSchema(schema);
        JAXBElement<ConfigurationType> element = unmarshaller.unmarshal(new StreamSource(is), ConfigurationType.class);
        return element.getValue();
    }
}
