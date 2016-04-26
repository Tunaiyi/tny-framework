package com.tny.game.common.utils;

import java.util.function.Supplier;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public abstract class Done<M> {

    /**
     * 是否成功 code == ItemResultCode.SUCCESS
     *
     * @return
     */
    public abstract boolean isSuccess();

    /**
     * 是否有结果值呈现
     *
     * @return
     */
    public boolean isPresent() {
        return this.get() != null;
    }

    /**
     * 获取返回结果
     *
     * @return
     */
    public abstract M get();

    public M orElse(M other) {
        M object = get();
        return object != null ? object : other;
    }

    public M orElseGet(Supplier<? extends M> other) {
        M object = get();
        return object != null ? object : other.get();
    }

}
