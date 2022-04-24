package com.tny.game.net.loader;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.net.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 读取 tny-factory.properties 配置中
 * com.tny.game.loader.EnumLoader 配置相关的枚举会提前读取
 * Created by Kun Yang on 16/9/9.
 */
public final class RpcControllerLoader {

    private static final Set<Class<?>> CLASSES = new ConcurrentHashSet<>();

    private RpcControllerLoader() {
    }

    @ClassSelectorProvider
    public static ClassSelector controllerSelector() {
        return ClassSelector.create()
                .addFilter(AnnotationClassFilter.ofInclude(RpcController.class))
                .setHandler((classes) -> classes.stream()
                        .filter((c) -> !c.isInterface() && !Modifier.isAbstract(c.getModifiers()))
                        .forEach(CLASSES::add)
                );
    }

    public static Set<Class<?>> getControllerClasses() {
        return Collections.unmodifiableSet(CLASSES);
    }

}
