package com.tny.game.basics.item;

import com.tny.game.basics.item.annotation.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class AnyIdConverter {

	private final EntityScheme scheme;

	private boolean single;

	private final String idHeader;

	public AnyIdConverter(EntityScheme scheme) {
		this.scheme = scheme;
		if (scheme.isHasPrefix()) {
			idHeader = scheme.prefix() + ":";
		} else {
			idHeader = "";
		}
		Class<?> entityClass = scheme.getEntityClass();
		SingleEntity single = entityClass.getAnnotation(SingleEntity.class);
		if (single != null) {
			this.single = single.value();
		} else {
			if (StuffOwner.class.isAssignableFrom(entityClass)) {
				this.single = true;
			} else if (Stuff.class.isAssignableFrom(entityClass)) {
				this.single = false;
			} else if (Item.class.isAssignableFrom(entityClass)) {
				this.single = true;
			} else {
				this.single = false;
			}
		}

	}

	public String anyId2Id(AnyId key) {
		if (single) {
			return idHeader + AnyId.formatId(key.getPlayerId(), 0);
		}
		return idHeader + key.toUuid();
	}

	public String any2Id(Any object) {
		if (single) {
			return idHeader + AnyId.formatId(object.getPlayerId(), 0);
		}
		return idHeader + AnyId.formatId(object);
	}

	public AnyId object2AnyId(Any object) {
		return AnyId.idOf(object.getPlayerId(), object.getId());
	}

}
