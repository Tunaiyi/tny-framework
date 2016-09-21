package com.tny.game.net.kafka;

import com.tny.game.net.LoginCertificate;

/**
 * Created by Kun Yang on 16/8/9.
 */
public interface Topics {

    String RESPONSE_PATH = "RPS";
    String REQUEST_PATH = "RQS";

    String TOPIC_SEPARATOR = "_";
    String KEY_SEPARATOR = ":";

    static String responseTopic(LoginCertificate certificate) {
        return certificate.getUserGroup() + TOPIC_SEPARATOR + certificate.getUserID() + TOPIC_SEPARATOR + RESPONSE_PATH;
    }

    static String responseTopic(KafkaServerInfo kafkaServerInfo) {
        return kafkaServerInfo.getServerType() + TOPIC_SEPARATOR + kafkaServerInfo.getID() + TOPIC_SEPARATOR + RESPONSE_PATH;
    }

    static String requestTopic(KafkaServerInfo kafkaServerInfo) {
        return kafkaServerInfo.getServerType() + TOPIC_SEPARATOR + kafkaServerInfo.getID() + TOPIC_SEPARATOR + REQUEST_PATH;
    }

    static String messageKey(String topic, String loginKey, long id) {
        return topic + KEY_SEPARATOR + loginKey + KEY_SEPARATOR + id;
    }

}
