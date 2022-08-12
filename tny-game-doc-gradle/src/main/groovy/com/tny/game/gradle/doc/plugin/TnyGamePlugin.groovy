/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
