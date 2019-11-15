package com.tny.game.actor.local;

import com.tny.game.common.worker.*;

import java.util.concurrent.*;

/**
 * Actor 执行器
 * Created by Kun Yang on 16/4/26.
 */
public class ActorCommandExecutor extends DefaultCommandExecutor implements ActorWorker {

    public ActorCommandExecutor(String name) {
        this(name, ForkJoinPool.commonPool());
    }

    public ActorCommandExecutor(String name, ExecutorService executor) {
        super(name, executor);
    }

    @Override
    public boolean takeOver(LocalActor<?, ?> actor) {
        return register(actor.cell().getCommandBox());
    }

}
