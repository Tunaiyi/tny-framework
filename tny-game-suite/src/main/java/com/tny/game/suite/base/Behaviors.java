package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.*;
import com.tny.game.common.config.*;
import com.tny.game.common.enums.*;
import com.tny.game.suite.utils.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Behaviors extends ClassImporter {

    protected static EnumeratorHolder<Behavior> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_BEHAVIOR_CLASS);
    }

    private Behaviors() {
    }

    static void register(Behavior value) {
        holder.register(value);
    }

    public static <T extends Behavior> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} Behavior 不存在", key);
    }

    public static <T extends Behavior> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 Behavior 不存在", id);
    }

    public static <T extends Behavior> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends Behavior> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<Behavior> getAll() {
        return holder.values();
    }

}
