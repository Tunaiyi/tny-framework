package com.tny.game.suite.net.spring;

import com.tny.game.net.initer.InitLevel;
import com.tny.game.net.initer.PostIniter;
import com.tny.game.net.initer.ServerPostStart;
import com.tny.game.net.kafka.KafkaAppContext;
import com.tny.game.net.kafka.KafkaMessage;
import com.tny.game.net.kafka.KafkaNetBootstrap;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/8/10.
 */
@Component
@Profile({GAME_KAFKA, SERVER_KAFKA})
public class SpringKafkaNetBootstrap extends KafkaNetBootstrap implements ServerPostStart {

    @Autowired
    public SpringKafkaNetBootstrap(KafkaAppContext context, KafkaProducer<String, KafkaMessage> producer, KafkaConsumer<String, KafkaMessage> consumer) {
        super(context, producer, consumer);
    }

    @Override
    public void initialize() throws Exception {
        this.start();
    }

    @Override
    public PostIniter getIniter() {
        return PostIniter.initer(SpringKafkaNetBootstrap.class, InitLevel.LEVEL_1);
    }
}
