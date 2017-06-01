package com.tny.game.actor.exception;

/**
 * 配置异常
 * Created by Kun Yang on 16/1/17.
 */
public class ConfigurationException extends ActorException {

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }

}
