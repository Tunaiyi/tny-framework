package com.tny.game.net.base;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;

import java.util.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class RpcServiceTypes extends ClassImporter {

    public static final EnumerableSymbol<RpcServiceType, String> SERVICE_SYMBOL = EnumerableSymbol
            .symbolOf(RpcServiceType.class, "service", RpcServiceType::getService);

    protected static EnumeratorHolder<RpcServiceType> holder = new EnumeratorHolder<RpcServiceType>() {

        @Override
        protected void postRegister(RpcServiceType object) {
            super.postRegister(object);
            putAndCheckSymbol(SERVICE_SYMBOL, object);
        }

    };

    private RpcServiceTypes() {
    }

    static void register(RpcServiceType value) {
        holder.register(value);
    }

    public static <T extends RpcServiceType> T check(String key) {
        return holder.check(key, "获取 {} RpcAccessType 不存在", key);
    }

    public static <T extends RpcServiceType> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 RpcAccessType 不存在", id);
    }

    public static <T extends RpcServiceType> T checkService(String service) {
        return holder.checkBySymbol(SERVICE_SYMBOL, service, "获取 service 为 {} 的 RpcAccessType 不存在", service);
    }

    public static <T extends RpcServiceType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends RpcServiceType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends RpcServiceType> T ofService(String service) {
        return holder.ofBySymbol(SERVICE_SYMBOL, service);
    }

    public static <T extends RpcServiceType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends RpcServiceType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends RpcServiceType> Optional<T> optionService(String service) {
        return holder.optionBySymbol(SERVICE_SYMBOL, service);
    }

    public static <T extends RpcServiceType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<RpcServiceType> enumerator() {
        return holder;
    }

}
