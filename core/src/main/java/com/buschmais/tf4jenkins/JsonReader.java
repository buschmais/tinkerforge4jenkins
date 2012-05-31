package com.buschmais.tf4jenkins;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonReader {

	public JsonNode read(String url) throws HttpException, IOException {
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
	
}
