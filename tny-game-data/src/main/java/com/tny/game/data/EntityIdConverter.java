package com.tny.game.data;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:58 下午
 */
public interface EntityIdConverter<K extends Comparable<?>, O, ID> {

	ID keyToId(K key);

	ID entityToId(O object);

}
