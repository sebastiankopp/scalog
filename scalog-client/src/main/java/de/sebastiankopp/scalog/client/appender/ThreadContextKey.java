package de.sebastiankopp.scalog.client.appender;

public enum ThreadContextKey {
	CORRELATION_ID("correlationId"),
	MESSAGE_ID("messageId");
	
	private final String val;
	ThreadContextKey(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
}
