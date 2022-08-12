/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.gradle.doc.plugin.tools.anygenerator


import com.tny.game.scanner.filter.ClassFilter
import org.gradle.api.tasks.Input

import java.lang.annotation.Annotation
import java.util.stream.Collectors

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 7:58 下午
 */
abstract class BaseClassFilterSpec {

    private List<Class<? extends Annotation>> includeClasses = new ArrayList<>()

    private List<Class<? extends Annotation>> excludeClasses = new ArrayList<>()

    private ClassLoader loader

    BaseClassFilterSpec() {
        this.loader = Thread.currentThread().contextClassLoader;
    }

    @Input
    List<Class<? extends Annotation>> getIncludeClasses() {
        return includeClasses
    }

    @Input
    List<Class<? extends Annotation>> getExcludeClasses() {
        return excludeClasses
    }

    void include(List<Class<? extends Annotation>> includeClasses) {
        this.includeClasses.addAll(includeClasses)
    }

    void exclude(List<Class<? extends Annotation>> excludeClasses) {
        this.excludeClasses.addAll(excludeClasses)
    }

    void includeClassNames(List<String> includeClasses) {
        this.includeClasses.addAll(includeClasses.stream()
                .map({ it -> loadClass(it) })
                .collect(Collectors.toList()))
    }

    void excludeClassNames(List<String> excludeClasses) {
        this.excludeClasses.addAll(excludeClasses.stream()
                .map({ it -> loadClass(it) })
                .collect(Collectors.toList()))
    }

    void includeClassNames(String... includeClasses) {
        this.includeClasses.addAll(includeClasses.toList().stream()
                .map({ it -> loadClass(it) })
                .collect(Collectors.toList()))
    }

    void excludeClassNames(String... excludeClasses) {
        this.excludeClasses.addAll(excludeClasses.toList().stream()
                .map({ it -> loadClass(it) })
                .collect(Collectors.toList()))
    }

    Class<? extends Annotation> loadClass(String name) {
        this.loader.loadClass(name) as Class<? extends Annotation>
    }

    protected abstract ClassFilter filter();

}
