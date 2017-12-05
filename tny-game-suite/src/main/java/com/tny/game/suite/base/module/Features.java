package com.tny.game.suite.base.module;

import com.tny.game.base.module.Feature;
import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.common.utils.ClassImporter;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Features extends ClassImporter {

    protected static EnumeratorHolder<Feature> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_FEATURE_CLASS);
    }

    private Features() {
    }

    static void register(Feature value) {
        holder.register(value);
    }

    public static <T extends Feature> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} Feature 不存在", key);
    }

    public static <T extends Feature> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 Feature 不存在", id);
    }

    public static <T extends Feature> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends Feature> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<Feature> values() {
        return holder.values();
    }

}
