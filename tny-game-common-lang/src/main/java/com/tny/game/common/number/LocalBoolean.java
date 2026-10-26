/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
