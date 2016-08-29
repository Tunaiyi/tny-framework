package com.tny.game.suite.net.spring;

import com.tny.game.net.kafka.KafkaMessageBuilderFactory;
import com.tny.game.net.kafka.KafkaTicketSeller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Created by Kun Yang on 16/8/10.
 */
@Component
@Profile({GAME_KAFKA, SERVER_KAFKA})
public class SpringKafkaMessageBuilderFactory extends KafkaMessageBuilderFactory {

    @Autowired
    public SpringKafkaMessageBuilderFactory(KafkaTicketSeller ticketCreator) {
        super(ticketCreator);
    }

}
