package com.tny.game.namespace;

/**
 * 基础 Hash 计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public abstract class BaseHasher<T> implements Hasher<T> {

    protected abstract long toHash(T value, int seed);

    @Override
    public long hash(T value, int seed) {
        return Math.abs(toHash(value, seed));
    }

}
