package de.sebastiankopp.scalog.client.appender;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static de.sebastiankopp.scalog.client.appender.JsonHelpers.asMap;
import static de.sebastiankopp.scalog.client.appender.RecordMatchers.matchesConverted;
import static de.sebastiankopp.scalog.client.appender.ThreadContextKey.CORRELATION_ID;
import static de.sebastiankopp.scalog.client.appender.ThreadContextKey.MESSAGE_ID;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.apache.logging.log4j.LogManager.getLogger;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.testng.Assert.assertEquals;

public class ITScalogKafkaLogging {
	private KafkaConsumer<String,String> consumer;
	
	private static final ThreadLocal<String> cid = new ThreadContextLinkedThreadLocal<>(CORRELATION_ID);
	private static final ThreadLocal<String> mid = new ThreadContextLinkedThreadLocal<>(MESSAGE_ID);
	private final Path logParentDir = Paths.get("target/generated-junk");
	@BeforeClass
	void init() throws Exception {
		Files.createDirectories(logParentDir);
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
		properties.put("key.deserializer", StringDeserializer.class.getName());
		properties.put("value.deserializer", StringDeserializer.class.getName());
		properties.put("group.id", "scalog-test-listener");
		consumer = new KafkaConsumer<>(properties);
		consumer.subscribe(asList("scalogMsgs"));
	}
	
	@Test
	public void test1() throws Exception {
		cid.set(randomUUID().toString());
		mid.set(randomUUID().toString());
		ScopeKeeper.enterScope("SomeTestScope");
		assertEquals(ThreadContext.get("scope"), "SomeTestScope");
		final Logger logger = getLogger(ITScalogKafkaLogging.class);
		final String message = "Some Kafka message";
		logger.info(message);
		ScopeKeeper.enterScope("SomeInnerTestScope");
		logger.error("A non-existent error occured ...", new Exception());
		assertEquals(ThreadContext.get("scope"), "SomeInnerTestScope");
		ScopeKeeper.exitScope();
		TimeUnit.SECONDS.sleep(1);
		final ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
		consumer.commitAsync();
		final int recordsCount = records.count();
		assertEquals(ThreadContext.get("scope"), "SomeTestScope");
		ScopeKeeper.exitScope();
		records.forEach(record -> System.out.printf(
				"Consumer Record:(Rec Key: %s, Rec Val: %s, Partition: %d, Offset: %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset()));
		assertThat(records, Matchers.<ConsumerRecord<String, String>>hasItem(
				matchesConverted(
						rec -> asMap(rec.value()),
						hasEntry("correlationId", cid.get())
				)));
		final ConsumerRecord<String, String> rec1 = records.iterator().next();
		cid.remove();
		mid.remove();
	}
}
