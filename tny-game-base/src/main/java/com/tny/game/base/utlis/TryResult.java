package com.tny.game.base.utlis;

import com.tny.game.base.item.behavior.TryToDoResult;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.utils.DoneResult;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/7/29.
 */
public class TryResult<M> extends DoneResult<M> {

    private TryToDoResult tryResult;

    protected TryResult(M returnValue, ResultCode code) {
        super(returnValue, code);
    }

    protected TryResult(M returnValue, TryToDoResult tryResult) {
        super(returnValue, tryResult.getResultCode());
        this.tryResult = tryResult;
    }

    public void ifTryFailed(Consumer<TryResult<M>> consumer) {
        if (!this.isSuccess())
            consumer.accept(this);
    }

    public void ifTrySuccess(Consumer<TryResult<M>> consumer) {
        if (this.isSuccess())
            consumer.accept(this);
    }

    public boolean isTryFailed() {
        return super.isFailed() && this.tryResult != null;
    }

    public TryToDoResult getResult() {
        return tryResult;
    }


}
