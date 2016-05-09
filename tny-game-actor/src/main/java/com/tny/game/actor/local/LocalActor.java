package com.tny.game.actor.local;

import com.tny.game.actor.Actor;
import com.tny.game.actor.ActorInvoker;

/**
 * Created by Kun Yang on 16/4/30.
 */
public abstract class LocalActor<ID, M> implements Actor<ID, M>, ActorInvoker<ID, Actor<ID, M>>, ActorTheatre {


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
