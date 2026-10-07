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

package tny.convention.integration;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

/**
 * integration 源集通道装配段（tny.integration-test 的第一段，迁移自 tny.integration-test.gradle
 * 原第 31 至 43 行）。两级验证通道语义出处（add-integration-testing design D1；
 * rename-integration-source-set D1）：为 java 模块提供 integration source set——
 * 目录名与命令解耦，主 spec 钉的是任务命令，源集名属实现细节。
 */
final class IntegrationSourceSetConventions {

    private IntegrationSourceSetConventions() {
    }

    static SourceSet apply(Project project) {
        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        SourceSet main = java.getSourceSets().getByName("main");
        SourceSet test = java.getSourceSets().getByName("test");
        SourceSet integration = java.getSourceSets().create("integration");
        // compileClasspath += sourceSets.main.output + sourceSets.test.output（runtime 同理）：
        // FileCollection 不可变，plus 后整体回设，与 Groovy += 语义全等
        integration.setCompileClasspath(integration.getCompileClasspath()
                .plus(main.getOutput()).plus(test.getOutput()));
        integration.setRuntimeClasspath(integration.getRuntimeClasspath()
                .plus(main.getOutput()).plus(test.getOutput()));

        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("integrationImplementation")
                .extendsFrom(configurations.getByName("implementation"),
                        configurations.getByName("testImplementation"));
        configurations.getByName("integrationRuntimeOnly")
                .extendsFrom(configurations.getByName("runtimeOnly"),
                        configurations.getByName("testRuntimeOnly"));
        return integration;
    }
}
