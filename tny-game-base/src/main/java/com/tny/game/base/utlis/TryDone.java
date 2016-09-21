package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.DoneResult;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/7/29.
 */
public class TryDone<M> extends DoneResult<M> {

    private TryToDoResult tryResult;

    protected TryDone(M returnValue, ResultCode code) {
        super(returnValue, code);
    }

    protected TryDone(M returnValue, TryToDoResult tryResult) {
        super(returnValue, tryResult.getResultCode());
        this.tryResult = tryResult;
    }

    public void ifFailedTry(Consumer<TryDone<M>> consumer) {
        if (!this.isSuccess())
            consumer.accept(this);
    }

    public void ifResult(Consumer<TryDone<M>> consumer) {
        consumer.accept(this);
    }

    public boolean isTryFailed() {
        return this.tryResult != null;
    }

    public TryToDoResult getTryResult() {
        return tryResult;
    }


}
