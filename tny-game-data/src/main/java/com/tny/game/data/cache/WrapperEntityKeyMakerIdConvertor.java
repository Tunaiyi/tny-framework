package com.tny.game.data.cache;

import com.tny.game.data.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/15 8:32 上午
 */
public class WrapperEntityKeyMakerIdConvertor<K extends Comparable<?>, O> implements EntityIdConverter<K, O, K> {

	private final EntityScheme scheme;

	private final EntityKeyMaker<K, O> keyMaker;

	public static <K extends Comparable<?>, O> EntityIdConverter<K, O, K> wrapper(EntityScheme scheme, EntityKeyMaker<K, O> keyMaker) {
		return new WrapperEntityKeyMakerIdConvertor<>(scheme, keyMaker);
	}

	private WrapperEntityKeyMakerIdConvertor(EntityScheme scheme, EntityKeyMaker<K, O> keyMaker) {
		this.scheme = scheme;
		this.keyMaker = keyMaker;
	}

	@Override
	public K keyToId(K key) {
		return key;
	}

	@Override
	public K entityToId(O object) {
		return keyMaker.make(scheme, object);
	}

}
