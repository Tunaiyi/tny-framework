package com.tny.game.net.rpc.loader;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.net.rpc.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.util.*;

/**
 * 读取 tny-factory.properties 配置中
 * com.tny.game.loader.EnumLoader 配置相关的枚举会提前读取
 * Created by Kun Yang on 16/9/9.
 */
public final class RpcServiceLoader {

    private static final Set<Class<?>> CLASSES = new ConcurrentHashSet<>();

    private RpcServiceLoader() {
    }

    @ClassSelectorProvider
    public static ClassSelector serviceSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(RpcRemoteService.class))
                .setHandler((classes) -> classes.stream()
                        .filter(Class::isInterface)
                        .forEach(CLASSES::add)
                );
    }

    public static Set<Class<?>> getServiceClasses() {
        return Collections.unmodifiableSet(CLASSES);
    }

}
