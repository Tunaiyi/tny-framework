package com.tny.game.namespace.consistenthash;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/20 16:53
 **/
public interface HashAlgorithm {

    long hash(String key, int seed);

    long getMax();

}
