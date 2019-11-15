package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.utils.*;
import com.tny.game.suite.utils.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class OpenModes extends ClassImporter {

    protected static EnumeratorHolder<OpenMode> holder = new EnumeratorHolder<>();

    static {
        loadClass(Configs.SUITE_CONFIG, Configs.SUITE_BASE_OPEN_MODE_CLASS);
    }

    private OpenModes() {
    }

    static void register(OpenMode value) {
        holder.register(value);
    }

    public static <T extends OpenMode> T of(String key) {
        return holder.ofAndCheck(key, "获取 {} OpenMode 不存在", key);
    }

    public static <T extends OpenMode> T of(int id) {
        return holder.ofAndCheck(id, "获取 ID为 {} 的 OpenMode 不存在", id);
    }

    public static <T extends OpenMode> T ofUncheck(int id) {
        return holder.of(id);
    }

    public static <T extends OpenMode> T ofUncheck(String key) {
        return holder.of(key);
    }

    public static Collection<OpenMode> getAll() {
        return holder.values();
    }

}
