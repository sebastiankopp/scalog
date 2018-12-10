package de.sebastiankopp.scalog.client.appender;

import org.apache.logging.log4j.ThreadContext;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ThreadContextLinkedThreadLocal<T> extends ThreadLocal<T> {
	private final String key;
	private final Function<? super T, String> toStringMapper;
	public ThreadContextLinkedThreadLocal(final ThreadContextKey key) {
		this(key, Object::toString);
	}
	
	public ThreadContextLinkedThreadLocal(final ThreadContextKey key, final Function<? super T, String> toStringMapper) {
		this.toStringMapper = requireNonNull(toStringMapper, "The toString mapper may not be null");
		this.key = requireNonNull(key, "The key may not be null").getVal();
	}
	
	@Override
	public void set(T t) {
		super.set(t);
		ThreadContext.put(key, toStringMapper.apply(t));
	}
	
	@Override
	public void remove() {
		super.remove();
		ThreadContext.remove(key);
	}
}
