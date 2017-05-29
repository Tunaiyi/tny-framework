package com.tny.game.actor.stage;

/**
 * 当fragment完成(无论成功或失败)时阶段完成
 * Created by Kun Yang on 16/1/22.
 */
class ThenDoneStage<R> extends DefaultStage<R> {

    public ThenDoneStage(CommonStage head, TaskFragment<?, R> fragment) {
        super(head, fragment);
    }

    @Override
    public boolean checkCanRun(TaskFragment<?, ?> prev) {
        return prev.isDone();
    }

    @Override
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskStageKey key) {
        while (true) {
            if (fragment.isDone()) {
                if (next != null && next.isCanRun(fragment)) {
                    if (next.isNoneParam())
                        next.run(fragment, null, fragment.getCause(), key);
                    else
                        next.run(fragment, fragment.getResult(), fragment.getCause(), key);
                }
                return;
            } else {
                fragment.execute(returnVal, e);
                if (fragment.isSuccess()) {
                    prev.obtrudeResult(null);
                } else if (!fragment.isDone()) {
                    return;
                }
            }
        }
    }

}
