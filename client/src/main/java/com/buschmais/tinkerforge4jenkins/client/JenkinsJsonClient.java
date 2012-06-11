package com.buschmais.tinkerforge4jenkins.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buschmais.tinkerforge4jenkins.core.BuildState;
import com.buschmais.tinkerforge4jenkins.core.JobState;

public class JenkinsJsonClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(JenkinsJsonClient.class);

    private Set<String> jobFilter = new HashSet<String>();

    public JenkinsJsonClient() {
        jobFilter.add("[a-zA-Z0-9]+_Continuous");
        jobFilter.add("[a-zA-Z0-9]+_IntegrationTest");
    }

    private JsonNode read(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        HttpMethod method = new GetMethod(url + "/api/json");
        int status = httpClient.executeMethod(method);
        if (status == HttpStatus.SC_OK) {
            InputStream is = method.getResponseBodyAsStream();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.reader().readTree(is);
        }
        return null;

    }

    public List<JobState> getJobStates(String url) throws IOException {
        List<JobState> result = new ArrayList<JobState>();
        JsonNode node = this.read(url);
        if (node == null) {
            LOGGER.warn("Cannot read job states from url '{}'", url);
        } else {
            JsonNode jobsNode = node.get("jobs");
            if (jobsNode != null) {
                for (JsonNode jobNode : jobsNode) {
                    String jobName = jobNode.get("name").getTextValue();
                    for (String filter : jobFilter) {
                        if (Pattern.matches(filter, jobName)) {
                            String jobUrl = jobNode.get("url").getTextValue();
                            JsonNode lastBuildNode = this.read(jobUrl + "lastBuild");
                            String buildState = lastBuildNode.get("result").getTextValue();
                            JobState state = new JobState();
                            state.setName(jobName);
                            if (buildState != null) {
                                state.setBuildState(BuildState.valueOf(buildState));
                            } else {
                                state.setBuildState(BuildState.UNKNOWN);
                            }
                            result.add(state);
                        }
                    }
                }
            }
        }
        return result;
    }

}
