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

package com.tny.game.common.tag;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * 可标记的
 * Created by Kun Yang on 2017/4/28.
 */
public interface Taggable {

    Set<Object> tags();

    default boolean anyMatch(Object... targets) {
        return this.anyMatch(ImmutableList.copyOf(targets));
    }

    default boolean anyMatch(Collection<Object> targets) {
        return CollectionUtils.containsAny(tags(), targets);
    }

    default boolean allMatch(Object... targets) {
        return this.allMatch(ImmutableList.copyOf(targets));
    }

    default boolean allMatch(Collection<Object> targets) {
        return CollectionUtils.containsAll(tags(), targets);
    }

}
