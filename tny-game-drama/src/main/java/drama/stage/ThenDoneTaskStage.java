package drama.stage;

/**
 * Created by Kun Yang on 16/1/22.
 */
class ThenDoneTaskStage<R> extends DefaultTaskStage<R> {

    public ThenDoneTaskStage(CommonTaskStage head, TaskFragment<?, R> fragment) {
        super(head, fragment);
    }

    @Override
    public boolean checkCanRun(TaskFragment<?, ?> prev) {
        return prev.isDone();
    }

    @Override
    public void run(TaskFragment<?, ?> prev, Object returnVal, Throwable e, TaskContext context, TaskStageKey key) {
        while (true) {
            if (fragment.isDone()) {
                if (next != null && next.isCanRun(fragment)) {
                    if (next.isNoneParam())
                        next.run(fragment, null, fragment.getCause(), context, key);
                    else
                        next.run(fragment, fragment.getResult(), fragment.getCause(), context, key);
                }
                return;
            } else {
                fragment.execute(returnVal, e, context);
                if (fragment.isSuccess()) {
                    prev.obtrudeResult(null);
                } else if (!fragment.isDone()) {
                    return;
                }
            }
        }
    }

}
