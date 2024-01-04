/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.number;

import java.util.function.*;

/**
 * 本地变量
 * Created by Kun Yang on 16/2/21.
 */
public class LocalBoolean {

    private boolean value;

    public LocalBoolean() {
        this.set(false);
    }

    public LocalBoolean(boolean value) {
        this.value = value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return this.value;
    }

    public void beTrue() {
        this.value = true;
    }

    public void beFalse() {
        this.value = false;
    }

    public LocalBoolean ifTrue(Consumer<LocalBoolean> consumer) {
        if (this.value) {
            consumer.accept(this);
        }
        return this;
    }

    public LocalBoolean ifFalse(Consumer<LocalBoolean> consumer) {
        if (!this.value) {
            consumer.accept(this);
        }
        return this;
    }

    public LocalBoolean trueIf(boolean condition) {
        if (condition) {
            this.value = true;
        }
        return this;
    }

    public LocalBoolean trueIf(BooleanSupplier condition) {
        return trueIf(condition.getAsBoolean());
    }

    public LocalBoolean falseIf(boolean condition) {
        if (condition) {
            this.value = false;
        }
        return this;
    }

    public LocalBoolean falseIf(BooleanSupplier condition) {
        return falseIf(condition.getAsBoolean());
    }

}
