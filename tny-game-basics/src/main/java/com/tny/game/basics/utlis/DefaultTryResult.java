package com.tny.game.basics.utlis;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.result.*;

import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/7/29.
 */
class DefaultTryResult<M> extends BaseDoneResult<M, DefaultTryResult<M>> implements TryResult<M> {

    private TryToDoResult tryResult;

    protected DefaultTryResult(ResultCode code, M returnValue) {
        super(code, returnValue);
    }

    protected DefaultTryResult(TryToDoResult tryResult, M returnValue) {
        super(tryResult.getResultCode(), returnValue);
        this.tryResult = tryResult;
    }

    @Override
    public void ifFailedToTry(Consumer<DefaultTryResult<M>> consumer) {
        if (!this.isSuccess()) {
            consumer.accept(this);
        }
    }

    @Override
    public void ifSucceedToTry(Consumer<DefaultTryResult<M>> consumer) {
        if (this.isSuccess()) {
            consumer.accept(this);
        }
    }

    @Override
    public boolean isFailedToTry() {
        return super.isFailure() && this.tryResult != null;
    }

    @Override
    public TryToDoResult getResult() {
        return this.tryResult;
    }

}
