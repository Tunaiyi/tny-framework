package com.tny.game.gradle.doc.plugin.tools.anygenerator

import com.tny.game.scanner.ClassSelector
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

import javax.inject.Inject

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 3:22 下午
 */
class AnyGeneratorPluginExtension {

    private List<String> classPackages = new ArrayList<>()

    private List<AnyGenerateScheme> schemes = new ArrayList<>()

    @Internal
    private final ObjectFactory objectFactory

    private Project project

    @Inject
    AnyGeneratorPluginExtension(Project project) {
        ObjectFactory objectFactory = project.getObjects()
        this.project = project
        this.objectFactory = objectFactory
    }

    @Input
    List<String> getClassPackages() {
        return classPackages
    }

    void setClassPackages(List<String> classPackages) {
        this.classPackages = classPackages
    }

    void classPackages(String... classPackages) {
        this.classPackages = classPackages.toList()
    }

    void classPackages(List<String> classPackages) {
        this.classPackages = classPackages
    }

    @Input
    List<AnyGenerateScheme> getSchemes() {
        this.schemes
    }

    void scheme(Action<? extends AnyGenerateScheme> action) {
        def newScheme = objectFactory.newInstance(AnyGenerateScheme.class, this.objectFactory)
        action.execute(newScheme)
        schemes.add(newScheme)
    }

    @Internal
    void extendTask(ProjectInternal project) {
        Map<String, AnyGenerateTask> taskMap = [:]
        project.logger.info("AnyGenerateScheme size : ${this.schemes.size()}")
        for (AnyGenerateScheme scheme : this.schemes) {
            project.logger.info("create AnyGenerateScheme ${scheme.name}")
            List<FileExportScheme> exportSchemes = scheme.exportSchemes
            for (FileExportScheme exportScheme : exportSchemes) {
                Set<String> taskNames = []
                taskNames.addAll(exportScheme.getTaskNames())
                taskNames.add("anyGenerate")
                for (String name : taskNames) {
                    AnyGenerateTask task = taskMap.computeIfAbsent(name, {
                        project.logger.info("create AnyGenerateTask ${name}")
                        AnyGenerateTask task = project.tasks.create(name, AnyGenerateTask.class)
                        task.classPackages = this.classPackages
                        task.outputParentDirectory = project.buildDir.absolutePath
                        task.dependsOn(project.tasks.build)
                        return task;
                    }) as AnyGenerateTask
                    ClassSelector selector = scheme.selector(exportScheme)
                    task.addClassSelector(selector)
                }
            }
        }
    }

}
