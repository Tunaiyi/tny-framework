/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.scheduler;

import com.tny.game.common.enums.*;
import com.tny.game.common.io.config.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 服务器工具栏
 * Created by Kun Yang on 16/1/27.
 */
public class TaskReceiverTypes extends ClassImporter {

    private static EnumeratorHolder<TaskReceiverType> holder = new EnumeratorHolder<TaskReceiverType>() {

    };

    private TaskReceiverTypes() {
    }

    static {
        holder.register(DefaultTaskReceiverType.values());
    }

    static void register(TaskReceiverType value) {
        holder.register(value);
    }


    public static <T extends TaskReceiverType> T check(String key) {
        return holder.check(key, "获取 {} TaskReceiverType 不存在", key);
    }

    public static <T extends TaskReceiverType> T check(int id) {
        return holder.check(id, "获取 ID为 {} 的 TaskReceiverType 不存在", id);
    }

    public static <T extends TaskReceiverType> T of(int id) {
        return holder.of(id);
    }

    public static <T extends TaskReceiverType> T of(String key) {
        return holder.of(key);
    }

    public static <T extends TaskReceiverType> Optional<T> option(int id) {
        return holder.option(id);
    }

    public static <T extends TaskReceiverType> Optional<T> option(String key) {
        return holder.option(key);
    }

    public static <T extends TaskReceiverType> Collection<T> all() {
        return holder.allValues();
    }

    public static Enumerator<TaskReceiverType> enumerator() {
        return holder;
    }

}
