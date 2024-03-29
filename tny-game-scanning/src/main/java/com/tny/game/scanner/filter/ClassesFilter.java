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

import java.util.*;

public class ClassesFilter implements ClassFilter {

    private Set<Class<?>> includes = new HashSet<>();

    private Set<Class<?>> excludes = new HashSet<>();

    public ClassesFilter(Collection<Class<?>> includes) {
        this(includes, false);
    }

    @SafeVarargs
    public ClassesFilter(Class<?>... includes) {
        this(Arrays.asList(includes), false);
    }

    @SafeVarargs
    public ClassesFilter(boolean exclude, Class<?>... filters) {
        this(Arrays.asList(filters), exclude);
    }

    public ClassesFilter(Collection<Class<?>> filters, boolean exclude) {
        if (!exclude) {
            this.includes.addAll(filters);
        } else {
            this.excludes.addAll(filters);
        }
    }

    public ClassesFilter(
            Collection<Class<?>> includes,
            Collection<Class<?>> excludes) {
        this.includes.addAll(includes);
        this.excludes.addAll(excludes);
    }

    private boolean check(MetadataReader reader, Set<Class<?>> filter) {
        for (Class<?> clazz : this.includes) {
            String className = reader.getClassMetadata().getClassName();
            if (clazz.getName().equals(className)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean include(MetadataReader reader) {
        if (this.includes.isEmpty()) {
            return true;
        }
        if (this.check(reader, this.includes)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean exclude(MetadataReader reader) {
        if (this.excludes.isEmpty()) {
            return false;
        }
        if (this.check(reader, this.includes)) {
            return true;
        }
        return false;
    }

}
