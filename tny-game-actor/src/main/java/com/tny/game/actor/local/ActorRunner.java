package com.tny.game.actor.local;

/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public interface ActorRunner<M> extends ActorHandler<M> {

    @Override
    default Object handler(ActorMail<M> mail) {
        this.accept(mail);
        return null;
    }

    void accept(ActorMail<M> message);

}