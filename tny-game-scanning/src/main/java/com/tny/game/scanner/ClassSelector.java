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

package com.tny.game.scanner;

import com.tny.game.scanner.filter.*;
import org.slf4j.*;
import org.springframework.core.type.classreading.MetadataReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 2016/12/16.
 */
public class ClassSelector {

    private static final Logger LOG = LoggerFactory.getLogger(ClassSelector.class);

    private List<ClassFilter> filters = new ArrayList<>();

    private List<Class<?>> classes = Collections.emptyList();

    private Map<Thread, Set<Class<?>>> mapClass = new ConcurrentHashMap<>();

    private ClassSelectedHandler handler;

    private ClassSelector() {
    }

    public static ClassSelector create(Collection<ClassFilter> filters) {
        return new ClassSelector().addFilter(filters);
    }

    public static ClassSelector create(ClassFilter... filters) {
        return new ClassSelector().addFilter(filters);
    }

    public static ClassSelector create() {
        return new ClassSelector();
    }

    public static ClassSelector create(ClassSelectedHandler handler) {
        return new ClassSelector()
                .setHandler(handler);
    }

    public ClassSelector setHandler(ClassSelectedHandler handler) {
        this.handler = handler;
        return this;
    }

    public Collection<Class<?>> getClasses() {
        return Collections.unmodifiableCollection(this.classes);
    }

    public ClassSelector addFilter(ClassFilter... filters) {
        this.filters.addAll(Arrays.asList(filters));
        return this;
    }

    public ClassSelector addFilter(Collection<ClassFilter> filters) {
        this.filters.addAll(filters);
        return this;
    }

    Class<?> select(MetadataReader reader, ClassLoader loader, Class<?> loadClass) {
        if (filter(reader)) {
            String fullClassName = reader.getClassMetadata().getClassName();
            try {
                Thread current = Thread.currentThread();
                if (loadClass == null) {
                    if (loader == null) {
                        loader = current.getContextClassLoader();
                    }
                    loadClass = loader.loadClass(fullClassName);
                }
                this.mapClass.computeIfAbsent(current, (k) -> new HashSet<>())
                        .add(loadClass);
                return loadClass;
            } catch (Throwable e) {
                LOG.error("添加用户自定义视图类错误 找不到此类的{}文件", fullClassName, e);
            }
        }
        return loadClass;
    }

    private boolean filter(MetadataReader reader) {
        for (ClassFilter fileFilter : this.filters) {
            if (!fileFilter.include(reader) || fileFilter.exclude(reader)) {
                return false;
            }
        }
        return true;
    }

    void selected() {
        this.classes = this.mapClass.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        this.mapClass.clear();
        this.mapClass = null;
        if (this.handler != null) {
            this.handler.selected(this.getClasses());
        }
    }

    public void clear() {
        this.classes = Collections.emptyList();
    }

}
