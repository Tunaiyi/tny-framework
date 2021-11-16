package com.tny.game.basics.module;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Modulers extends ClassImporter {

	protected static EnumeratorHolder<Moduler> holder = new EnumeratorHolder<>();

	//    static {
	//        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_MODULE_CLASS);
	//    }

	private Modulers() {
	}

	public static void register(Moduler value) {
		holder.register(value);
	}

	public static <T extends Moduler> T check(String key) {
		return holder.check(key, "获取 {} Module 不存在", key);
	}

	public static <T extends Moduler> T check(int id) {
		return holder.check(id, "获取 ID为 {} 的 Module 不存在", id);
	}

	public static <T extends Moduler> T of(int id) {
		return holder.of(id);
	}

	public static <T extends Moduler> T of(String key) {
		return holder.of(key);
	}

	public static <T extends Moduler> Optional<T> option(int id) {
		return holder.option(id);
	}

	public static <T extends Moduler> Optional<T> option(String key) {
		return holder.option(key);
	}

	public static <T extends Moduler> Collection<T> all() {
		return holder.allValues();
	}

	public static Enumerator<Moduler> enumerator() {
		return holder;
	}

}
