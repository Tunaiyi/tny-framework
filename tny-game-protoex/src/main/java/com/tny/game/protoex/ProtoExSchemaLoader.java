/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.protoex;

import com.tny.game.common.runtime.*;
import com.tny.game.protoex.annotations.*;
import com.tny.game.protoex.field.runtime.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public final class ProtoExSchemaLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger("proto");

    private static ClassSelector selector = ClassSelector
            .create(AnnotationClassFilter.ofInclude(ProtoEx.class))
            .setHandler(ProtoExSchemaLoader::loadClasses);

    @ClassSelectorProvider
    protected static ClassSelector selector() {
        return selector;
    }

    private ProtoExSchemaLoader() {
    }

    private static void loadClasses(Collection<Class<?>> classes) {
        LOGGER.info("启动初始化 ProtoSchema 任务!");
        // forkJoinTask = ForkJoinPool.commonPool().submit(() -> {
        Class<?> clazz;
        RunChecker.trace(ProtoExSchemaLoader.class);
        LOGGER.info("开始初始化 ProtoSchema .......");
        Map<Integer, Class<?>> classMap = new HashMap<>();
        for (Class<?> cl : classes) {
            clazz = cl;
            ProtoExSchema<?> schema = RuntimeProtoExSchema.getProtoSchema(clazz);
            if (schema == null) {
                throw new NullPointerException(format("{} 找不到对应的schema", clazz));
            }
            Class<?> old = classMap.put(schema.getProtoExId(), clazz);
            if (old != null) {
                throw new IllegalArgumentException(format("{} protoID 与 {} protoID 都为 {}", clazz, old, schema.getProtoExId()));
            }
        }
        LOGGER.info("开始初始化 ProtoSchema 完成! 耗时 {} ms", RunChecker.end(ProtoExSchemaLoader.class).costMicroTime());
        selector.clear();
    }

}
