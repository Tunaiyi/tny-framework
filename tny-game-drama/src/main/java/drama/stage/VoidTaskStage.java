package drama.stage;

import com.tny.game.base.item.Done;
import drama.stage.invok.CatcherRun;
import drama.stage.invok.RunDone;
import drama.stage.invok.SupplyDone;
import drama.stage.invok.TaskBooleanSupplier;
import drama.stage.invok.TaskCatcherRun;
import drama.stage.invok.TaskRunDone;
import drama.stage.invok.TaskRunnable;
import drama.stage.invok.TaskSupplier;
import drama.stage.invok.TaskSupplyDone;

import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface VoidTaskStage extends CommonTaskStage {

    <T> TaskStage<T> joinRun(Supplier<? extends TaskStage<T>> fn);

    <T> TaskStage<T> joinRunWith(TaskSupplier<? extends TaskStage<T>> fn);

    VoidTaskStage joinSupply(Supplier<? extends VoidTaskStage> fn);

    VoidTaskStage joinSupplyWith(TaskSupplier<? extends VoidTaskStage> fn);

    VoidTaskStage thenRun(Runnable fn);

    VoidTaskStage thenRunWith(TaskRunnable fn);

    <T> TaskStage<T> thenSupply(Supplier<T> fn);

    <T> TaskStage<T> thenSupplyWith(TaskSupplier<T> fn);

    VoidTaskStage doneRun(RunDone fn);

    VoidTaskStage doneRunWith(TaskRunDone fn);

    <T> TaskStage<T> doneSupply(SupplyDone<T> fn);

    <T> TaskStage<T> doneSupplyWith(TaskSupplyDone<T> fn);

    VoidTaskStage thenThrow(CatcherRun catcher);

    VoidTaskStage thenThrow(TaskCatcherRun catcher);

    VoidTaskStage awaitRun(BooleanSupplier fn);

    VoidTaskStage awaitRun(BooleanSupplier fn, Duration timeout);

    VoidTaskStage awaitRunWith(TaskBooleanSupplier fn);

    VoidTaskStage awaitRunWith(TaskBooleanSupplier fn, Duration timeout);

    <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn);

    <T> TaskStage<T> awaitSupply(Supplier<Done<T>> fn, Duration timeout);

    <T> TaskStage<T> awaitSupplyWith(TaskSupplier<Done<T>> fn);

    <T> TaskStage<T> awaitSupplyWith(TaskSupplier<Done<T>> fn, Duration timeout);

}
