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
 * WITHOUT WARRANTIES OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention;

import org.gradle.api.Project;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;


/**
 * IDE 输出目录与 mavenJava 构件接线装配段（tny.java-conventions 入口第五段，迁移自
 * tny.java-module.gradle 原第 174 至 204 行）。
 *
 * <p>mavenJava 先创建者契约（merge-tny-module-into-java-module 变更自发布元数据退役插件逐行迁入，
 * 置于末段合区块顺序需求）：本段是 mavenJava 的创建者，tny.publications 同名块是配置方——
 * 两处同为 create-or-configure 形态，先后互换语义等价（design D2 隔离探针实证），发布物零差异为兜底判据。
 */
final class IdeAndPublicationConventions {

    private IdeAndPublicationConventions() {
    }

    static void apply(Project project) {
        project.getPlugins().withType(IdeaPlugin.class, idea -> {
            IdeaModel model = project.getExtensions().getByType(IdeaModel.class);
            model.getModule().setInheritOutputDirs(false);
            // 输出目录取 java 插件约定路径（与 compileJava/compileTestJava 的 destinationDir 同值），
            // 不再点名求值任务属性（gradle-build-style 需求二）。
            model.getModule().setOutputDir(project.file("build/classes/java/main"));
            model.getModule().setTestOutputDir(project.file("build/classes/java/test"));
            model.getModule().setDownloadJavadoc(false);
            model.getModule().setDownloadSources(true);
        });

        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublication mavenJava = publishing.getPublications().create("mavenJava", MavenPublication.class);
        mavenJava.from(project.getComponents().getByName("java"));
        mavenJava.versionMapping(mapping -> {
            mapping.usage("java-api", usage -> usage.fromResolutionOf("runtimeClasspath"));
            mapping.usage("java-runtime", usage -> usage.fromResolutionResult());
        });
    }
}
