package com.tny.game.actor.stage;


import com.tny.game.actor.stage.exception.*;
import com.tny.game.common.utils.*;

import java.util.concurrent.Executor;

/**
 * 基础阶段抽象类
 * Created by Kun Yang on 16/1/22.
 */
@SuppressWarnings("unchecked")
public abstract class BaseStage<R> implements InnerStage<R> {

    private Object name;

    protected InnerStage next;

    private Executor executor;

    protected BaseStage(Object name) {
        this.name = name;
    }

    @Override
    public InnerStage getNext() {
        return next;
    }

    @Override
    public void setNext(InnerStage next) {
        this.next = ObjectAide.as(next);
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public void setSwitchExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Executor getSwitchExecutor() {
        return this.executor;
    }

    @Override
    public void interrupt() {
        Fragment<Object, ?> fragment = (Fragment<Object, ?>) getFragment();
        if (!fragment.isDone()) {
            fragment.fail(new StageInterruptedException("stage was interrupted"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Object returnVal, Throwable e) {
        Fragment<Object, ?> fragment = (Fragment<Object, ?>) getFragment();
        if (!fragment.isDone()) {
            fragment.execute(returnVal, e);
        }
    }

}
