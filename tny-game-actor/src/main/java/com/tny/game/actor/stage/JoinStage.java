package com.tny.game.actor.stage;


import com.tny.game.actor.stage.Flows.JoinFragment;

/**
 * Created by Kun Yang on 16/1/22.
 */
class JoinStage<T> extends BaseStage<Stage<T>> {

    protected JoinFragment<?, ?, T> targetFragment;

    @SuppressWarnings("unchecked")
    public JoinStage(Object name, JoinFragment<?, ?, T> fragment) {
        super(name);
        this.targetFragment = fragment;
    }

    // @SuppressWarnings("unchecked")
    // public JoinStage(CommonStage head, JoinSupplierDoneSupplierFragment<T> fragment) {
    //     super(head);
    //     this.targetFragment = fragment;
    // }

    @Override
    public boolean isNoneParam() {
        return false;
    }

    @Override
    public Fragment<?, Stage<T>> getFragment() {
        return this.targetFragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(Fragment<?, ?> prev, Object returnVal, Throwable e) {
        while (true) {
            if (this.targetFragment.isDone()) {
                // if (next != null && next.isCanRun(targetFragment)) {
                //     if (next.isNoneParam())
                //         next.run(targetFragment, null, targetFragment.getCause(), key);
                //     else
                //         next.run(targetFragment, returnVal, targetFragment.getCause(), key);
                // }
                return;
            } else {
                targetFragment.execute(returnVal, e);
                if (!targetFragment.isDone())
                    return;
                InnerStage stage = (InnerStage) targetFragment.getStage();
                // stage.setHead(this.head, key);
                stage.setNext(this.next);
                this.setNext(stage);
            }
        }
    }

}
