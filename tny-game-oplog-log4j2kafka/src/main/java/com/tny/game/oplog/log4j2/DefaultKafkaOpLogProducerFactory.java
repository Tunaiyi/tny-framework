package com.tny.game.oplog.log4j2;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

/**
 * 默认Kafka OpLog工厂
 * Created by Kun Yang on 16/5/24.
 */
public class DefaultKafkaOpLogProducerFactory implements KafkaOpLogProducerFactory {

    @Override
    public Producer<String, byte[]> newKafkaProducer(Properties config) {
        return new KafkaProducer<>(config);
    }

}
