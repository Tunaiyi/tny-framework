package com.tny.game.suite.net.kafka;

import com.tny.game.net.kafka.KafkaMessage;
import com.tny.game.net.kafka.KafkaRequest;
import com.tny.game.protoex.ProtoExWriter;
import com.tny.game.protoex.annotations.TypeEncode;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by Kun Yang on 16/8/11.
 */
public class KafkaProtoExSerializer implements Serializer<KafkaMessage> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, KafkaMessage data) {
        if (data == KafkaRequest.BLANK)
            return new byte[0];
        ProtoExWriter writer = new ProtoExWriter();
        writer.writeMessage(data, TypeEncode.EXPLICIT);
        return writer.toByteArray();
    }

    @Override
    public void close() {
    }
}
