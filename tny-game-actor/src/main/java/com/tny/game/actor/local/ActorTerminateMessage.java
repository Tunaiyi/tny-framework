package com.tny.game.actor.local;

/**
 * Actor 终止消息
 * Created by Kun Yang on 16/4/26.
 */
public class ActorTerminateMessage implements ActorMessage {

    private static ActorTerminateMessage message;

    private ActorTerminateMessage() {
    }

    public static ActorTerminateMessage message() {
        return message;
    }

}
