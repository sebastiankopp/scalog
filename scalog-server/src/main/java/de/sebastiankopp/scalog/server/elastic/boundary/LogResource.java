package de.sebastiankopp.scalog.server.elastic.boundary;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("logMsgs")
@Component
public class LogResource {
	
	@Autowired
	ElasticWriter writer;
	
	@Autowired
	ElasticReader elasticReader;
	
	@POST
	@Consumes(APPLICATION_JSON)
	public Response addMessage(ObjectNode contentBody) {
		final UUID logMsgId = writer.writeToIndex(contentBody);
		return Response.created(UriBuilder.fromResource(LogResource.class)
				.build(logMsgId.toString()))
				.build();
	}
	
	@GET
	@Path("{id}")
	@Produces(APPLICATION_JSON)
	public ObjectNode getLogMsgById(@PathParam("id") String id) {
		return elasticReader.getLogMsgById(id);
	}
	
}
