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

package com.tny.game.gradle.doc.plugin.tools.template


import org.apache.commons.lang3.StringUtils
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
class ToolsTemplatePluginExtension {

    private String rootPath
    private String title = "配表工具"

    @Internal
    private final ObjectFactory objectFactory

    private Project project

    private OpenToolsTemplateTask task

    @Inject
    ToolsTemplatePluginExtension(Project project) {
        ObjectFactory objectFactory = project.getObjects()
        this.project = project
        this.objectFactory = objectFactory
    }


    void setRootPath(String rootPath) {
        this.rootPath = rootPath
    }

    @Input
    String getRootPath() {
        return rootPath
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    @Internal
    void extendTask(ProjectInternal project) {
        if (this.rootPath == null) {
            return;
        }
        OpenToolsTemplateTask task = project.tasks.create("openToolsTemplate", OpenToolsTemplateTask.class)
        if (StringUtils.isEmpty(this.rootPath)) {
            task.rootPath = project.getRootDir().getAbsolutePath()
        } else {
            task.rootPath = this.rootPath
        }
        task.title = title
    }

}
