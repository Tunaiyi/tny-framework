package com.tny.game.actor;


public interface MessageQueue {

    void enqueue(ActorRef receiver, Envelope envelope);

    void enqueueFirst(ActorRef receiver, Envelope envelope);

    Envelope dequeue();

    int size();

    boolean hasMessages();

    void cleanUp(ActorRef owner, MessageQueue deadLetters);

}