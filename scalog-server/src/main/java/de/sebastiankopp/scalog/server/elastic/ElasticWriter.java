package de.sebastiankopp.scalog.server.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
public class ElasticWriter {
	
	@Autowired
	ObjectMapper objectMapper;
	
	private final WebTarget target;
	public ElasticWriter(final String baseUri) {
		target = ClientBuilder.newClient()
				.target(baseUri);
	}
	
	public void writeToIndex(final ObjectNode logMsgObject) {
		final Response response = target.request().buildPut(entity(logMsgObject, APPLICATION_JSON))
				.invoke();
	}
	
	public void writeToIndex(final String logMsgAsJson) {
		try {
			writeToIndex((ObjectNode) objectMapper.reader(ObjectNode.class).readValue(logMsgAsJson));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
