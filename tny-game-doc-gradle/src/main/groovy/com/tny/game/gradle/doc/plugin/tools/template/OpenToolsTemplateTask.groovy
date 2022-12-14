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

import funs.tools.template.window.JConfigFrame
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

/**
 * $DESC
 * <p>
 *
 * @date Kun Yang
 * @date 2022/4/8 03:07
 * */
class OpenToolsTemplateTask extends DefaultTask {

    public String rootPath;

    public String title;

    @Inject
    OpenToolsTemplateTask() {
        this.group = "any-generator"
    }


    @TaskAction
    void doAction() {
        new JConfigFrame(title, rootPath);
    }


}
