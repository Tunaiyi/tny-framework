package drama.stage;

import com.tny.game.actor.task.Stages.*;
import com.tny.game.actor.task.invok.*;
import com.tny.game.base.item.Done;
import com.tny.game.common.ExceptionUtils;

import java.time.Duration;
import java.util.function.*;

/**
 * Created by Kun Yang on 16/1/22.
 */
interface InnerTaskStage<R> extends TaskStage<R> {

    default boolean isFinalSuccess() {
        return this.getHead().isAllSuccess();
    }

    default boolean isNoneParam() {
        return getTaskFragment().isNoneParam();
    }

    default void checkNextExist() {
        ExceptionUtils.checkState(getNext() == null, "stage next stage is exist");
    }

    //region Join 链接另一个fn返回的代码段

    default VoidTaskStage joinSupply(Supplier<? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    default VoidTaskStage joinSupplyWith(TaskSupplier<? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new TaskJoinSupplyFragment<>(fn)), Stages.KEY);
    }

    default VoidTaskStage joinAccept(Function<R, ? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    default VoidTaskStage joinAcceptWith(TaskFunction<R, ? extends VoidTaskStage> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new TaskJoinApplyFragment<>(fn)), Stages.KEY);
    }

    default <T> TaskStage<T> joinRun(Supplier<? extends TaskStage<T>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinSupplyFragment<>(fn)), Stages.KEY);
    }

    default <T> TaskStage<T> joinRunWith(TaskSupplier<? extends TaskStage<T>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new TaskJoinSupplyFragment<>(fn)), Stages.KEY);
    }

    @Override
    default <NR> TaskStage<NR> joinApply(Function<R, TaskStage<NR>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new JoinApplyFragment<>(fn)), Stages.KEY);
    }

    default <NR> TaskStage<NR> joinApplyWith(TaskFunction<R, TaskStage<NR>> fn) {
        checkNextExist();
        return setNext(new JoinTaskStage<>(this.getHead(), new TaskJoinApplyFragment<>(fn)), Stages.KEY);
    }

    //endregion

    //region then 成功时处理
    @Override
    default VoidTaskStage thenRun(Runnable fn) {
        checkNextExist();
        return setNext(new ThenSuccessTaskStage<>(this.getHead(), new RunFragment(fn)), Stages.KEY);
    }

    @Override
    default VoidTaskStage thenRunWith(TaskRunnable fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> thenSupply(Supplier<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new SupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> thenSupplyWith(TaskSupplier<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <N> TaskStage<N> thenApply(Function<R, N> fn) {
        checkNextExist();
        InnerTaskStage<N> stage = new ThenSuccessTaskStage<>(this.getHead(), new ApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <N> TaskStage<N> thenApplyWith(TaskFunction<R, N> fn) {
        checkNextExist();
        InnerTaskStage<N> stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage thenAccept(Consumer<R> fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenSuccessTaskStage<>(this.getHead(), new AcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage thenAcceptWith(TaskConsumer<R> fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskAcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }
    //endregion

    //region done 完成时(无论是否成功)处理
    @Override
    default VoidTaskStage doneRun(RunDone fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new DoneRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage doneRunWith(TaskRunDone fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new TaskDoneRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> doneSupply(SupplyDone<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> doneSupplyWith(TaskSupplyDone<T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new TaskDoneSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> doneApply(ApplyDone<R, T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new DoneApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> doneApplyWith(TaskApplyDone<R, T> fn) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenDoneTaskStage<>(this.getHead(), new TaskDoneApplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage doneAccept(AcceptDone<R> fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new DoneAcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage doneAcceptWith(TaskAcceptDone<R> fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new TaskDoneAcceptFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }
    //endregion

    //region throw 异常时处理
    @Override
    default VoidTaskStage thenThrow(CatcherRun fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new ThrowRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage thenThrow(TaskCatcherRun fn) {
        checkNextExist();
        VoidTaskStage stage = new ThenDoneTaskStage<>(this.getHead(), new TaskThrowRunFragment(fn));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default TaskStage<R> thenThrow(CatcherSupplier<R> fn) {
        checkNextExist();
        InnerTaskStage<R> stage = new ThenDoneTaskStage<>(this.getHead(), new ThrowSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }


    @Override
    default TaskStage<R> thenThrow(TaskCatcherSupplier<R> fn) {
        checkNextExist();
        InnerTaskStage<R> stage = new ThenDoneTaskStage<>(this.getHead(), new TaskThrowSupplyFragment<>(fn));
        return setNext(stage, Stages.KEY);
    }
    //endregion

    //region wait 等待方法返回true或返回的Done为true时继续执行
    @Override
    default VoidTaskStage awaitRun(BooleanSupplier fn) {
        return this.awaitRun(fn, null);
    }

    @Override
    default VoidTaskStage awaitRun(BooleanSupplier fn, Duration timeout) {
        checkNextExist();
        VoidTaskStage stage = new AwaysVoidTaskStage(this.getHead(), new WaitRunFragment(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage awaitRunWith(TaskBooleanSupplier fn) {
        return this.awaitRunWith(fn, null);
    }

    @Override
    default VoidTaskStage awaitRunWith(TaskBooleanSupplier fn, Duration timeout) {
        checkNextExist();
        VoidTaskStage stage = new AwaysVoidTaskStage(this.getHead(), new TaskWaitRunFragment(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn) {
        return this.awaitSupply(fn, null);
    }

    @Override
    default <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitSupplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> awaitSupplyWith(TaskSupplier<Done<T>> fn) {
        return this.awaitSupplyWith(fn, null);
    }

    @Override
    default <T> TaskStage<T> awaitSupplyWith(TaskSupplier<Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskWaitSupplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> awaitApply(Function<R, Done<T>> fn) {
        return this.awaitApply(fn, null);
    }

    @Override
    default <T> TaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new WaitApplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default <T> TaskStage<T> awaitApplyWith(TaskFunction<R, Done<T>> fn) {
        return this.awaitApplyWith(fn, null);
    }

    @Override
    default <T> TaskStage<T> awaitApplyWith(TaskFunction<R, Done<T>> fn, Duration timeout) {
        checkNextExist();
        InnerTaskStage<T> stage = new ThenSuccessTaskStage<>(this.getHead(), new TaskWaitApplyFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage awaitAccept(Predicate<R> fn) {
        return this.awaitAccept(fn, null);
    }

    @Override
    default VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout) {
        checkNextExist();
        VoidTaskStage stage = new AwaysVoidTaskStage(this.getHead(), new WaitAcceptFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }

    @Override
    default VoidTaskStage awaitAcceptWith(TaskPredicate<R> fn) {
        return this.awaitAcceptWith(fn, null);
    }

    @Override
    default VoidTaskStage awaitAcceptWith(TaskPredicate<R> fn, Duration timeout) {
        checkNextExist();
        VoidTaskStage stage = new AwaysVoidTaskStage(this.getHead(), new TaskWaitAcceptFragment<>(fn, timeout));
        return setNext(stage, Stages.KEY);
    }
    //endregion


}
