package com.tny.game.common.result;

import java.util.Optional;
import java.util.function.*;

/**
 * 做完的结果
 *
 * @param <M>
 * @author KGTny
 */
public interface Done<M> {

    /**
     * @return 是否成功 code == ItemResultCode.SUCCESS
     */
    boolean isSuccess();

    /**
     * @return 是否有结果值呈现
     */
    default boolean isPresent() {
        return this.get() != null;
    }

    /**
     * @return 返回结果消息
     */
    String getMessage();

    /**
     * @return 获取返回结果
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
        if (object != null) {
            consumer.accept(object);
        }
    }

    Optional<M> optional();

    default void ifSuccess(Consumer<? super M> consumer) {
        if (this.isSuccess()) {
            consumer.accept(get());
        }
    }

    default boolean isFailure() {
        return !this.isSuccess();
    }

    default void ifFailure(Consumer<? super M> consumer) {
        if (!this.isSuccess()) {
            consumer.accept(get());
        }

    }

}
