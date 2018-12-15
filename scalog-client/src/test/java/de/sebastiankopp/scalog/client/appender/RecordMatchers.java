package de.sebastiankopp.scalog.client.appender;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.equalTo;

public class RecordMatchers {

	public static <T> Matcher<ConsumerRecord<? extends Object, T>> recordWithValue(Matcher<? super T> itemMatcher) {
		return new HasValueMatcher<T>(itemMatcher);
	}
	
	public static <T,U> Matcher<ConsumerRecord<? extends Object, T>> recordWithValue(
			Matcher<? super T> itemMatcher, Function<? super T, ? extends U> valueMapper) {
		return new HasValueMatcher<T>(itemMatcher);
	}
	
	public static <T> Matcher<ConsumerRecord<? extends Object, T>> recordWithValue(T value) {
		return new HasValueMatcher<T>(equalTo(value));
	}
	
	public static <T, U> Matcher<? super T> matchesConverted(
			Function<? super T, ? extends U> mapper, Matcher<? super U> innerMatcher) {
		return new ConvertingMatcher<T,U>(innerMatcher, mapper);
	}
	
	private static class ConvertingMatcher<A,B> extends TypeSafeDiagnosingMatcher<A> {
		
		private final Matcher<? super B> innerMatcher;
		private final Function<? super A, ? extends B> converter;
		
		private ConvertingMatcher(Matcher<? super B> innerMatcher, Function<? super A, ? extends B> converter) {
			this.innerMatcher = innerMatcher;
			this.converter = requireNonNull(converter);
		}
		
		@Override
		protected boolean matchesSafely(A item, Description mismatchDescription) {
			B convertedItem = converter.apply(item);
			return innerMatcher.matches(convertedItem);
		}
		
		@Override
		public void describeTo(Description description) {
			innerMatcher.describeTo(description);
		}
	}
	
	private static class HasValueMatcher<T> extends TypeSafeDiagnosingMatcher<ConsumerRecord<?, T>> {
		private final Matcher<? super T> coreMatcher;
		public HasValueMatcher(Matcher<? super T> matcher) {
			coreMatcher = matcher;
		}
		
		@Override
		public void describeTo(Description description) {
			coreMatcher.describeTo(description);
		}
		
		@Override
		protected boolean matchesSafely(ConsumerRecord<?, T> item, Description mismatchDescription) {
			return coreMatcher.matches(item.value());
		}
		
	}
	
}
