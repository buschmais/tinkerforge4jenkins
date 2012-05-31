package com.buschmais.tf4jenkins.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class JsonReaderTest {

	@Test
	public void write() throws IOException {
		Map<String, Object> root= new HashMap<String, Object>();
		root.put("name", "Dirk");		
		Map<String, Object> child = new HashMap<String, Object>();
		child.put("city", "Dresden");
		root.put("address", child);
		ObjectMapper objectMapper = new ObjectMapper();
		StringWriter writer = new StringWriter();
		objectMapper.writer().writeValue(writer, root);
		System.out.println(writer.toString());
	}

}
