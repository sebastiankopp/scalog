package de.sebastiankopp.scalog.server.elastic.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
public class ElasticWriter extends ElasticClient {
	
	@Autowired
	public ElasticWriter(ConfigEnv env, ObjectMapper objectMapper) {
		super(env, objectMapper);
	}
	
	public UUID writeToIndex(final ObjectNode logMsgObject) {
		return writeToIndex(logMsgObject, 5);
	}
	
	private UUID writeToIndex(final ObjectNode logMsgObject, int retries) {
		final UUID scalogLogMsgId = randomUUID();
		final Response response = target
				.path(scalogLogMsgId.toString())
				.queryParam("op_type", "create")
				.request()
				.buildPut(entity(logMsgObject, APPLICATION_JSON))
				.invoke();
		return handleResponseError(response, () -> writeToIndex(logMsgObject, retries - 1), scalogLogMsgId);
	}
	
	public void writeToIndex(final String logMsgAsJson) {
		try {
			writeToIndex((ObjectNode) objectMapper.reader(ObjectNode.class).readValue(logMsgAsJson));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@KafkaListener(topics = "scalogMsgs", groupId = "scalog-aggregation")
	public void processMsg(String content) {
		writeToIndex(content);
	}
	
}
