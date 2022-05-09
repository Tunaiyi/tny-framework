package com.tny.game.net.base;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class AppScopes extends ClassImporter {

    public static final EnumerableSymbol<AppScope, String> SCOPE_NAME_SYMBOL = EnumerableSymbol.symbolOf(
            AppScope.class, "scopeName", AppScope::getScopeName);

    private static final EnumeratorHolder<AppScope> holder = new EnumeratorHolder<AppScope>() {

        @Override
        protected void postRegister(AppScope object) {
            putAndCheckSymbol(SCOPE_NAME_SYMBOL, object);
        }

    };

    private AppScopes() {
    }

    static void register(AppScope value) {
        holder.register(value);
    }

    public static <T extends AppScope> T of(String name) {
        return holder.check(name, "获取 {} ScopeType 不存在", name);
    }

    public static <T extends AppScope> T ofScopeName(String name) {
        return holder.checkBySymbol(SCOPE_NAME_SYMBOL, "获取 {} ServerType不存在", name);
    }

    public static <T extends AppScope> T valueOfEnum(String enumName) {
        return holder.check(enumName, "获取 {} 的ScopeType 不存在", enumName);
    }

    public static Collection<AppScope> getAll() {
        return holder.allValues();
    }

}
