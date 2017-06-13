package com.tny.game.actor.local;

import com.tny.game.worker.ForkJoinPoolCommandExecutor;

import java.util.concurrent.ForkJoinPool;

/**
 * Actor 执行器
 * Created by Kun Yang on 16/4/26.
 */
public class ActorCommandExecutor extends ForkJoinPoolCommandExecutor implements ActorWorker {

    public ActorCommandExecutor(String name) {
        this(name, ForkJoinPool.commonPool());
    }

    public ActorCommandExecutor(String name, ForkJoinPool pool) {
        super(name, pool);
    }

    @Override
    public boolean takeOver(LocalActor<?, ?> actor) {
        return register(actor.cell().getCommandBox());
    }

}
