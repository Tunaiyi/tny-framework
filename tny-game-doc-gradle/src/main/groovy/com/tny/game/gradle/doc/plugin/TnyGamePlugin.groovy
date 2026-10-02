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

package com.tny.game.gradle.doc.plugin

import com.tny.game.gradle.doc.plugin.tools.anygenerator.AnyGeneratorPluginExtension
import com.tny.game.gradle.doc.plugin.tools.template.ToolsTemplatePluginExtension
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
        AnyGeneratorPluginExtension anyGeneratorExtension = project.extensions.create("anyGenerator", AnyGeneratorPluginExtension.class, project)
        ToolsTemplatePluginExtension toolsTemplateExtension = project.extensions.create("toolsTemplate", ToolsTemplatePluginExtension.class, project)
        project.afterEvaluate {
            project.logger.info("AnyGenerateScheme size : ${anyGeneratorExtension.schemes.size()}")
            anyGeneratorExtension.extendTask(project)
            project.logger.info("init AnyGeneratorPlugin finished")
            toolsTemplateExtension.extendTask(project)
            project.logger.info("init ToolsTemplatePlugin finished")

        }

    }

}
