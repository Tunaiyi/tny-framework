/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.boot.launcher;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.scanner.*;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.*;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 */
public class ApplicationLauncherContext {

    public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncherContext.class);

    private static final ApplicationLifecycleProcessor processor = new ApplicationLifecycleProcessor();

    private static final AtomicBoolean staticInit = new AtomicBoolean(false);

    private static final AtomicBoolean start = new AtomicBoolean(false);

    private static final AtomicBoolean started = new AtomicBoolean(false);

    private static final AtomicBoolean closed = new AtomicBoolean(false);

    private static Set<String> basePackages = ImmutableSet.of();

    public static void register(Class<?> applicationClass) {
        ClassLoader loader = applicationClass.getClassLoader();
        Set<String> scanBasePackages = new HashSet<>();
        SpringBootApplication application = applicationClass.getAnnotation(SpringBootApplication.class);
        if (application != null) {
            String[] packages = application.scanBasePackages();
            CollectionUtils.addAll(scanBasePackages, packages);
        }
        ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
        if (componentScan != null) {
            String[] packages = componentScan.value();
            CollectionUtils.addAll(scanBasePackages, packages);
            packages = componentScan.basePackages();
            CollectionUtils.addAll(scanBasePackages, packages);
        }
        if (staticInit.compareAndSet(false, true)) {
            basePackages = ImmutableSet.copyOf(scanBasePackages);
            ClassMetadataReaderFactory.init(loader);
            ExeAide.runUnchecked(() -> processor.onStaticInit(loader, basePackages));
        }
    }

    public static Set<String> getBasePackages() {
        return basePackages;
    }

    public static void staticInit(String... paths) {
        if (staticInit.compareAndSet(false, true)) {
            ExeAide.runUnchecked(() -> processor.onStaticInit(ApplicationLauncherContext.class.getClassLoader(), Arrays.asList(paths)));
        }
    }

    public static void prepareStart(ApplicationContext context) {
        if (start.compareAndSet(false, true)) {
            ApplicationLifecycleProcessor.loadHandler(context);
            // processor.setApplicationContext(this.appContext);
            try {
                processor.onPrepareStart(false);
            } catch (Throwable throwable) {
                LOGGER.error("System exit -1 | {} processor exec exception", processor.getClass(), throwable);
                ((ConfigurableApplicationContext) context).close();
                //                throw new IllegalArgumentException(throwable);
                //                System.exit(1);
            }
        }
    }

    public static void postStart(ApplicationContext context) {
        if (started.compareAndSet(false, true)) {
            ApplicationLifecycleProcessor.loadHandler(context);
            try {
                processor.onPostStart(false);
            } catch (Throwable throwable) {
                LOGGER.error("System exit -1 | {} processor exec exception", processor.getClass(), throwable);
                ((ConfigurableApplicationContext) context).close();
            }
        }
    }

    public static void close() {
        if (closed.compareAndSet(false, true)) {
            ExeAide.runUnchecked(() -> processor.onClosed(true));
        }
    }

}
