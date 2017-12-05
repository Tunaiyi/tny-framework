package com.tny.game.common.utils.module;

import com.tny.game.base.module.Module;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.common.utils.ClassImporter;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Modules extends ClassImporter {

    protected static EnumeratorHolder<Module> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_MODULE_CLASS);
    }

    private Modules() {
    }

    static void register(Module value) {
        holder.register(value);
    }

    public static <T extends Module> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} Module 不存在", key);
    }

    public static <T extends Module> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 Module 不存在", id);
    }

    public static <T extends Module> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends Module> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<Module> values() {
        return holder.values();
    }

}
