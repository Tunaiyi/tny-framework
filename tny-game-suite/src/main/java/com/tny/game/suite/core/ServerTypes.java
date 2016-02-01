package com.tny.game.suite.core;

import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.base.AutoImport;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class ServerTypes extends AutoImport {

    protected static EnumeratorHolder<ServerType> holder = new EnumeratorHolder<ServerType>() {

        @Override
        protected void postRegister(ServerType object) {
            putAndCheck(object.getName(), object);
        }

    };

    static {
        loadClass(Configs.SUITE_SERVER_TYPE_CLASS);
    }

    private ServerTypes() {
    }

    static void register(ServerType value) {
        holder.register(value);
    }

    public static <T extends ServerType> T of(String name) {
        return holder.ofAndCheck(name, "获取 {} ServerType不存在", name);
    }

    public static <T extends ServerType> T valueOfEnum(String enumName) {
        return holder.ofAndCheck(enumName, "获取 {} 的ServerType不存在", enumName);
    }


    public static Collection<ServerType> getAll() {
        return holder.values();
    }

}
