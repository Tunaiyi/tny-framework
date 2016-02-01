package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Actions extends AutoImport {

    protected static EnumeratorHolder<Action> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_BASE_ACTION_CLASS);
    }

    private Actions() {
    }

    static void register(Action value) {
        holder.register(value);
    }

    public static <T extends Action> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} Action 不存在", key);
    }

    public static <T extends Action> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 Action 不存在", id);
    }

    public static Collection<Action> getAll() {
        return holder.values();
    }

}
