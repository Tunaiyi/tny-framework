package com.tny.game.namespace;

import com.tny.game.namespace.consistenthash.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/9 04:31
 **/
@FunctionalInterface
public interface Hasher<T> {

    String toNodeId(T value);

    default String toHashKey(T value) {
        return toNodeId(value);
    }

    default long hash(HashAlgorithm algorithm, T value) {
        return algorithm.hash(toHashKey(value), 0);
    }

}
