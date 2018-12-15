package de.sebastiankopp.scalog.server.elastic.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
public class ElasticReader extends ElasticClient {
	
	@Autowired
	public ElasticReader(ConfigEnv env, ObjectMapper objectMapper) {
		super(env, objectMapper);
	}
	
	public ObjectNode getLogMsgById(final String logMsgId) {
		final Response response = target.path(logMsgId)
				.path("_source")
				.request(APPLICATION_JSON)
				.buildGet()
				.invoke();
		handleResponseError(response);
		return response.readEntity(ObjectNode.class);
	}
	
	
}
