package com.tny.game.base.module;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Modules extends ClassImporter {

    protected static EnumeratorHolder<Module> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_MODULE_CLASS);
    //    }

    private Modules() {
    }

    public static void register(Module value) {
        holder.register(value);
    }

    public static <T extends Module> T check(String key) {
        return holder.check(key, "获取 {} Module 不存在", key);
    }

    public static <T extends Module> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 Module 不存在", id);
    }

    public static <T extends Module> T of(int id) {
        return holder.of(id);
    }

    public static <T extends Module> T of(String key) {
        return holder.of(key);
    }

    public static <T extends Module> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends Module> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends Module> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<Module> enumerator() {
        return holder;
    }

}
