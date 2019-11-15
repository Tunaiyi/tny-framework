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

    public LocalBoolean ifTrue(Consumer<LocalBoolean> consumer) {
        if (this.value)
            consumer.accept(this);
        return this;
    }

    public LocalBoolean ifFalse(Consumer<LocalBoolean> consumer) {
        if (!this.value)
            consumer.accept(this);
        return this;
    }


    public LocalBoolean trueIf(boolean condition) {
        if (condition)
            this.value = true;
        return this;
    }

    public LocalBoolean trueIf(BooleanSupplier condition) {
        return trueIf(condition.getAsBoolean());
    }

    public LocalBoolean falseIf(boolean condition) {
        if (condition)
            this.value = false;
        return this;
    }

    public LocalBoolean falseIf(BooleanSupplier condition) {
        return falseIf(condition.getAsBoolean());
    }


}
