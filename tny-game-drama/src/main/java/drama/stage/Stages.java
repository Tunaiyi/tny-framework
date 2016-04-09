package drama.stage;

import com.tny.game.base.item.Done;
import com.tny.game.common.ExceptionUtils;
import drama.stage.exception.TaskTimeoutException;
import drama.stage.invok.*;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.function.*;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class Stages {

    static final TaskStageKey KEY = new TaskStageKey() {
    };

    static void checkKey(TaskStageKey key) {
        ExceptionUtils.checkNotNull(key, "TaskStageKey is null");
    }

    public static BooleanSupplier time(Duration duration) {
        return new TimeAwait(duration)::get;
    }

    public static <T> Supplier<Done<T>> time(T object, Duration duration) {
        return new TimeAwaitWith<>(object, duration)::get;
    }

    //region then 成功时处理
    public static VoidTaskStage run(Runnable fn) {
        return new ThenSuccessTaskStage<>(null, new RunFragment(fn));
    }

    public static VoidTaskStage run(TaskRunnable fn) {
        return new ThenSuccessTaskStage<>(null, new TaskRunFragment(fn));
    }

    public static <T> TaskStage<T> supply(Supplier<T> fn) {
        return new ThenSuccessTaskStage<>(null, new SupplyFragment<>(fn));
    }

    public static <T> TaskStage<T> supply(TaskSupplier<T> fn) {
        return new ThenSuccessTaskStage<>(null, new TaskSupplyFragment<>(fn));
    }
    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    public static VoidTaskStage awaitRun(BooleanSupplier fn) {
        return awaitRun(fn, null);
    }

    public static VoidTaskStage awaitRun(BooleanSupplier fn, Duration timeout) {
        return new AwaysVoidTaskStage(null, new WaitRunFragment(fn, timeout));
    }

    public static VoidTaskStage awaitRun(TaskBooleanSupplier fn) {
        return awaitRun(fn, null);
    }

    public static VoidTaskStage awaitRun(TaskBooleanSupplier fn, Duration timeout) {
        return new AwaysVoidTaskStage(null, new TaskWaitRunFragment(fn, timeout));
    }

    public static <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn) {
        return awaitSupply(fn, null);
    }

    public static <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn, Duration timeout) {
        return new ThenSuccessTaskStage<>(null, new WaitSupplyFragment<>(fn, timeout));
    }

    public static <T> TaskStage<T> awaitSupply(TaskSupplier<Done<T>> fn) {
        return awaitSupply(fn, null);
    }

    public static <T> TaskStage<T> awaitSupply(TaskSupplier<Done<T>> fn, Duration timeout) {
        return new ThenSuccessTaskStage<>(null, new TaskWaitSupplyFragment<>(fn, timeout));
    }

    public static <T> TaskStage<T> yieldForFuture(Future<T> future) {
        return yieldForFuture(future, null);
    }

    public static <T> TaskStage<T> yieldForFuture(Future<T> future, Duration duration) {
        return awaitSupply(new FutureAwait<>(future), duration);
    }

    public static VoidTaskStage yieldForWait(Duration duration) {
        return awaitRun(new TimeAwait(duration)::get, null);
    }

    public static <T> TaskStage<T> yieldForWait(T object, Duration duration) {
        return awaitSupply(new TimeAwaitWith<>(object, duration)::get, null);
    }


//    public static <R, T> TaskStage<T> awaitApply(Function<R, Done<T>> fn) {
//        return awaitApply(fn, null);
//    }
//
//    public static <R, T> TaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout) {
//        return new ThenSuccessTaskStage<>(null, new WaitApplyFragment<>(fn, timeout));
//    }
//
//    public static <R, T> TaskStage<T> awaitApply(TaskFunction<R, Done<T>> fn) {
//        return awaitApply(fn, null);
//    }
//
//    public static <R, T> TaskStage<T> awaitApply(TaskFunction<R, Done<T>> fn, Duration timeout) {
//        return new ThenSuccessTaskStage<>(null, new TaskWaitApplyFragment<>(fn, timeout));
//    }
//
//    public static <R> VoidTaskStage awaitAccept(Predicate<R> fn) {
//        return awaitAccept(fn, null);
//    }
//
//    public static <R> VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout) {
//        return new AwaysVoidTaskStage(null, new WaitAcceptFragment<>(fn, timeout));
//    }
//
//    public static <R> VoidTaskStage awaitAccept(TaskPredicate<R> fn) {
//        return awaitAccept(fn, null);
//    }
//
//    public static <R> VoidTaskStage awaitAccept(TaskPredicate<R> fn, Duration timeout) {
//        return new AwaysVoidTaskStage(null, new TaskWaitAcceptFragment<>(fn, timeout));
//    }
    //endregion

    static abstract class BaseTaskFragment<F, T, NR> extends TaskFragment<T, NR> {

        protected F fn;

        BaseTaskFragment(F fn) {
            super();
            this.fn = fn;
        }

        protected abstract NR invoke(T returnVal, Throwable e, TaskContext context);

        protected boolean isFailed(Throwable e) {
            return e != null;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e, TaskContext context) {
            if (isFailed(e) || this.isDone())
                return;
            NR result = invoke(returnVal, e, context);
            this.finish(result == null ? null : result);
        }

    }

    static abstract class VoidTaskFragment<F, NR> extends BaseTaskFragment<F, Void, NR> {

        VoidTaskFragment(F fn) {
            super(fn);
        }

        @Override
        protected boolean isNoneParam() {
            return true;
        }
    }

    //region JoinFragment classes
    static abstract class JoinFragment<F, T, TS extends CommonTaskStage> extends BaseTaskFragment<F, T, TS> {

        protected TS stage;

        JoinFragment(F fn) {
            super(fn);
        }

        TS getStage() {
            return stage;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e, TaskContext context) {
            if (this.isDone())
                return;
            stage = this.invoke(returnVal, e, context);
            if (stage != null) {
                finish(null);
            } else {
                fail(new NullPointerException("go on stage is null"));
            }
        }

    }

    static abstract class VoidJoinFragment<F, TS extends CommonTaskStage> extends JoinFragment<F, Void, TS> {

        VoidJoinFragment(F fn) {
            super(fn);
        }

        @Override
        protected boolean isNoneParam() {
            return true;
        }
    }

    static class JoinSupplyFragment<TS extends CommonTaskStage> extends VoidJoinFragment<Supplier<TS>, TS> {

        JoinSupplyFragment(Supplier<TS> fn) {
            super(fn);
        }

        protected TS invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.get();
        }

    }

    static class TaskJoinSupplyFragment<TS extends CommonTaskStage> extends VoidJoinFragment<TaskSupplier<TS>, TS> {

        TaskJoinSupplyFragment(TaskSupplier<TS> fn) {
            super(fn);
        }

        protected TS invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.get(context);
        }

    }

    static class JoinApplyFragment<T, TS extends CommonTaskStage> extends JoinFragment<Function<T, TS>, T, TS> {

        JoinApplyFragment(Function<T, TS> fn) {
            super(fn);
        }

        protected TS invoke(T returnVal, Throwable e, TaskContext context) {
            return fn.apply(returnVal);
        }

    }

    static class TaskJoinApplyFragment<T, TS extends CommonTaskStage> extends JoinFragment<TaskFunction<T, TS>, T, TS> {

        TaskJoinApplyFragment(TaskFunction<T, TS> fn) {
            super(fn);
        }

        protected TS invoke(T returnVal, Throwable e, TaskContext context) {
            return fn.apply(returnVal, context);
        }

    }
    //endregion

    //region RunFragment classes
    static class RunFragment extends VoidTaskFragment<Runnable, Void> {

        RunFragment(Runnable fn) {
            super(fn);
        }

        @Override
        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.run();
            return null;
        }
    }


    static class TaskRunFragment extends VoidTaskFragment<TaskRunnable, Void> {

        TaskRunFragment(TaskRunnable fn) {
            super(fn);
        }

        @Override
        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.run(context);
            return null;
        }
    }
    //endregion

    //region SupplyFragment classes
    static class SupplyFragment<T> extends VoidTaskFragment<Supplier<T>, T> {

        SupplyFragment(Supplier<T> fn) {
            super(fn);
        }

        @Override
        protected T invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.get();
        }
    }


    static class TaskSupplyFragment<T> extends VoidTaskFragment<TaskSupplier<T>, T> {

        TaskSupplyFragment(TaskSupplier<T> fn) {
            super(fn);
        }

        @Override
        protected T invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.get(context);
        }
    }
    //endregion

    //region ApplyFragment classes
    static class ApplyFragment<T, R> extends BaseTaskFragment<Function<T, R>, T, R> {

        ApplyFragment(Function<T, R> fn) {
            super(fn);
        }

        @Override
        protected R invoke(T returnVal, Throwable e, TaskContext context) {
            return fn.apply(returnVal);
        }
    }


    static class TaskApplyFragment<T, R> extends BaseTaskFragment<TaskFunction<T, R>, T, R> {

        TaskApplyFragment(TaskFunction<T, R> fn) {
            super(fn);
        }

        @Override
        protected R invoke(T returnVal, Throwable e, TaskContext context) {
            return fn.apply(returnVal, context);
        }
    }
    //endregion

    //region AcceptFragment classes

    static class AcceptFragment<T> extends BaseTaskFragment<Consumer<T>, T, Void> {

        AcceptFragment(Consumer<T> fn) {
            super(fn);
        }

        @Override
        protected Void invoke(T returnVal, Throwable e, TaskContext context) {
            fn.accept(returnVal);
            return null;
        }
    }


    static class TaskAcceptFragment<T> extends BaseTaskFragment<TaskConsumer<T>, T, Void> {

        TaskAcceptFragment(TaskConsumer<T> fn) {
            super(fn);
        }

        @Override
        protected Void invoke(T returnVal, Throwable e, TaskContext context) {
            fn.accept(returnVal, context);
            return null;
        }
    }
    //endregion

    //region doneFragment classes
    static abstract class DoneFragment<F, T, NR> extends BaseTaskFragment<F, T, NR> {

        DoneFragment(F fn) {
            super(fn);
        }

        protected abstract NR invoke(T returnVal, Throwable e, TaskContext context);

        protected boolean isSuccess(Throwable e) {
            return e == null;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e, TaskContext context) {
            if (this.isDone())
                return;
            NR result = invoke(returnVal, e, context);
            this.finish(result == null ? null : result);
        }

    }

    static abstract class VoidDoneFragment<F, NR> extends DoneFragment<F, Void, NR> {

        VoidDoneFragment(F fn) {
            super(fn);
        }

        @Override
        protected boolean isNoneParam() {
            return true;
        }

    }

    static class DoneRunFragment extends VoidDoneFragment<RunDone, Void> {

        DoneRunFragment(RunDone fn) {
            super(fn);
        }

        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.run(isSuccess(e), e);
            return null;
        }

    }

    static class TaskDoneRunFragment extends VoidDoneFragment<TaskRunDone, Void> {

        TaskDoneRunFragment(TaskRunDone fn) {
            super(fn);
        }

        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.run(isSuccess(e), e, context);
            return null;
        }

    }

    static class DoneSupplyFragment<NR> extends VoidDoneFragment<SupplyDone<NR>, NR> {

        DoneSupplyFragment(SupplyDone<NR> fn) {
            super(fn);
        }

        protected NR invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.handle(isSuccess(e), e);
        }

    }

    static class TaskDoneSupplyFragment<NR> extends VoidDoneFragment<TaskSupplyDone<NR>, NR> {

        TaskDoneSupplyFragment(TaskSupplyDone<NR> fn) {
            super(fn);
        }

        protected NR invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.handle(isSuccess(e), e, context);
        }

    }

    static class DoneApplyFragment<NV, NR> extends DoneFragment<ApplyDone<NV, NR>, NV, NR> {

        DoneApplyFragment(ApplyDone<NV, NR> fn) {
            super(fn);
        }

        protected NR invoke(NV returnVal, Throwable e, TaskContext context) {
            return fn.handle(isSuccess(e), returnVal, e);
        }

    }

    static class TaskDoneApplyFragment<NV, NR> extends DoneFragment<TaskApplyDone<NV, NR>, NV, NR> {

        TaskDoneApplyFragment(TaskApplyDone<NV, NR> fn) {
            super(fn);
        }

        protected NR invoke(NV returnVal, Throwable e, TaskContext context) {
            return fn.handle(isSuccess(e), returnVal, e, context);
        }

    }

    static class DoneAcceptFragment<NV> extends DoneFragment<AcceptDone<NV>, NV, Void> {

        DoneAcceptFragment(AcceptDone<NV> fn) {
            super(fn);
        }

        protected Void invoke(NV returnVal, Throwable e, TaskContext context) {
            fn.handle(isSuccess(e), returnVal, e);
            return null;
        }

    }

    static class TaskDoneAcceptFragment<NV> extends DoneFragment<TaskAcceptDone<NV>, NV, Void> {

        TaskDoneAcceptFragment(TaskAcceptDone<NV> fn) {
            super(fn);
        }

        protected Void invoke(NV returnVal, Throwable e, TaskContext context) {
            fn.handle(isSuccess(e), returnVal, e, context);
            return null;
        }

    }
    //endregion

    //region throwFragment class
    static abstract class ThrowFragment<F, T, NR> extends BaseTaskFragment<F, T, NR> {

        ThrowFragment(F fn) {
            super(fn);
        }

        protected abstract NR invoke(T returnVal, Throwable e, TaskContext context);

        @Override
        protected void doExecute(T returnVal, Throwable e, TaskContext context) {
            if (this.isDone())
                return;
            if (e == null) {
                this.finish(returnVal);
            } else {
                NR result = invoke(null, e, context);
                this.finish(result == null ? null : result);
            }
        }

    }

    static abstract class VoidThrowFragment<F, NR> extends ThrowFragment<F, Void, NR> {

        VoidThrowFragment(F fn) {
            super(fn);
        }

        @Override
        protected boolean isNoneParam() {
            return false;
        }

    }

    static class ThrowRunFragment extends VoidThrowFragment<CatcherRun, Void> {

        ThrowRunFragment(CatcherRun fn) {
            super(fn);
        }

        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.catchThrow(e);
            return null;
        }

    }

    static class TaskThrowRunFragment extends VoidThrowFragment<TaskCatcherRun, Void> {

        TaskThrowRunFragment(TaskCatcherRun fn) {
            super(fn);
        }

        protected Void invoke(Void returnVal, Throwable e, TaskContext context) {
            fn.catchThrow(e, context);
            return null;
        }

    }

    static class ThrowSupplyFragment<T> extends VoidThrowFragment<CatcherSupplier<T>, T> {

        ThrowSupplyFragment(CatcherSupplier<T> fn) {
            super(fn);
        }

        @Override
        protected T invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.catchThrow(e);
        }
    }


    static class TaskThrowSupplyFragment<T> extends VoidThrowFragment<TaskCatcherSupplier<T>, T> {

        TaskThrowSupplyFragment(TaskCatcherSupplier<T> fn) {
            super(fn);
        }

        @Override
        protected T invoke(Void returnVal, Throwable e, TaskContext context) {
            return fn.catchThrow(e, context);
        }
    }
    //endregion

    //region waitFragment classes

    static abstract class WaitFragment<F, T, R> extends BaseTaskFragment<F, T, R> {

        private long timeout = -1;
        private Duration duration;

        WaitFragment(F fn, Duration duration) {
            super(fn);
            this.duration = duration;
        }

        protected R checkDone(Done<R> result) {
            if (result == null)
                throw new NullPointerException("等待返回值必须为Boolean或Done, 不可为空");
            if (result.isSuccess()) {
                finish(result.get());
            } else {
                checkTimeout();
            }
            return result.get();
        }

        protected Boolean checkBoolean(Boolean result) {
            if (result == null)
                throw new NullPointerException("等待返回值必须为Boolean或Done, 不可为空");
            if (result) {
                finish(null);
            } else {
                checkTimeout();
            }
            return result;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e, TaskContext context) {
            if (isFailed(e) || this.isDone())
                return;
            if (duration != null && timeout == -1)
                this.timeout = System.currentTimeMillis() + duration.toMillis();
            invoke(returnVal, null, context);
        }

        protected void checkTimeout() {
            if (timeout > 0 && System.currentTimeMillis() > timeout)
                throw new TaskTimeoutException("等待任务超时");
        }

    }


    static abstract class VoidWaitFragment<F, R> extends WaitFragment<F, Void, R> {

        VoidWaitFragment(F fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected boolean isNoneParam() {
            return true;
        }

    }

    static class WaitRunFragment extends VoidWaitFragment<BooleanSupplier, Boolean> {

        WaitRunFragment(BooleanSupplier fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected Boolean invoke(Void returnVal, Throwable e, TaskContext context) {
            return checkBoolean(fn.getAsBoolean());
        }

    }

    static class TaskWaitRunFragment extends VoidWaitFragment<TaskBooleanSupplier, Boolean> {

        TaskWaitRunFragment(TaskBooleanSupplier fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected Boolean invoke(Void returnVal, Throwable e, TaskContext context) {
            return checkBoolean(fn.getAsBoolean(context));
        }

    }

    static class WaitSupplyFragment<R> extends VoidWaitFragment<Supplier<Done<R>>, R> {

        WaitSupplyFragment(Supplier<Done<R>> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(Void returnVal, Throwable e, TaskContext context) {
            return checkDone(fn.get());
        }

    }

    static class TaskWaitSupplyFragment<R> extends VoidWaitFragment<TaskSupplier<Done<R>>, R> {

        TaskWaitSupplyFragment(TaskSupplier<Done<R>> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(Void returnVal, Throwable e, TaskContext context) {
            return checkDone(fn.get(context));
        }

    }

    static class WaitApplyFragment<T, R> extends WaitFragment<Function<T, Done<R>>, T, R> {

        WaitApplyFragment(Function<T, Done<R>> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(T returnVal, Throwable e, TaskContext context) {
            return checkDone(fn.apply(returnVal));
        }

    }

    static class TaskWaitApplyFragment<T, R> extends WaitFragment<TaskFunction<T, Done<R>>, T, R> {

        TaskWaitApplyFragment(TaskFunction<T, Done<R>> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(T returnVal, Throwable e, TaskContext context) {
            return checkDone(fn.apply(returnVal, context));
        }

    }

    static class WaitAcceptFragment<T> extends WaitFragment<Predicate<T>, T, Boolean> {

        WaitAcceptFragment(Predicate<T> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected Boolean invoke(T returnVal, Throwable e, TaskContext context) {
            return checkBoolean(fn.test(returnVal));
        }

    }

    static class TaskWaitAcceptFragment<T> extends WaitFragment<TaskPredicate<T>, T, Boolean> {

        TaskWaitAcceptFragment(TaskPredicate<T> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected Boolean invoke(T returnVal, Throwable e, TaskContext context) {
            return checkBoolean(fn.test(returnVal, context));
        }

    }

    //endregion

}