package com.tny.game.data;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/20 1:58 下午
 */
public interface EntityOnLoad<K, E> {

    void onLoad(K key, E entity);

}
