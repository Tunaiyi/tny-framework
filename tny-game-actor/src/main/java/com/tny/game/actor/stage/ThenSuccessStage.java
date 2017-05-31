package com.tny.game.actor.stage;

/**
 * 当fragment成功时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenSuccessStage<R> extends DefaultStage<R> {

    public ThenSuccessStage(Object name, Fragment<?, R> fragment) {
        super((name), fragment);
    }

    @Override
    public boolean doCheck(Fragment<?, ?> prev) {
        return super.doCheck(prev);
    }

}
