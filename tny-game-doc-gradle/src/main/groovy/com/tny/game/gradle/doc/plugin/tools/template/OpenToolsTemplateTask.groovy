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
