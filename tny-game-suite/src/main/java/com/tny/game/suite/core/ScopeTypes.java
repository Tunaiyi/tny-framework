package com.tny.game.suite.core;

import com.tny.game.common.enums.EnumeratorHolder;
import com.tny.game.suite.base.AutoImport;
import com.tny.game.suite.utils.Configs;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class ScopeTypes extends AutoImport {

    protected static EnumeratorHolder<ScopeType> holder = new EnumeratorHolder<ScopeType>() {

        @Override
        protected void postRegister(ScopeType object) {
            putAndCheck(object.getName(), object);
        }

    };

    static {
        loadClass(Configs.SUITE_SCOPE_TYPE_CLASS);
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
