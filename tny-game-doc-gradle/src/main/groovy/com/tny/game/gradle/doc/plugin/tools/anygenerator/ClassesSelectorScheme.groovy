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

import com.tny.game.doc.output.ExportTask
import com.tny.game.scanner.ClassSelector
import com.tny.game.scanner.filter.ClassExcludeFilter
import com.tny.game.scanner.filter.ClassFilter
import com.tny.game.scanner.filter.ClassIncludeFilter
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.springframework.core.type.classreading.MetadataReader

import javax.inject.Inject
import java.util.function.Predicate

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 3:02 上午
 */
class ClassesSelectorScheme {

    private List<ClassFilter> filters = new ArrayList<>()

    private ObjectFactory objectFactory

    @Inject
    ClassesSelectorScheme(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory
    }

    @Input
    List<ClassFilter> getFilters() {
        return filters
    }

    void setFilters(List<ClassFilter> filters) {
        this.filters = filters
    }

    void filter(ClassFilter filter) {
        this.filters.add(filter)
    }

    void filterAnnotation(Action<AnnotationClassFilterSpec> action) {
        def spec = this.objectFactory.newInstance(AnnotationClassFilterSpec.class)
        action.execute(spec)
        filters.add(spec.filter())
    }

    void filterSubClassOf(Action<SubOfClassFilterSpec> action) {
        def spec = this.objectFactory.newInstance(SubOfClassFilterSpec.class)
        action.execute(spec)
        filters.add(spec.filter())
    }

    void filterInclude(Predicate<MetadataReader> filter) {
        filters.add(ClassIncludeFilter.of(filter))
    }

    void filterExclude(Predicate<MetadataReader> filter) {
        filters.add(ClassExcludeFilter.of(filter))
    }

    protected ClassSelector selector(String name, FileExportScheme scheme) {
        return ClassSelector.create(this.filters)
                .setHandler({ classes ->
                    ExportTask task = scheme.exportTask(name)
                    task.export(classes, scheme.getOutputType())
                })
    }

}
