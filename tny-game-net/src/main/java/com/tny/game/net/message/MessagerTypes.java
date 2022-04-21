package com.tny.game.net.message;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class MessagerTypes extends ClassImporter {

    protected static EnumeratorHolder<MessagerType> holder = new EnumeratorHolder<MessagerType>();

    private MessagerTypes() {
    }

    static void register(MessagerType value) {
        holder.register(value);
    }

    public static <T extends MessagerType> T check(String key) {
        return holder.check(key, "获取 {} MessagerType 不存在", key);
    }

    public static <T extends MessagerType> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 MessagerType 不存在", id);
    }

    public static <T extends MessagerType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends MessagerType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends MessagerType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends MessagerType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends MessagerType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<MessagerType> enumerator() {
        return holder;
    }

}
