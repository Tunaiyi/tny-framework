package com.tny.game.actor.local;

import com.tny.game.actor.Actor;

import java.util.concurrent.Executor;

/**
 * Created by Kun Yang on 16/4/30.
 */
public abstract class LocalActor<ID, M> implements Actor<ID, M>, ActorTheatre, Executor {


    /**
     * 脱管当前worker
     */
    protected abstract boolean detach();

    /**
     * @return 获取Actor单元
     */
    protected abstract ActorCell cell();

    /**
     * 终止
     */
    protected abstract void terminate();

}
