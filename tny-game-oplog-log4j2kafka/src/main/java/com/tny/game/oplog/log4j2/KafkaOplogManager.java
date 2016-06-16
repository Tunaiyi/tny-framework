package com.tny.game.oplog.log4j2;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.Log4jThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class KafkaOplogManager extends AbstractManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaOplogManager.class);

    public static final String DEFAULT_TIMEOUT_MILLIS = "30000";

    /**
     * package-private access for testing.
     */
    private static KafkaOpLogProducerFactory producerFactory = new DefaultKafkaOpLogProducerFactory();

    private final Properties config = new Properties();
    private Producer<String, byte[]> producer = null;
    private final int timeoutMillis;

    private final String topic;

    public KafkaOplogManager(final String name, final String topic, final Property[] properties) {
        super(name);
        this.topic = topic;
        config.setProperty("key.serializer", StringSerializer.class.getName());
        config.setProperty("value.serializer", ByteArraySerializer.class.getName());
        config.setProperty("batch.size", "0");
        for (final Property property : properties) {
            config.setProperty(property.getName(), property.getValue());
        }
        this.timeoutMillis = Integer.parseInt(config.getProperty("timeout.ms", DEFAULT_TIMEOUT_MILLIS));
    }

    @Override
    public void releaseSub() {
        if (producer != null) {
            // This thread is a workaround for this Kafka issue: https://issues.apache.org/jira/browse/KAFKA-1660
            final Thread closeThread = new Log4jThread(new Runnable() {
                @Override
                public void run() {
                    producer.close();
                }
            });
            closeThread.setName("KafkaManager-CloseThread");
            closeThread.setDaemon(true); // avoid blocking JVM shutdown
            closeThread.start();
            try {
                closeThread.join(timeoutMillis);
            } catch (final InterruptedException ignore) {
                // ignore
            }
        }
    }

    public void send(final Integer serverID, final String key, final byte[] msg) throws ExecutionException, InterruptedException, TimeoutException {
        if (producer != null) {
            producer.send(new ProducerRecord<>(this.topic + ObjectUtils.defaultIfNull(serverID, ""), key, msg), (metadata, exception) -> {
                if (exception != null)
                    LOGGER.error("", exception);
            });
        }
    }

    public void startup() {
        producer = producerFactory.newKafkaProducer(config);
    }

}
