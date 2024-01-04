/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.log4j2;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;
import org.slf4j.*;

import java.util.Properties;
import java.util.concurrent.*;

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

    public KafkaOplogManager(LoggerContext context, final String name, final String topic, final Property[] properties) {
        super(context, name);
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
    public boolean releaseSub(final long timeout, final TimeUnit timeUnit) {
        if (timeout > 0) {
            closeProducer(timeout, timeUnit);
        } else {
            closeProducer(timeoutMillis, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    private void closeProducer(final long timeout, final TimeUnit timeUnit) {
        if (producer != null) {
            // This thread is a workaround for this Kafka issue: https://issues.apache.org/jira/browse/KAFKA-1660
            final Runnable task = () -> {
                if (producer != null) {
                    producer.close();
                }
            };
            // try {
            //     getLoggerContext().submitDaemon(task).get(timeout, timeUnit);
            // } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //     // ignore
            // }
        }
    }

    public void send(final Integer serverID, final String key, final byte[] msg) throws ExecutionException, InterruptedException, TimeoutException {
        if (producer != null) {
            producer.send(new ProducerRecord<>(this.topic + ObjectUtils.defaultIfNull(serverID, ""), key, msg), (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.error("", exception);
                }
            });
        }
    }

    public void startup() {
        producer = producerFactory.newKafkaProducer(config);
    }

    public String getTopic() {
        return topic;
    }

}
