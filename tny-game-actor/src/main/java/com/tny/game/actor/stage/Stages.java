package com.tny.game.actor.stage;


import com.tny.game.actor.Completable;
import com.tny.game.actor.DoneSupplier;
import com.tny.game.actor.stage.exception.TaskTimeoutException;
import com.tny.game.actor.stage.invok.AcceptDone;
import com.tny.game.actor.stage.invok.ApplyDone;
import com.tny.game.actor.stage.invok.ApplyStageable;
import com.tny.game.actor.stage.invok.CatcherRun;
import com.tny.game.actor.stage.invok.CatcherSupplier;
import com.tny.game.actor.stage.invok.RunDone;
import com.tny.game.actor.stage.invok.SupplyDone;
import com.tny.game.actor.stage.invok.SupplyStageable;
import com.tny.game.common.ExceptionUtils;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.utils.Done;
import com.tny.game.common.utils.DoneUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class Stages {

    static final TaskStageKey KEY = new TaskStageKey() {
    };

    static void checkKey(TaskStageKey key) {
        ExceptionUtils.checkNotNull(key, "TaskStageKey is null");
    }

    public static void process(Stage stage) {
        ObjectUtils.as(stage, CommonStage.class).start();
    }

    public static void cancel(Stage stage) {
        ObjectUtils.as(stage, CommonStage.class).cancel();
    }

    public static <T> Done<T> getResult(Stage stage) {
        if (!stage.isDone())
            return DoneUtils.fail();
        if (stage instanceof TypeStage) {
            TypeStage<T> typeStage = (TypeStage<T>) stage;
            return DoneUtils.succNullable(typeStage.getResult());
        } else if (stage instanceof VoidStage) {
            return DoneUtils.succNullable(null);
        }
        return DoneUtils.fail();
    }

    public static Done<Throwable> getCause(Stage stage) {
        if (!stage.isDone())
            return DoneUtils.fail();
        return DoneUtils.succNullable(stage.getCause());
    }

    public static Completable time(Duration duration) {
        return new TimeAwait(duration)::get;
    }

    public static <T> DoneSupplier<T> time(T object, Duration duration) {
        return new TimeAwaitWith<>(object, duration)::get;
    }

    //region then 成功时处理
    public static VoidStage of(Runnable fn) {
        return new ThenSuccessStage<>(null, new RunFragment(fn));
    }

    public static <T> TypeStage<T> of(Supplier<T> fn) {
        return new ThenSuccessStage<>(null, new SupplyFragment<>(fn));
    }
    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    public static VoidStage waitUntil(Iterable<? extends Completable> fns) {
        return waitUntil(fns, null);
    }

    public static VoidStage waitUntil(Iterable<? extends Completable> fns, Duration timeout) {
        return new AwaysStage(null, new WaitRunFragment(fns, timeout));
    }

    public static VoidStage waitUntil(Completable fn) {
        return waitUntil(fn, null);
    }

    public static VoidStage waitUntil(Completable fn, Duration timeout) {
        return new AwaysStage(null, new WaitRunFragment(fn, timeout));
    }

    public static VoidStage waitTime(Duration duration) {
        return waitUntil(new TimeAwait(duration)::get, null);
    }

    public static <T> TypeStage<T> waitTime(T object, Duration duration) {
        return waitFor(new TimeAwaitWith<>(object, duration)::get, null);
    }

    public static <T> TypeStage<T> waitFor(DoneSupplier<T> fn) {
        return waitFor(fn, null);
    }

    public static <T> TypeStage<T> waitFor(DoneSupplier<T> fn, Duration timeout) {
        return new ThenSuccessStage<>(null, new WaitSupplyFragment<>(fn, timeout));
    }

    public static <T> TypeStage<List<T>> waitFor(Iterable<? extends Supplier<Done<T>>> fns) {
        return waitFor(fns, null);
    }

    public static <T> TypeStage<List<T>> waitFor(Iterable<? extends Supplier<Done<T>>> fns, Duration timeout) {
        return new ThenSuccessStage<>(null, new WaitSupplyDoneListFragment<>(fns, timeout));
    }

    public static <K, T> TypeStage<Map<K, T>> waitFor(Map<K, ? extends Supplier<Done<T>>> fns) {
        return waitFor(fns, null);
    }

    public static <K, T> TypeStage<Map<K, T>> waitFor(Map<K, ? extends Supplier<Done<T>>> fns, Duration timeout) {
        return new ThenSuccessStage<>(null, new WaitSupplyDoneMapFragment<>(fns, timeout));
    }

    public static <T> TypeStage<T> waitFor(Future<T> future) {
        return waitFor(future, null);
    }

    public static <T> TypeStage<T> waitFor(Future<T> future, Duration duration) {
        return waitFor(new FutureAwait<>(future), duration);
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

        boolean isFailed(Throwable e) {
            return e != null;
        }

        protected abstract NR invoke(T returnVal, Throwable e);

        @Override
        protected void doExecute(T returnVal, Throwable e) {
            if (isFailed(e) || this.isDone())
                return;
            NR result = invoke(returnVal, e);
            this.finish(result);
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
    static abstract class JoinFragment<F, T, TS extends Stage> extends BaseTaskFragment<F, T, TS> {

        protected TS stage;

        JoinFragment(F fn) {
            super(fn);
        }

        TS getStage() {
            return stage;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e) {
            if (this.isDone())
                return;
            stage = this.invoke(returnVal, e);
            if (stage != null) {
                finish(null);
            } else {
                fail(new NullPointerException("go on stage is null"));
            }
        }

    }

    static abstract class VoidJoinFragment<F, TS extends Stage> extends JoinFragment<F, Void, TS> {

        VoidJoinFragment(F fn) {
            super(fn);
        }

        @Override
        protected boolean isNoneParam() {
            return true;
        }
    }

    static class JoinSupplyFragment<TS extends Stage> extends VoidJoinFragment<Supplier<TS>, TS> {

        JoinSupplyFragment(Supplier<TS> fn) {
            super(fn);
        }


        @Override
        protected TS invoke(Void returnVal, Throwable e) {
            return fn.get();
        }

    }

    static class JoinApplyFragment<T, TS extends Stage> extends JoinFragment<Function<T, TS>, T, TS> {

        JoinApplyFragment(Function<T, TS> fn) {
            super(fn);
        }

        @Override
        protected TS invoke(T returnVal, Throwable e) {
            return fn.apply(returnVal);
        }

    }

    static class JoinSupplyStageableFragment<TS extends Stage> extends VoidJoinFragment<SupplyStageable<TS>, TS> {

        JoinSupplyStageableFragment(SupplyStageable<TS> fn) {
            super(fn);
        }


        @Override
        protected TS invoke(Void returnVal, Throwable e) {
            return fn.stageable().stage();
        }

    }

    static class JoinApplyStageableFragment<T, TS extends Stage> extends JoinFragment<ApplyStageable<T, TS>, T, TS> {

        JoinApplyStageableFragment(ApplyStageable<T, TS> fn) {
            super(fn);
        }

        @Override
        protected TS invoke(T returnVal, Throwable e) {
            return fn.stageable(returnVal).stage();
        }

    }

    static class JoinSupplierAvailableFragment<T> extends VoidJoinFragment<Supplier<DoneSupplier<T>>, TypeStage<T>> {

        JoinSupplierAvailableFragment(Supplier<DoneSupplier<T>> fn) {
            super(fn);
        }

        @Override
        protected TypeStage<T> invoke(Void returnVal, Throwable e) {
            return waitFor(fn.get());
        }

    }

    static class JoinSupplierCompletableFragment extends VoidJoinFragment<Supplier<Completable>, VoidStage> {

        JoinSupplierCompletableFragment(Supplier<Completable> fn) {
            super(fn);
        }

        @Override
        protected VoidStage invoke(Void returnVal, Throwable e) {
            return waitUntil(fn.get());
        }

    }

    static class JoinApplyCompletableFragment<R> extends JoinFragment<Function<R, Completable>, R, VoidStage> {

        JoinApplyCompletableFragment(Function<R, Completable> fn) {
            super(fn);
        }

        @Override
        protected VoidStage invoke(R returnVal, Throwable e) {
            return waitUntil(fn.apply(returnVal));
        }


    }

    static class JoinApplyAvailableFragment<T, R> extends JoinFragment<Function<R, DoneSupplier<T>>, R, TypeStage<T>> {

        JoinApplyAvailableFragment(Function<R, DoneSupplier<T>> fn) {
            super(fn);
        }

        @Override
        protected TypeStage<T> invoke(R returnVal, Throwable e) {
            return waitFor(fn.apply(returnVal));
        }

    }

    //endregion

    //region RunFragment classes
    static class RunFragment extends VoidTaskFragment<Runnable, Void> {

        RunFragment(Runnable fn) {
            super(fn);
        }

        @Override
        protected Void invoke(Void returnVal, Throwable e) {
            fn.run();
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
        protected T invoke(Void returnVal, Throwable e) {
            return fn.get();
        }
    }
    //endregion

    //region ApplyFragment classes
    static class ApplyFragment<T, R> extends BaseTaskFragment<Function<T, R>, T, R> {

        ApplyFragment(Function<T, R> fn) {
            super(fn);
        }

        @Override
        protected R invoke(T returnVal, Throwable e) {
            return fn.apply(returnVal);
        }
    }
    //endregion

    //region AcceptFragment classes

    static class AcceptFragment<T> extends BaseTaskFragment<Consumer<T>, T, Void> {

        AcceptFragment(Consumer<T> fn) {
            super(fn);
        }

        @Override
        protected Void invoke(T returnVal, Throwable e) {
            fn.accept(returnVal);
            return null;
        }
    }
    //endregion

    //region doneFragment classes
    static abstract class DoneFragment<F, T, NR> extends BaseTaskFragment<F, T, NR> {

        DoneFragment(F fn) {
            super(fn);
        }

        @Override
        protected abstract NR invoke(T returnVal, Throwable e);

        protected boolean isSuccess(Throwable e) {
            return e == null;
        }

        @Override
        protected void doExecute(T returnVal, Throwable e) {
            if (this.isDone())
                return;
            NR result = invoke(returnVal, e);
            this.finish(result);
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

        @Override
        protected Void invoke(Void returnVal, Throwable e) {
            fn.run(isSuccess(e), e);
            return null;
        }

    }

    static class DoneSupplyFragment<NR> extends VoidDoneFragment<SupplyDone<NR>, NR> {

        DoneSupplyFragment(SupplyDone<NR> fn) {
            super(fn);
        }

        @Override
        protected NR invoke(Void returnVal, Throwable e) {
            return fn.handle(isSuccess(e), e);
        }

    }

    static class DoneApplyFragment<NV, NR> extends DoneFragment<ApplyDone<NV, NR>, NV, NR> {

        DoneApplyFragment(ApplyDone<NV, NR> fn) {
            super(fn);
        }

        @Override
        protected NR invoke(NV returnVal, Throwable e) {
            return fn.handle(isSuccess(e), returnVal, e);
        }

    }

    static class DoneAcceptFragment<NV> extends DoneFragment<AcceptDone<NV>, NV, Void> {

        DoneAcceptFragment(AcceptDone<NV> fn) {
            super(fn);
        }

        @Override
        protected Void invoke(NV returnVal, Throwable e) {
            fn.handle(isSuccess(e), returnVal, e);
            return null;
        }

    }
    //endregion

    //region throwFragment class
    static abstract class ThrowFragment<F, T, NR> extends BaseTaskFragment<F, T, NR> {

        ThrowFragment(F fn) {
            super(fn);
        }

        @Override
        protected abstract NR invoke(T returnVal, Throwable e);

        @Override
        protected void doExecute(T returnVal, Throwable e) {
            if (this.isDone())
                return;
            if (e == null) {
                this.finish(returnVal);
            } else {
                NR result = invoke(null, e);
                this.finish(result);
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

        @Override
        protected Void invoke(Void returnVal, Throwable e) {
            fn.catchThrow(e);
            return null;
        }

    }

    static class ThrowSupplyFragment<T> extends VoidThrowFragment<CatcherSupplier<T>, T> {

        ThrowSupplyFragment(CatcherSupplier<T> fn) {
            super(fn);
        }

        @Override
        protected T invoke(Void returnVal, Throwable e) {
            return fn.catchThrow(e);
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

        R checkDone(Done<R> result) {
            if (result == null)
                throw new NullPointerException("等待返回值必须为Boolean或Done, 不可为空");
            if (result.isSuccess()) {
                finish(result.get());
            } else {
                checkTimeout();
            }
            return result.get();
        }

        Boolean checkBoolean(Boolean result) {
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
        protected void doExecute(T returnVal, Throwable e) {
            if (isFailed(e) || this.isDone())
                return;
            if (duration != null && timeout == -1)
                this.timeout = System.currentTimeMillis() + duration.toMillis();
            invoke(returnVal, null);
        }

        void checkTimeout() {
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

    static class WaitRunFragment extends VoidWaitFragment<Completable, Boolean> {

        WaitRunFragment(Completable fn, Duration timeout) {
            super(fn, timeout);
        }

        WaitRunFragment(Iterable<? extends Completable> fns, Duration timeout) {
            super(() -> {
                for (Completable fn : fns)
                    if (!fn.isCompleted())
                        return false;
                return true;
            }, timeout);
        }

        @Override
        protected Boolean invoke(Void returnVal, Throwable e) {
            return checkBoolean(fn.isCompleted());
        }

    }

    static class WaitSupplyFragment<R> extends VoidWaitFragment<DoneSupplier<R>, R> {

        WaitSupplyFragment(DoneSupplier<R> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(Void returnVal, Throwable e) {
            return checkDone(fn.getDone());
        }

    }

    private static class WaitSupplyDoneListFragment<R> extends VoidWaitFragment<Supplier<Done<List<R>>>, List<R>> {

        WaitSupplyDoneListFragment(Iterable<? extends Supplier<Done<R>>> fns, Duration timeout) {
            super(() -> {
                List<R> result = new ArrayList<>();
                for (Supplier<Done<R>> fn : fns) {
                    Done<R> done = fn.get();
                    if (!done.isSuccess())
                        return DoneUtils.fail();
                    else
                        result.add(done.get());
                }
                return DoneUtils.succ(result);
            }, timeout);
        }

        @Override
        protected List<R> invoke(Void returnVal, Throwable e) {
            return checkDone(fn.get());
        }

    }

    private static class WaitSupplyDoneMapFragment<K, R> extends VoidWaitFragment<Supplier<Done<Map<K, R>>>, Map<K, R>> {

        WaitSupplyDoneMapFragment(Map<K, ? extends Supplier<Done<R>>> fns, Duration timeout) {
            super(() -> {
                for (Supplier<Done<R>> fn : fns.values()) {
                    Done<R> done = fn.get();
                    if (!done.isSuccess())
                        return DoneUtils.fail();
                }
                return DoneUtils.succ(fns.entrySet().stream()
                        .collect(Collectors.toMap(
                                Entry::getKey,
                                e -> e.getValue().get().get()
                        )));
            }, timeout);
        }

        @Override
        protected Map<K, R> invoke(Void returnVal, Throwable e) {
            return checkDone(fn.get());
        }

    }

    static class WaitApplyFragment<T, R> extends WaitFragment<Function<T, Done<R>>, T, R> {

        WaitApplyFragment(Function<T, Done<R>> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected R invoke(T returnVal, Throwable e) {
            return checkDone(fn.apply(returnVal));
        }

    }

    static class WaitAcceptFragment<T> extends WaitFragment<Predicate<T>, T, Boolean> {

        WaitAcceptFragment(Predicate<T> fn, Duration timeout) {
            super(fn, timeout);
        }

        @Override
        protected Boolean invoke(T returnVal, Throwable e) {
            return checkBoolean(fn.test(returnVal));
        }

    }

    //endregion

}
