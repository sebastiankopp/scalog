package de.sebastiankopp.scalog.server.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.SimpleThreadScope;

@SpringBootApplication
public class ScalogServerApp {
	public static void main(String... args) {
		SpringApplication.run(ScalogServerApp.class, args);
	}
	
	@Bean
	public JacksonJsonProvider jsonProvider(ObjectMapper objectMapper) {
		final JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
		jsonProvider.setMapper(objectMapper);
		return jsonProvider;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	public CustomScopeConfigurer scopeConfigurer() {
		final CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
		scopeConfigurer.addScope("thread", new SimpleThreadScope());
		return scopeConfigurer;
	}
}
