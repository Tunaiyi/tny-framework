package com.tny.game.basics.mould;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class Moulds extends ClassImporter {

    private static final EnumeratorHolder<Mould> holder = new EnumeratorHolder<>();

    private Moulds() {
    }

    public static void register(Mould value) {
        holder.register(value);
    }

    public static <T extends Mould> T check(String key) {
        return holder.check(key, "获取 {} Mould 不存在", key);
    }

    public static <T extends Mould> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 Mould 不存在", id);
    }

    public static <T extends Mould> T of(int id) {
        return holder.of(id);
    }

    public static <T extends Mould> T of(String key) {
        return holder.of(key);
    }

    public static <T extends Mould> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends Mould> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends Mould> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<Mould> enumerator() {
        return holder;
    }

}
