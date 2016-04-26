package com.tny.game.actor.task;

import com.tny.game.actor.task.exception.TaskException;
import com.tny.game.common.utils.Do;
import com.tny.game.common.utils.Done;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class FutureAwait<T> implements Supplier<Done<T>> {

    private Future<T> future;

    FutureAwait(Future<T> future) {
        this.future = future;
    }

    @Override
    public Done<T> get() {
        if (!future.isDone()) {
            return Do.fail();
        } else {
            try {
                return Do.succ(future.get());
            } catch (ExecutionException e) {
                throw new TaskException(e.getCause());
            } catch (InterruptedException e) {
                throw new TaskException(e);
            }
        }
    }
}
