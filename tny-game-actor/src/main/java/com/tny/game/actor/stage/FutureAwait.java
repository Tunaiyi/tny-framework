package com.tny.game.actor.stage;


import com.tny.game.actor.*;
import com.tny.game.actor.stage.exception.*;
import com.tny.game.common.utils.*;

import java.util.concurrent.*;

/**
 * Created by Kun Yang on 16/1/23.
 */
public class FutureAwait<T> implements DoneSupplier<T> {

    private Future<T> future;

    FutureAwait(Future<T> future) {
        this.future = future;
    }


    @Override
    public Done<T> getDone() {
        if (!future.isDone()) {
            return DoneResults.failure();
        } else {
            try {
                return DoneResults.success(future.get());
            } catch (ExecutionException e) {
                throw new StageException(e.getCause());
            } catch (InterruptedException e) {
                throw new StageException(e);
            }
        }
    }
}
