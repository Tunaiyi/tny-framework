/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

    public static <T extends TaskReceiverType> T ofAlias(String alias) {
        String[] heads = StringUtils.split(alias, '$');
        return holder.check("$" + heads[0], "获取 别名前缀 {} 的 TaskReceiverType 不存在", heads[0]);
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
