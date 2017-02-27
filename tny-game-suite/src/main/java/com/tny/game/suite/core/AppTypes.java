package com.tny.game.suite.core;

import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.base.AutoImport;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class AppTypes extends AutoImport {

    protected static EnumeratorHolder<AppType> holder = new EnumeratorHolder<AppType>() {

        @Override
        protected void postRegister(AppType object) {
            putAndCheck(object.getName(), object);
        }

    };

    static {
        loadClass(Configs.SUITE_SERVER_TYPE_CLASS);
    }

    private AppTypes() {
    }

    static void register(AppType value) {
        holder.register(value);
    }

    public static <T extends AppType> T of(String name) {
        return holder.ofAndCheck(name, "获取 {} ServerType不存在", name);
    }

    public static <T extends AppType> T valueOfEnum(String enumName) {
        return holder.ofAndCheck(enumName, "获取 {} 的ServerType不存在", enumName);
    }


    public static Collection<AppType> getAll() {
        return holder.values();
    }

}