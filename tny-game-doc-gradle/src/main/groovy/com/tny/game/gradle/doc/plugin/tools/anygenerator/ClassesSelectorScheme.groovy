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
