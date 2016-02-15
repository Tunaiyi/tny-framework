package com.tny.game.actor.local;

import com.tny.game.common.config.Config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * 邮差(消息分派者)配置类
 * Created by Kun Yang on 16/1/19.
 */
public class PostmanConfigurator {

    private String id;
    private Config config;
    private PostmanPrerequisites prerequisites;
    private ForkJoinPoolConfigurator forkJoinPoolConfigurator;

    private MessagePostman postman;

    PostmanConfigurator(String id, Config config, PostmanPrerequisites prerequisites) {
        this.id = id;
        this.config = config.child(id);
        this.prerequisites = prerequisites;
        this.forkJoinPoolConfigurator = new ForkJoinPoolConfigurator(config);
        this.postman = new DefaultPostman(this, id,
                config.getInt("throughput", -1),
                Duration.of(config.getLong("throughput-deadline-time", Long.MAX_VALUE),
                        config.getEnum("throughput-deadline-time-unit", ChronoUnit.MILLIS)),
                this.forkJoinPoolConfigurator,
                Duration.of(config.getLong("shutdown-timeout", 15 * 3600),
                        config.getEnum("shutdown-timeout-time-unit", ChronoUnit.MILLIS)));
    }

    public PostmanPrerequisites getPrerequisites() {
        return prerequisites;
    }

    public MessagePostman postman() {
        return postman;
    }

    public ForkJoinPoolConfigurator configureExecutor() {
        return forkJoinPoolConfigurator;
    }


}