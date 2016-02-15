package com.tny.game.actor.system;

import com.tny.game.LogUtils;
import com.tny.game.actor.ActorRef;
import com.tny.game.actor.local.ActorUtils;
import com.tny.game.actor.exception.ActorException;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Kun Yang on 16/1/20.
 */
public interface ActorScheduler {

    TaskExecutor DEFAULT_EXECUTOR = new TaskExecutor() {

        @Override
        public void execute(Runnable runnable) {
            runnable.run();
        }

    };

    default <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, ActorRef receiver, Object message, ActorRef sender, TaskExecutor executor) {
        return schedule(initialDelay, interval, () -> {
            ActorRef sendActor = sender == null ? ActorUtils.noSender() : sender;
            receiver.tell(message, sendActor);
            if (receiver.isTerminated())
                throw new ActorException(LogUtils.format("schedule 执行接受actor消息是receiver {} 已经关闭", receiver.getPath()));
        }, executor);
    }

    default <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, ActorRef receiver, Object message, ActorRef sender) {
        return schedule(initialDelay, interval, receiver, message, sender, DEFAULT_EXECUTOR);
    }

    default <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, ActorRef actorRef, Object message, TaskExecutor executor) {
        return this.schedule(initialDelay, interval, actorRef, message, ActorUtils.noSender(), executor);
    }

    default <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, ActorRef actorRef, Object message) {
        return this.schedule(initialDelay, interval, actorRef, message, ActorUtils.noSender());
    }

    <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, Runnable runnable, TaskExecutor executor);

    default <M> ScheduledFuture<M> schedule(Duration initialDelay, Duration interval, Runnable runnable) {
        return schedule(initialDelay, interval, runnable, DEFAULT_EXECUTOR);
    }


    default <M> ScheduledFuture<M> scheduleOnce(Duration delay, ActorRef receiver, Object message, ActorRef sender, TaskExecutor executor) {
        return scheduleOnce(delay, () -> {
            ActorRef sendActor = sender == null ? ActorUtils.noSender() : sender;
            receiver.tell(message, sendActor);
        }, executor);
    }


    default <M> ScheduledFuture<M> scheduleOnce(Duration delay, ActorRef receiver, Object message, ActorRef sender) {
        return scheduleOnce(delay, receiver, message, sender, DEFAULT_EXECUTOR);
    }

    default <M> ScheduledFuture<M> scheduleOnce(Duration delay, ActorRef receiver, Object message, TaskExecutor executor) {
        return scheduleOnce(delay, receiver, message, ActorUtils.noSender(), executor);
    }

    default <M> ScheduledFuture<M> scheduleOnce(Duration delay, ActorRef receiver, Object message) {
        return scheduleOnce(delay, receiver, message, ActorUtils.noSender());
    }

    <M> ScheduledFuture<M> scheduleOnce(Duration delay, Runnable runnable, TaskExecutor executor);

    default <M> ScheduledFuture<M> scheduleOnce(Duration delay, Runnable runnable) {
        return scheduleOnce(delay, runnable, DEFAULT_EXECUTOR);
    }

}
