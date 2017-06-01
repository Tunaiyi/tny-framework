package com.tny.game.actor.local;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deliver对象,负责处理消息.
 *
 * @author KGTny
 */
public class ProxyActorLifeCycle implements ActorLifeCycle {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProxyActorLifeCycle.class);

    private ActorLifeCycle lifeCycle;

    public ProxyActorLifeCycle(ActorLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    @Override
    public void preBindWorker() {
        try {
            lifeCycle.preBindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postBindWorker() {
        try {
            lifeCycle.postBindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preUnbindWorker() {
        try {
            lifeCycle.preUnbindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postUnbindWorker() {
        try {
            lifeCycle.postUnbindWorker();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preTerminate() {
        try {
            lifeCycle.preTerminate();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postTerminate() {
        try {
            lifeCycle.postTerminate();
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void preHandle(ActorCommand<?> command) {
        try {
            lifeCycle.preHandle(command);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postHandle(ActorCommand<?> command) {
        try {
            lifeCycle.postHandle(command);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postSucc(Object result) {
        try {
            lifeCycle.postSucc(result);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postFail(Throwable cause) {
        try {
            lifeCycle.postFail(cause);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

}