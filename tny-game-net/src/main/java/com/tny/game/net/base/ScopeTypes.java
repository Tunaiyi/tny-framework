package com.tny.game.net.base;

import com.tny.game.common.enums.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.utils.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class ScopeTypes extends ClassImporter {

    protected static EnumeratorHolder<ScopeType> holder = new EnumeratorHolder<ScopeType>() {

        @Override
        protected void postRegister(ScopeType object) {
            putAndCheck(object.getName(), object);
        }

    };

    static {
        loadClass(NetConfigs.NET_CONFIG, NetConfigs.BASE_SCOPE_TYPE_CLASS);
    }

    private ScopeTypes() {
    }

    static void register(ScopeType value) {
        holder.register(value);
    }

    public static <T extends ScopeType> T of(String name) {
        return holder.ofAndCheck(name, "获取 {} ScopeType 不存在", name);
    }

    public static <T extends ScopeType> T valueOfEnum(String enumName) {
        return holder.ofAndCheck(enumName, "获取 {} 的ScopeType 不存在", enumName);
    }

    public static Collection<ScopeType> getAll() {
        return holder.values();
    }

}
