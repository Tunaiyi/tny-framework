package com.tny.game.actor.stage;

/**
 * 当fragment成功时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenSuccessStage<R> extends DefaultStage<R> {

    public ThenSuccessStage(CommonStage head, TaskFragment<?, R> fragment) {
        super(head, fragment);
    }

    @Override
    public boolean checkCanRun(TaskFragment<?, ?> prev) {
        return super.checkCanRun(prev);
    }

}
