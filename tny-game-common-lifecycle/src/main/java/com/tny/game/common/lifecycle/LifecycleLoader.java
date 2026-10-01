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

package com.tny.game.common.lifecycle;

import com.tny.game.common.lifecycle.annotation.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * <p>
 */
public final class LifecycleLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleLoader.class);


    private static final Set<StaticInitiator> INITIATORS = new ConcurrentSkipListSet<>();

    private static final ClassSelector SELECTOR = ClassSelector.create()
            .addFilter(AnnotationClassFilter.ofInclude(AsLifecycle.class))
            .setHandler(LifecycleLoader::registerAll);

    /**
     * 扫描注册入口：单类缺注解/缺初始化方法不再终止整轮（原 forEach 一个坏类炸全表）——
     * 按类隔离，失败类以一条汇总告警显式留痕。
     */
    static void registerAll(Iterable<Class<?>> classes) {
        List<Class<?>> failed = new ArrayList<>();
        List<Throwable> causes = new ArrayList<>();
        for (Class<?> clazz : classes) {
            try {
                register(clazz);
            } catch (Throwable e) {
                failed.add(clazz);
                causes.add(e);
            }
        }
        if (!failed.isEmpty()) {
            LOGGER.error("生命周期扫描注册完成但 {} 个类被跳过: {} | 首个原因: {}",
                    failed.size(), failed, causes.get(0).toString(), causes.get(0));
        }
    }

    @ClassSelectorProvider
    private static ClassSelector selector() {
        return SELECTOR;
    }

    private LifecycleLoader() {
    }

    public static void register(Class<?> clazz) {
        StaticInitiator initiator = StaticInitiator.instance(clazz);
        INITIATORS.add(initiator);
    }

    public static Set<StaticInitiator> getStaticInitiators() {
        return Collections.unmodifiableSet(INITIATORS);
    }

    /**
     * 清空扫描登记（仅限测试基座复位全局态；生产启动路径单次装配不调用）。
     */
    public static void reset() {
        INITIATORS.clear();
    }

}
