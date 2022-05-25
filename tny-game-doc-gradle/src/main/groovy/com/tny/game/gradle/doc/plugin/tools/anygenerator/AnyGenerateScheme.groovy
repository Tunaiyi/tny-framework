package com.tny.game.gradle.doc.plugin.tools.anygenerator

import com.tny.game.scanner.ClassSelector
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

import javax.inject.Inject

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 3:02 上午
 */
class AnyGenerateScheme {

    private String name = "AnyGenerate"

    /**
     * 导出任务列表
     */
    private List<FileExportScheme> exportSchemes = new ArrayList<>()

    /**
     * 类选择器
     */
    private ClassesSelectorScheme selectorScheme

    private final ObjectFactory objectFactory

    @Input
    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    void name(String name) {
        this.setName(name)
    }

    @Inject
    AnyGenerateScheme(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory
        selectorScheme = objectFactory.newInstance(ClassesSelectorScheme.class, objectFactory)
    }

    @Input
    List<FileExportScheme> getExportSchemes() {
        return exportSchemes
    }

    void export(Action<? extends FileExportScheme> action) {
        def task = objectFactory.newInstance(FileExportScheme.class, objectFactory)
        action.execute(task)
        exportSchemes.add(task)
    }

    void select(Action<? extends ClassesSelectorScheme> action) {
        action.execute(this.selectorScheme)
    }

    ClassesSelectorScheme getClassesSelector() {
        return selectorScheme
    }

    @Internal
    protected ClassSelector selector(FileExportScheme scheme) {
        return selectorScheme.selector(this.name, scheme)
    }

}
