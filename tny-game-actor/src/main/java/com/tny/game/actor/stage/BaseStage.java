package com.tny.game.actor.stage;


import com.tny.game.actor.stage.exception.StageInterruptedException;
import com.tny.game.common.reflect.ObjectUtils;

/**
 * 基础阶段抽象类
 * Created by Kun Yang on 16/1/22.
 */
@SuppressWarnings("unchecked")
public abstract class BaseStage<R> implements InnerStage<R> {

    private Object name;

    protected InnerStage next;

    protected BaseStage(Object name) {
        this.name = name;
    }

    @Override
    public InnerStage getNext() {
        return next;
    }

    @Override
    public void setNext(InnerStage next) {
        this.next = ObjectUtils.as(next);
    }

    @Override
    public Object getName() {
        return name;
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
