package com.tny.game.basics.item.capacity;

import com.tny.game.common.utils.*;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class CapacityUsages {

	private static final ConcurrentMap<String, CapacityUsage> NAME_TYPE_MAP = new ConcurrentHashMap<>();

	public static <T> CapacityUsage usageOf(String name, T defaultNumber, BiFunction<T, T, T> aggregation) {
		CapacityUsage type = new DefaultCapacityUsage<>(name, defaultNumber, aggregation);
		CapacityUsage old = NAME_TYPE_MAP.putIfAbsent(type.name(), type);
		if (old != null) {
			throw new IllegalArgumentException(StringAide.format("创建{}失败, {} : {} 已存在", type, name, old));
		}
		return type;
	}

	public static Collection<CapacityUsage> all() {
		return NAME_TYPE_MAP.values();
	}

}
