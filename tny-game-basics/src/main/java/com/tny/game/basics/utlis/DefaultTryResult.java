/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
