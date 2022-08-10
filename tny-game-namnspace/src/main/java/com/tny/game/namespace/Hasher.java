package com.tny.game.namespace;

/**
 * Hash计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public interface Hasher<T> {

    default long hash(T value, int seed, long max) {
        var code = Math.abs(hash(value, seed));
        if (max > 0L) {
            return code % max;
        }
        return code;
    }

    long hash(T value, int seed);

}
