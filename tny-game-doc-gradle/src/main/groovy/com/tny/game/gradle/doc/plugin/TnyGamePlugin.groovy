package com.tny.game.gradle.doc.plugin

import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin

/**
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/4 5:05 上午
 */
class TnyGamePlugin implements Plugin<ProjectInternal> {

    @Override
    void apply(ProjectInternal project) {
        project.logger.info("init AnyGeneratorPlugin")
        project.getPluginManager().apply(JavaPlugin.class)
        AnyGeneratorPluginExtension extension = project.extensions.create("anyGenerator", AnyGeneratorPluginExtension.class, project)
//        anyGeneratorExtension.extendTask(project)
        project.afterEvaluate {
            project.logger.info("AnyGenerateScheme size : ${extension.schemes.size()}")
            extension.extendTask(project)
            project.logger.info("init AnyGeneratorPlugin finished")
        }
    }

}
