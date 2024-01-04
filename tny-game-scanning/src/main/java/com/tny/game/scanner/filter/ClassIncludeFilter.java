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

package com.tny.game.scanner.filter;

import org.springframework.core.type.classreading.MetadataReader;

import java.util.function.Predicate;

public interface ClassIncludeFilter extends ClassFilter {

    static ClassIncludeFilter of(Predicate<MetadataReader> filter) {
        return filter::test;
    }

    @Override
    default boolean exclude(MetadataReader reader) {
        return false;
    }

}
