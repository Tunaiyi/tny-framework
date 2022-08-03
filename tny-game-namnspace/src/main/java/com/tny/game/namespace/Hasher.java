package com.tny.game.namespace;

/**
 * Hash计算器
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
public interface Hasher<T> {

    long hash(T value, int seed);

    long getMax();

}
