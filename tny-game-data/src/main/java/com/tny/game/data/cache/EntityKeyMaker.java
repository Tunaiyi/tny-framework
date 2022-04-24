package com.tny.game.data.cache;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 12:19 下午
 */
public interface EntityKeyMaker<K extends Comparable<?>, O> {

    K make(O object);

    Class<K> getKeyClass();

}
