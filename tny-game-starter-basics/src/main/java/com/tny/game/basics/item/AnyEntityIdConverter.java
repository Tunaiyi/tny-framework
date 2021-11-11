package com.tny.game.basics.item;

import com.tny.game.data.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class AnyEntityIdConverter implements EntityIdConverter<AnyUnid, Any, String> {

	private final String head;

	public AnyEntityIdConverter(EntityScheme scheme) {
		if (scheme.isHasPrefix()) {
			head = scheme.prefix() + ":";
		} else {
			head = "";
		}
	}

	@Override
	public String keyToId(AnyUnid key) {
		return head + key.toUuid();
	}

	@Override
	public String entityToId(Any object) {
		return head + AnyUnid.formatUuid(object);
	}

}
