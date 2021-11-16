package com.tny.game.basics.module;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class FeatureOpenModes extends ClassImporter {

	private static final EnumeratorHolder<FeatureOpenMode<?>> holder = new EnumeratorHolder<>();

	private FeatureOpenModes() {
	}

	public static void register(FeatureOpenMode<?> value) {
		holder.register(value);
	}

	public static <T extends FeatureOpenMode<?>> T check(String key) {
		return holder.check(key, "获取 {} OpenMode 不存在", key);
	}

	public static <T extends FeatureOpenMode<?>> T check(int id) {
		return holder.check(id, "获取 ID为 {} 的 OpenMode 不存在", id);
	}

	public static <T extends FeatureOpenMode<?>> T of(int id) {
		return holder.of(id);
	}

	public static <T extends FeatureOpenMode<?>> T of(String key) {
		return holder.of(key);
	}

	public static <T extends FeatureOpenMode<?>> Optional<T> option(int id) {
		return holder.option(id);
	}

	public static <T extends FeatureOpenMode<?>> Optional<T> option(String key) {
		return holder.option(key);
	}

	public static <T extends FeatureOpenMode<?>> Collection<T> all() {
		return holder.allValues();
	}

	public static Enumerator<FeatureOpenMode<?>> enumerator() {
		return holder;
	}

}
