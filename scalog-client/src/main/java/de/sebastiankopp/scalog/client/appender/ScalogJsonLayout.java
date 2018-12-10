package de.sebastiankopp.scalog.client.appender;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.apache.logging.log4j.core.util.KeyValuePair;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

@Plugin(name = "ScalogJsonLayout", category = "Core", elementType = Layout.ELEMENT_TYPE, printObject = true)
public final class ScalogJsonLayout implements Layout<String> {
	private final JsonLayout forwardedLayout;
	
	public ScalogJsonLayout(Configuration configuration, boolean stacktraceAsString,
	                        final KeyValuePair[] userDefinedExtraFields) {
		forwardedLayout = JsonLayout.newBuilder()
				.setConfiguration(configuration)
				.setCompact(true)
				.setStacktraceAsString(stacktraceAsString)
				.setAdditionalFields(getAdditionalFields(userDefinedExtraFields))
				.setLocationInfo(true)
				.build();
	}
	
	static KeyValuePair[] getAdditionalFields(KeyValuePair[] userDefinedExtraFields) {
		final List<KeyValuePair> kvPairs = stream(ThreadContextKey.values())
				.map(ScalogJsonLayout::convertToKVPair)
				.collect(toCollection(ArrayList::new));
		kvPairs.addAll(asList(
			asKvPair("hostId", getHostName()),
			asKvPair("scope", "${ctx:scope}"),
			asKvPair("systemId", "${sys:app.system.id:-unknown-system-id}"),
			asKvPair("javaVersion", "${java:version}"),
			asKvPair("javaRuntime", "${java:runtime}"),
			asKvPair("jvm", "${java:vm}"),
			asKvPair("operatingSystem", "${java:os}"),
			asKvPair("locale", "${java:locale}"),
			asKvPair("hardwareInfo", "${java:hw}")
		));
		final Set<String> usedKeys = kvPairs.stream().map(KeyValuePair::getKey).collect(toSet());
		stream(userDefinedExtraFields)
				.filter(e -> e != null && e.getKey() != null && !usedKeys.contains(e.getKey()))
				.forEachOrdered(kvPairs::add);
		final int size = kvPairs.size();
		return kvPairs.toArray(new KeyValuePair[size]);
	}
	
	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "UNKNOWN HOST!";
		}
	}
	
	@Deprecated
	public static ScalogJsonLayout createLayout(final Configuration configuration, final boolean stacktraceAsString,
	                                            final KeyValuePair[] userDefinedExtraField) {
		return new ScalogJsonLayout(configuration, stacktraceAsString, userDefinedExtraField);
	}
	
	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder implements org.apache.logging.log4j.core.util.Builder<ScalogJsonLayout> {
		
		@PluginConfiguration
		private Configuration configuration;
		
		@PluginAttribute("stacktraceAsString")
		private boolean stacktraceAsString = true;
		
	//	@PluginElement("AdditionalData")
		private KeyValuePair[] additionalKeyValuePairs = new KeyValuePair[0];
		
		public Configuration getConfiguration() {
			return configuration;
		}
		
		public Builder setConfiguration(Configuration configuration) {
			this.configuration = configuration;
			return this;
		}
		
		public boolean isStacktraceAsString() {
			return stacktraceAsString;
		}
		
		public Builder setStacktraceAsString(boolean stacktraceAsString) {
			this.stacktraceAsString = stacktraceAsString;
			return this;
		}
		
		@Override
		public ScalogJsonLayout build() {
			return new ScalogJsonLayout(configuration, stacktraceAsString, additionalKeyValuePairs);
		}
	}
	
	private static KeyValuePair convertToKVPair(ThreadContextKey _key) {
		return new KeyValuePair.Builder()
				.setKey(_key.getVal())
				.setValue("${ctx:" + _key.getVal() + "}").build();
	}
	
	private static KeyValuePair asKvPair(String key, String value) {
		return new KeyValuePair.Builder()
				.setKey(key)
				.setValue(value)
				.build();
	}
	
	@Override
	public byte[] getFooter() {
		return forwardedLayout.getFooter();
	}
	
	@Override
	public byte[] getHeader() {
		return forwardedLayout.getHeader();
	}
	
	@Override
	public byte[] toByteArray(LogEvent event) {
		return forwardedLayout.toByteArray(event);
	}
	
	@Override
	public String toSerializable(LogEvent event) {
		return forwardedLayout.toSerializable(event);
	}
	
	@Override
	public String getContentType() {
		return forwardedLayout.getContentType();
	}
	
	@Override
	public Map<String, String> getContentFormat() {
		return forwardedLayout.getContentFormat();
	}
	
	@Override
	public void encode(LogEvent source, ByteBufferDestination destination) {
		forwardedLayout.encode(source, destination);
	}
}
