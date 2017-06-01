package com.tny.game.actor.local;


/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public interface ActorLifeCycle {

    default void preBindWorker() {
    }

    default void postBindWorker() {
    }

    default void preUnbindWorker() {
    }

    default void postUnbindWorker() {
    }

    default void preTerminate() {
    }

    default void postTerminate() {
    }

    default void preHandle(ActorCommand<?> command) {
    }

    default void postHandle(ActorCommand<?> command) {
    }


    default void postSucc(Object result) {
    }


    default void postFail(Throwable cause) {
    }

}