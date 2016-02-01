package com.tny.game.suite.base;

import com.tny.game.base.item.Ability;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Abilities extends AutoImport {

    protected static EnumeratorHolder<Ability> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_BASE_ABILITY_CLASS);
    }

    private Abilities() {
    }

    static void register(Ability value) {
        holder.register(value);
    }

    public static <T extends Ability> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} Ability 不存在", key);
    }

    public static <T extends Ability> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 Ability 不存在", id);
    }

    public static Collection<Ability> getAll() {
        return holder.values();
    }

}
