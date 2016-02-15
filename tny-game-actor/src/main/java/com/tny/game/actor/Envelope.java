package com.tny.game.actor;

import com.tny.game.actor.local.ActorUtils;

@SuppressWarnings("unchecked")
public class Envelope {

    private final Object message;

    private final ActorRef sender;

    private final Answer<?> answer;

    private Envelope(Object message, ActorRef sender, Answer<?> answer) {
        super();
        this.message = message;
        this.sender = sender;
        this.answer = answer;
    }

    private Envelope(Object message, ActorRef sender, ActorSystem system) {
        super();
        this.message = message;
        this.sender = sender != ActorUtils.noSender() ? sender : system.getDeadLetters();
        this.answer = null;
    }

    public Object getMessage() {
        return message;
    }

    public <M> M message() {
        return (M) message;
    }


    public <V> Answer<V> getAnswer() {
        return (Answer<V>) answer;
    }

    public ActorRef getSender() {
        return sender;
    }


    public static Envelope of(Object message, ActorRef sender, Answer<?> answer) {
        return new Envelope(message, ActorUtils.orNoSender(sender), answer);
    }

    public static Envelope of(Object message, ActorRef sender, ActorSystem system) {
        return new Envelope(message, sender, system);
    }

}
