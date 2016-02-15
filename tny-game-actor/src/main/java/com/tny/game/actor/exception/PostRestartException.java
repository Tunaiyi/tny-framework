package com.tny.game.actor.exception;

import com.tny.game.LogUtils;
import com.tny.game.actor.ActorRef;

/**
 * 重启后异常
 * Created by Kun Yang on 16/1/17.
 */
public class PostRestartException extends ActorInitializationException {


    private Throwable restartCause;

    public PostRestartException(ActorRef actorRef, Throwable cause, Throwable restartCause) {
        super(actorRef, LogUtils.format("exception post restart({})", restartCause.getClass()), cause);
    }

    public Throwable getRestartCause() {
        return restartCause;
    }


}
