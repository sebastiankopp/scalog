package de.sebastiankopp.scalog.server.elastic.boundary;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.getenv;
import static java.util.Arrays.stream;

@Component
@Scope("thread")
public class ConfigEnv {
	
	private final Map<ConfProp,String> env = new EnumMap<>(ConfProp.class);
	
	@PostConstruct
	void init() {
		stream(ConfProp.values())
				.forEachOrdered(prop -> env.put(prop, prop.getValue()));
	}
	
	public String getEnvVal(ConfProp propKey) {
		return env.get(propKey);
	}
	
	public static enum ConfProp {
		ELASTIC_HOST("ELASTIC_HOST", "localhost"),
		ELASTIC_PORT("ELASTIC_PORT", "9200");
		
		ConfProp(String varEnv, String defaultValue) {
			this.varEnv = varEnv;
			this.defaultValue = defaultValue;
		}
		
		private final String varEnv;
		private final String defaultValue;
		
		String getValue() {
			return Optional.ofNullable(getenv(varEnv))
					.filter(e -> !e.trim().isEmpty())
					.orElse(defaultValue);
		}
	}
	
}
