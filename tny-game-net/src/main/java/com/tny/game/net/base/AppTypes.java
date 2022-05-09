package com.tny.game.net.base;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/1/29.
 */
public final class AppTypes extends ClassImporter {

    private static final EnumerableSymbol<AppType, String> APP_NAME_SYMBOL = EnumerableSymbol.symbolOf(
            AppType.class, "appName", AppType::getAppName);

    private static EnumeratorHolder<AppType> holder = new EnumeratorHolder<AppType>() {

        @Override
        protected void postRegister(AppType object) {
            putAndCheckSymbol(APP_NAME_SYMBOL, object);
        }

    };

    private AppTypes() {
    }

    static void register(AppType value) {
        holder.register(value);
    }

    public static <T extends AppType> T of(String name) {
        return holder.check(name, "获取 {} AppType 不存在", name);
    }

    public static <T extends AppType> T ofAppName(String name) {
        return holder.checkBySymbol(APP_NAME_SYMBOL, "获取 {} AppType 不存在", name);
    }

    public static <T extends AppType> T valueOfEnum(String enumName) {
        return holder.check(enumName, "获取 {} AppType 不存在", enumName);
    }

    public static Collection<AppType> getAll() {
        return holder.allValues();
    }

}
