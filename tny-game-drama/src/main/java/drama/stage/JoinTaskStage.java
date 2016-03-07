package drama.stage;

import com.tny.game.actor.task.Stages.JoinFragment;

/**
 * Created by Kun Yang on 16/1/22.
 */
class JoinTaskStage<R, NR, TS extends CommonTaskStage> extends BaseTaskStage<NR> {

    protected JoinFragment<?, R, TS> targetFragment;

    public JoinTaskStage(CommonTaskStage head, JoinFragment<?, R, TS> fragment) {
        super(head);
        this.targetFragment = fragment;
    }

    @Override
    public TaskFragment<?, ?> getTaskFragment() {
        return this.targetFragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskContext context, TaskStageKey key) {
        while (true) {
            if (this.targetFragment.isDone()) {
                if (next != null && next.isCanRun(targetFragment)) {
                    if (next.isNoneParam())
                        next.run(targetFragment, null, targetFragment.getCause(), context, key);
                    else
                        next.run(targetFragment, returnVal, targetFragment.getCause(), context, key);
                }
                return;
            } else {
                targetFragment.execute((R) returnVal, e, context);
                if (!targetFragment.isDone())
                    return;
                TS stage = targetFragment.getStage();
                stage.setHead(this.head, key);
                stage.setNext(this.next, key);
                this.setNext(stage, key);
            }
        }
    }

}
