package com.tny.game.actor.exception;

import com.tny.game.LogUtils;
import com.tny.game.actor.ActorRef;

import java.util.Optional;

/**
 * 重启前异常
 * Created by Kun Yang on 16/1/17.
 */
public class PreRestartException extends ActorInitializationException {

    private Optional<Object> sendMessage;

    private Throwable restartCause;

    public PreRestartException(ActorRef actorRef, Throwable cause, Throwable restartCause, Optional<Object> sendMessage) {
        super(actorRef, LogUtils.format("exception in preRestart({} , {})", restartCause, sendMessage.orElse("None")), cause);
    }

    public Optional<Object> getSendMessage() {
        return sendMessage;
    }

    public Throwable getRestartCause() {
        return restartCause;
    }


}
