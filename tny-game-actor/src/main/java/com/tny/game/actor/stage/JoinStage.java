package com.tny.game.actor.stage;


import com.tny.game.actor.stage.Stages.JoinFragment;

/**
 * Created by Kun Yang on 16/1/22.
 */
class JoinStage<TS extends Stage> extends BaseStage<Object> {

    protected JoinFragment<?, Object, ? extends Stage> targetFragment;

    @SuppressWarnings("unchecked")
    public JoinStage(CommonStage head, JoinFragment<?, ?, TS> fragment) {
        super(head);
        this.targetFragment = (JoinFragment<?, Object, Stage>) fragment;
    }

    @Override
    public TaskFragment<?, ?> getTaskFragment() {
        return this.targetFragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskStageKey key) {
        while (true) {
            if (this.targetFragment.isDone()) {
                if (next != null && next.isCanRun(targetFragment)) {
                    if (next.isNoneParam())
                        next.run(targetFragment, null, targetFragment.getCause(), key);
                    else
                        next.run(targetFragment, returnVal, targetFragment.getCause(), key);
                }
                return;
            } else {
                targetFragment.execute(returnVal, e);
                if (!targetFragment.isDone())
                    return;
                CommonStage stage = (CommonStage) targetFragment.getStage();
                stage.setHead(this.head, key);
                stage.setNext(this.next, key);
                this.setNext(stage, key);
            }
        }
    }

}
