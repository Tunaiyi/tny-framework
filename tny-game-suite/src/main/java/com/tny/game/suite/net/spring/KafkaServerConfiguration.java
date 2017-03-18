package com.tny.game.suite.net.spring;

import com.google.common.collect.Range;
import com.tny.game.net.dispatcher.DefaultSessionHolder;
import com.tny.game.net.common.session.BaseNetSessionHolder;
import com.tny.game.net.kafka.KafkaAppContext;
import com.tny.game.net.kafka.KafkaMessage;
import com.tny.game.net.kafka.KafkaServeAuthProvider;
import com.tny.game.net.kafka.KafkaServerInfo;
import com.tny.game.suite.core.GameInfo;
import com.tny.game.suite.net.kafka.KafkaProtoExDeserializer;
import com.tny.game.suite.net.kafka.KafkaProtoExSerializer;
import com.tny.game.suite.net.kafka.MD5KafkaTicketConductor;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tny.game.suite.SuiteProfiles.*;
import static com.tny.game.suite.utils.Configs.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@Profile({GAME_KAFKA, SERVER_KAFKA})
public class KafkaServerConfiguration {

    @Bean(name = "kafkaTicketConductor")
    public MD5KafkaTicketConductor kafkaTicketConductor() {
        return new MD5KafkaTicketConductor();
    }

    @Bean(name = "sessionHolder")
    @Profile({SERVER, SERVER_KAFKA})
    public BaseNetSessionHolder serverSessionHolder() {
        return new DefaultSessionHolder();
    }

    @Bean(name = "gameKafkaAppContext")
    @Profile({GAME_KAFKA})
    public KafkaAppContext gameKafkaAppContext() {
        String scopeType = Configs.SERVICE_CONFIG.getStr(Configs.SERVER_SCOPE);
        List<KafkaServerInfo> servers = GameInfo.getAllGamesInfo()
                .stream()
                .filter(GameInfo::isRegister)
                .map(info -> new KafkaServerInfo(info.getServerType().getName(), info.getServerID()))
                .collect(Collectors.toList());
        return new SpringKafkaAppContext(scopeType)
                .setServers(servers);
    }

    @Bean(name = "serverKafkaAppContext")
    @Profile({SERVER_KAFKA})
    public KafkaAppContext serverKafkaAppContext() {
        String serverType = Configs.SERVICE_CONFIG.getStr(Configs.SERVER_TYPE);
        int serverID = Configs.SERVICE_CONFIG.getInt(Configs.SERVER_ID);
        return new SpringKafkaAppContext(serverType)
                .setLocalServerID(serverID)
                .setServers(new KafkaServerInfo(serverType, serverID));
    }

    @Bean(name = "kafkaServeAuthProvider")
    public KafkaServeAuthProvider kafkaServeAuthProvider() {
        Set<Integer> includes = null;
        Set<Integer> excludes = null;
        Range<Integer> includesRange = null;
        Range<Integer> excludesRange = null;
        String inc = Configs.SUITE_CONFIG.getStr(Configs.SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_INC, null);
        if (inc != null)
            includes = Stream.of(StringUtils.split(inc, ",")).map(NumberUtils::toInt).collect(Collectors.toSet());
        String exc = Configs.SUITE_CONFIG.getStr(Configs.SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_EXC, null);
        if (exc != null)
            excludes = Stream.of(StringUtils.split(exc, ",")).map(NumberUtils::toInt).collect(Collectors.toSet());
        String incRg = Configs.SUITE_CONFIG.getStr(Configs.SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_INC_RG, null);
        if (incRg != null) {
            int[] range = Stream.of(StringUtils.split(incRg, "-")).mapToInt(NumberUtils::toInt).toArray();
            includesRange = Range.closed(range[0], range[1]);
        }
        String excRg = Configs.SUITE_CONFIG.getStr(Configs.SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_EXC_RG, null);
        if (excRg != null) {
            int[] range = Stream.of(StringUtils.split(excRg, "-")).mapToInt(NumberUtils::toInt).toArray();
            excludesRange = Range.closed(range[0], range[1]);
        }
        return new SpringKafkaServeAuthProvider(includes, excludes, includesRange, excludesRange);
    }

    @Bean(name = "kafkaProducer")
    public KafkaProducer<String, KafkaMessage> kafkaProducer() {
        String ips = CLUSTER_CONFIG.getStr(Configs.CLUSTER_KAFKA_PRODUCER_IP_LIST);
        return new KafkaProducer<>(getKafkaProperties(ips, "kafka.producer"));
    }

    @Bean(name = "kafkaConsumer")
    public KafkaConsumer<String, KafkaMessage> kafkaConsumer() {
        String ips = CLUSTER_CONFIG.getStr(Configs.CLUSTER_KAFKA_CONSUMER_IP_LIST);
        return new KafkaConsumer<>(getKafkaProperties(ips, "kafka.consumer"));
    }

    private Properties getKafkaProperties(String ips, String head) {
        Properties properties = new Properties();
        if (head.contains("consumer")) {
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtoExDeserializer.class.getName());
        } else if (head.contains("producer")) {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtoExSerializer.class.getName());
        }
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, ips);
        Configs.KAFKA_CONFIG.child(head).entrySet().forEach(entry -> {
            String key = entry.getKey().replace(head + ".", "");
            properties.put(key, entry.getValue());
        });
        return properties;
    }

}
