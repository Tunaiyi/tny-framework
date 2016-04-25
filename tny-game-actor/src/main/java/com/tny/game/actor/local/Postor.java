package com.tny.game.actor.local;

import com.tny.game.actor.ActorRef;
import com.tny.game.actor.Answer;
import com.tny.game.actor.Envelope;
import com.tny.game.actor.SystemMessage;
import com.tny.game.actor.system.RecreateSysMsg;
import com.tny.game.actor.system.SuspendSysMsg;
import com.tny.game.actor.system.TerminateSysMsg;

interface Postor {

    MessagePostman getPostman();

    ActorCell actorCell();

    Postbox swapPostbox(Postbox newPostbox);

    Postbox getPostbox();

    default void start() {
        this.getPostman().attach(actorCell());
    }

    default void stop() {
        getPostman().systemPost(actorCell(), TerminateSysMsg.message());
    }

    default void suspend() {
        getPostman().systemPost(actorCell(), SuspendSysMsg.message());
    }

    default void resume(Throwable cause) {
        getPostman().systemPost(actorCell(), RecreateSysMsg.message(cause));
    }

    default void restart(Throwable cause) {
        getPostman().systemPost(actorCell(), RecreateSysMsg.message(cause));
    }

    default boolean isTerminated() {
        return getPostbox().isClosed();
    }

    default boolean hasMessages() {
        return getPostbox().hasMessages();
    }

    default int messageSize() {
        return getPostbox().messageSize();
    }

    default void sendSystemMessage(SystemMessage message) {
        getPostman().systemPost(actorCell(), message);
    }

    default <V> Answer<V> sendMessage(Object message, ActorRef sender, Answer<V> answer) {
        return sendMessage(Envelope.of(message, sender, answer));
    }

    default <V> Answer<V> sendMessage(Envelope envelope) {
        getPostman().post(actorCell(), envelope);
        return envelope.getAnswer();
    }

}
