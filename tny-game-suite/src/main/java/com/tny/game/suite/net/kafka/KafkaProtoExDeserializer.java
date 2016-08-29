package com.tny.game.suite.net.kafka;

import com.tny.game.net.kafka.KafkaMessage;
import com.tny.game.protoex.ProtoExReader;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by Kun Yang on 16/8/11.
 */
public class KafkaProtoExDeserializer implements Deserializer<KafkaMessage> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public KafkaMessage deserialize(String topic, byte[] data) {
        if (data.length <= 0)
            return null;
        ProtoExReader reader = new ProtoExReader(data);
        return (KafkaMessage) reader.readMessage();
    }

    @Override
    public void close() {
    }
}
