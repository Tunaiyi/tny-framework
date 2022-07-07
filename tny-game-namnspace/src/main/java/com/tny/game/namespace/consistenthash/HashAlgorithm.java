package com.tny.game.namespace.consistenthash;

/**
 * 哈希算法
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:29
 **/
public interface HashAlgorithm {

    long hash(String key, int seed);

    long getMax();

}
