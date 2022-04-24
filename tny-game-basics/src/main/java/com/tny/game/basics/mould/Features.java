package com.tny.game.basics.mould;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Features extends ClassImporter {

    private static final EnumeratorHolder<Feature> holder = new EnumeratorHolder<>();

    //    static {
    //        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_FEATURE_CLASS);
    //    }

    private Features() {
    }

    public static void register(Feature value) {
        holder.register(value);
    }

    public static <T extends Feature> T check(String key) {
        return holder.check(key, "获取 {} Feature 不存在", key);
    }

    public static <T extends Feature> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 Feature 不存在", id);
    }

    public static <T extends Feature> T of(int id) {
        return holder.of(id);
    }

    public static <T extends Feature> T of(String key) {
        return holder.of(key);
    }

    public static <T extends Feature> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends Feature> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends Feature> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<Feature> enumerator() {
        return holder;
    }

}
