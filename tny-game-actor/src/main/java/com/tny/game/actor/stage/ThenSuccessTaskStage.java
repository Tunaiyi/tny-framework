package com.tny.game.actor.stage;

/**
 * 当fragment成功时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenSuccessTaskStage<R> extends DefaultTaskStage<R> {

    public ThenSuccessTaskStage(CommonTaskStage head, TaskFragment<?, R> fragment) {
        super(head, fragment);
    }

    @Override
    public boolean checkCanRun(TaskFragment<?, ?> prev) {
        return super.checkCanRun(prev);
    }

}
