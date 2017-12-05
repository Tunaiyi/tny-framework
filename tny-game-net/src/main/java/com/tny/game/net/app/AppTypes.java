package com.tny.game.net.app;

import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.base.ClassImporter;
import com.tny.game.net.utils.NetConfigs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class AppTypes extends ClassImporter {

    protected static EnumeratorHolder<AppType> holder = new EnumeratorHolder<AppType>() {

        @Override
        protected void postRegister(AppType object) {
            putAndCheck(object.getName(), object);
        }

    };

    static {
        loadClass(NetConfigs.NET_CONFIG, NetConfigs.SERVER_APP_TYPE);
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
