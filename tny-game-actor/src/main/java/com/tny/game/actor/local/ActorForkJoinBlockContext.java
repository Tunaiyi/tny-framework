package com.tny.game.actor.local;


import java.time.Duration;
import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 16/1/21.
 */
public class ActorForkJoinBlockContext implements BlockContext {

    private Duration timeout;

    @Override
    public <T> T onBlock(Future<T> future) {
//        Thread thread = Thread.currentThread();
//        if (thread instanceof ForkJoinWorkerThread) {
//            ForkJoinWorkerThread workerThread = (ForkJoinWorkerThread) thread;
//            ForkJoinTask.
//        } else {
////            future.complete();
//        }
        return null;
    }

}
