/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
