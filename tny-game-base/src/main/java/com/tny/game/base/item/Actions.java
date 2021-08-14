package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Actions extends ClassImporter {

    protected static EnumeratorHolder<Action> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_ACTION_CLASS);
    //    }

    private Actions() {
    }

    static void register(Action value) {
        holder.register(value);
    }

    public static <T extends Action> T check(String key) {
        return holder.check(key, "获取 {} Action 不存在", key);
    }

    public static <T extends Action> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 Action 不存在", id);
    }

    public static <T extends Action> T of(int id) {
        return holder.of(id);
    }

    public static <T extends Action> T of(String key) {
        return holder.of(key);
    }

    public static <T extends Action> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends Action> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static Collection<Action> getAll() {
        return holder.allValues();
    }

    public static Enumerator<Action> enumerator() {
        return holder;
    }

}