package drama.stage;

import com.tny.game.actor.task.invok.*;
import com.tny.game.base.item.Done;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Kun Yang on 16/1/22.
 */
public interface TaskStage<R> extends VoidTaskStage {

    <NR> TaskStage<NR> joinApply(Function<R, TaskStage<NR>> fn);

    <NR> TaskStage<NR> joinApplyWith(TaskFunction<R, TaskStage<NR>> fn);

    VoidTaskStage joinAccept(Function<R, ? extends VoidTaskStage> fn);

    VoidTaskStage joinAcceptWith(TaskFunction<R, ? extends VoidTaskStage> fn);

    <N> TaskStage<N> thenApply(Function<R, N> fn);

    <N> TaskStage<N> thenApplyWith(TaskFunction<R, N> fn);

    VoidTaskStage thenAccept(Consumer<R> fn);

    VoidTaskStage thenAcceptWith(TaskConsumer<R> fn);

    <T> TaskStage<T> doneApply(ApplyDone<R, T> fn);

    <T> TaskStage<T> doneApplyWith(TaskApplyDone<R, T> fn);

    VoidTaskStage doneAccept(AcceptDone<R> fn);

    VoidTaskStage doneAcceptWith(TaskAcceptDone<R> fn);

    TaskStage<R> thenThrow(CatcherSupplier<R> fn);

    TaskStage<R> thenThrow(TaskCatcherSupplier<R> fn);

    <T> TaskStage<T> awaitApply(Function<R, Done<T>> fn);

    <T> TaskStage<T> awaitApply(Function<R, Done<T>> fn, Duration timeout);

    <T> TaskStage<T> awaitApplyWith(TaskFunction<R, Done<T>> fn);

    <T> TaskStage<T> awaitApplyWith(TaskFunction<R, Done<T>> fn, Duration timeout);

    VoidTaskStage awaitAccept(Predicate<R> fn);

    VoidTaskStage awaitAccept(Predicate<R> fn, Duration timeout);

    VoidTaskStage awaitAcceptWith(TaskPredicate<R> fn);

    VoidTaskStage awaitAcceptWith(TaskPredicate<R> fn, Duration timeout);

}
