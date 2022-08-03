package com.tny.game.namespace;

/**
 * 基础 Hash 计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public abstract class BaseHasher<T> implements Hasher<T> {

    private final long maxSlot;

    private static final long MAX = -1L >>> 32;

    public BaseHasher() {
        this.maxSlot = MAX;
    }

    public BaseHasher(long maxSlot) {
        this.maxSlot = maxSlot;
    }

    protected abstract long toHash(T value, int seed);

    @Override
    public long hash(T value, int seed) {
        long hashCode = Math.abs(toHash(value, seed));
        if (maxSlot < 0) {
            return hashCode;
        }
        return hashCode % maxSlot;
    }

    @Override
    public long getMax() {
        return maxSlot;
    }

}
