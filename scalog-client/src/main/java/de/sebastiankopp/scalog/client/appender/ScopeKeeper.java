package de.sebastiankopp.scalog.client.appender;

import org.apache.logging.log4j.ThreadContext;

import java.util.Stack;

import static java.util.Objects.requireNonNull;

public class ScopeKeeper {
	private static final ScopeKeepingThreadLocal thrLoc = new ScopeKeepingThreadLocal();
	private static final String KEY_SCOPE = "scope";
	
	public static void enterScope(String scope) {
		thrLoc.setCurrentValue(scope);
	}
	
	public static String getCurrentScope() {
		return thrLoc.getCurrentValue();
	}
	
	public static String exitScope() {
		return thrLoc.removeCurrentValue();
	}
	
	private static final class ScopeKeepingThreadLocal extends ThreadLocal<Stack<String>> {
		@Override
		protected Stack<String> initialValue() {
			return new Stack<>();
		}
		
		String getCurrentValue() {
			final Stack<String> stack = get();
			if (stack.isEmpty())
				return null;
			else
				return stack.peek();
			
		}
		
		void setCurrentValue(String value) {
			requireNonNull(value, "The value may not be null");
			get().push(value);
			ThreadContext.put(KEY_SCOPE, value);
		}
		
		String removeCurrentValue() {
			final Stack<String> stack = get();
			if (stack.isEmpty()) {
				return null;
			} else {
				final String rc = stack.pop();
				if (!stack.isEmpty())
					ThreadContext.put(KEY_SCOPE, stack.peek());
				else
					ThreadContext.remove(KEY_SCOPE);
				return rc;
			}
		}
	}
	

}
