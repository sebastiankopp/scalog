package de.sebastiankopp.scalog.server.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScalogServerApp {
	public static void main(String... args) {
		SpringApplication.run(ScalogServerApp.class, args);
	}
	
	@Bean
	public JacksonJsonProvider jsonProvider() {
		final JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
		jsonProvider.setMapper(objectMapper());
		return jsonProvider;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
