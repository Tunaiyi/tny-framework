package com.tny.game.common.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public interface Done<M> {

    /**
     * 是否成功 code == ItemResultCode.SUCCESS
     *
     * @return
     */
    boolean isSuccess();

    /**
     * 是否有结果值呈现
     *
     * @return
     */
    default boolean isPresent() {
        return this.get() != null;
    }

    /**
     * 获取返回结果
     *
     * @return
     */
    M get();

    default M orElse(M other) {
        M object = get();
        return object != null ? object : other;
    }

    default M orElseGet(Supplier<? extends M> other) {
        M object = get();
        return object != null ? object : other.get();
    }

    default <X extends Throwable> M orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        M object = get();
        if (object != null) {
            return object;
        } else {
            throw exceptionSupplier.get();
        }
    }

    default void ifPresent(Consumer<? super M> consumer) {
        M object = get();
        if (object != null)
            consumer.accept(object);
    }

    default void ifSuccess(Consumer<? super M> consumer) {
        if (this.isSuccess())
            consumer.accept(get());
    }

    default boolean isFailed() {
        return !this.isSuccess();
    }

    default void ifFailed(Consumer<? super M> consumer) {
        if (!this.isSuccess())
            consumer.accept(get());
    }

}
