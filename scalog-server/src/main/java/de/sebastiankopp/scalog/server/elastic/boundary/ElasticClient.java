package de.sebastiankopp.scalog.server.elastic.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Callable;

import static de.sebastiankopp.scalog.server.elastic.boundary.ConfigEnv.ConfProp.ELASTIC_HOST;
import static de.sebastiankopp.scalog.server.elastic.boundary.ConfigEnv.ConfProp.ELASTIC_PORT;
import static java.lang.Integer.parseInt;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;

public abstract class ElasticClient {
	protected final WebTarget target;
	protected final ObjectMapper objectMapper;
	
	public ElasticClient(ConfigEnv env, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		final URI baseUri = UriBuilder.fromPath("scalog/_doc")
				.host(env.getEnvVal(ELASTIC_HOST))
				.port(parseInt(env.getEnvVal(ELASTIC_PORT)))
				.build();
		target = ClientBuilder.newClient()
				.target(baseUri);
	}
	
	
	protected void handleResponse(Response response) {
		handleResponse(response, null, null, 0);
	}
	
	protected <T> T handleResponse(Response response, Callable<T> redoAction, T successRetVal, int retries) {
		final Response.Status.Family statusFamily = response.getStatusInfo().getFamily();
		if (statusFamily == CLIENT_ERROR || statusFamily == SERVER_ERROR) {
			if (response.getStatus() == CONFLICT.getStatusCode() && redoAction != null && retries > 0) {
				try {
					return redoAction.call();
				} catch (Exception e) {
					throw new IllegalStateException("Retry failed", e);
				}
			}
			throw new IllegalStateException("Error during trying to write new log message to elastic");
		}
		return successRetVal;
	}
}
