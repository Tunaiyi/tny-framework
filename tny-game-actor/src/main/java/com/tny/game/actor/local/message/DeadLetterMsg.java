package com.tny.game.actor.local.message;

import com.tny.game.actor.ActorRef;

/**
 * 死亡消息的消息
 * Created by Kun Yang on 16/1/19.
 */
public class DeadLetterMsg {

    private ActorRef sender;

    private Object message;

    private ActorRef recipient;

    public DeadLetterMsg(ActorRef sender, Object message, ActorRef recipient) {
        this.sender = sender;
        this.message = message;
        this.recipient = recipient;
    }

    public ActorRef getSender() {
        return sender;
    }

    public Object getMessage() {
        return message;
    }

    public ActorRef getRecipient() {
        return recipient;
    }
}
