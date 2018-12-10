package de.sebastiankopp.scalog.client.appender;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonHelpers {
	
	public static Map<String,Object> asMap(String jsonObject) {
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(jsonObject, new TypeReference<Map<String,Object>>(){});
		} catch (IOException e) {
			throw new IllegalStateException("IO exception occured", e);
		}
	}
}
