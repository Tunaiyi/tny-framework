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
