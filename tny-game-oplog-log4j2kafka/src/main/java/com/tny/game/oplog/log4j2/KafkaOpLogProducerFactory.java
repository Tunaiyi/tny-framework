package com.tny.game.oplog.log4j2;

import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

/**
 * Kafka OpLog工厂
 * Created by Kun Yang on 16/5/24.
 */
public interface KafkaOpLogProducerFactory {

    Producer<String, byte[]> newKafkaProducer(Properties config);

}
