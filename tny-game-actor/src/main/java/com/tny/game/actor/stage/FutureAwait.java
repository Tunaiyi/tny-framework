package com.tny.game.actor.stage;


import com.tny.game.actor.Available;
import com.tny.game.actor.stage.exception.TaskException;
import com.tny.game.common.utils.DoneUtils;
import com.tny.game.common.utils.Done;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class FutureAwait<T> implements Available<T> {

    private Future<T> future;

    FutureAwait(Future<T> future) {
        this.future = future;
    }


    @Override
    public Done<T> achieve() {
        if (!future.isDone()) {
            return DoneUtils.fail();
        } else {
            try {
                return DoneUtils.succ(future.get());
            } catch (ExecutionException e) {
                throw new TaskException(e.getCause());
            } catch (InterruptedException e) {
                throw new TaskException(e);
            }
        }
    }
}
