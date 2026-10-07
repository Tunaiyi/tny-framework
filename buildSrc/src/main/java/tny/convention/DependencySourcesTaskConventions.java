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
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 依赖源码包批量下载任务装配段（tny.java-conventions 入口第四段，迁移自 tny.java-module.gradle
 * 原第 126 至 172 行；merge-dependency-sources-into-java-module 自主约定插件退役件逐行迁入，
 * 任务语义不变；只读外部仓下载产物，不参与发布与门禁；镜像未代理 sources 时按缺件跳过并记录日志）。
 *
 * <p>程序性行为全部位于任务注册闭包的动作块内（gradle-build-style"工程配置写声明式语句"需求一
 * 的容身之处纪律），与原脚本形态同构；任务名与注册时机逐字不变。
 */
final class DependencySourcesTaskConventions {

    private static final List<String> SOURCE_SCAN_CONFIGURATIONS =
            List.of("compileClasspath", "runtimeClasspath", "testRuntimeClasspath");

    private DependencySourcesTaskConventions() {
    }

    static void apply(Project project) {
        project.getTasks().register("downloadDependencySources", task -> {
            task.setGroup("dependency management");
            task.setDescription("下载 compile/runtime/testCompile 类路径依赖的 sources 源码包到 build/dependency-sources");
            task.doLast(t -> {
                File outDir = project.getLayout().getBuildDirectory()
                        .dir("dependency-sources").get().getAsFile();
                outDir.mkdirs();

                // 收集所有已解析依赖坐标（去重）
                Set<String> modules = new LinkedHashSet<>();
                for (String configurationName : SOURCE_SCAN_CONFIGURATIONS) {
                    Configuration configuration = project.getConfigurations()
                            .getByName(configurationName);
                    configuration.getIncoming().getResolutionResult().getAllDependencies().forEach(dependency -> {
                        if (dependency instanceof ResolvedDependencyResult resolved) {
                            ModuleVersionIdentifier moduleVersion = resolved.getSelected().getModuleVersion();
                            modules.add(moduleVersion.getGroup() + ":" + moduleVersion.getName()
                                    + ":" + moduleVersion.getVersion());
                        }
                    });
                }

                List<String> downloaded = new ArrayList<>();
                List<String> missing = new ArrayList<>();
                for (String coordinate : modules) {
                    // group:name:version:classifier 记法指定 sources classifier
                    Configuration sourcesConfiguration = project.getConfigurations().detachedConfiguration(
                            project.getDependencies().create(coordinate + ":sources"));
                    sourcesConfiguration.setTransitive(false);
                    try {
                        project.copy(spec -> {
                            spec.from(sourcesConfiguration);
                            spec.into(outDir);
                            spec.setIncludeEmptyDirs(false);
                            spec.eachFile(details -> details.setPath(details.getName()));
                        });
                        downloaded.add(coordinate);
                    } catch (Exception ignored) {
                        // 镜像仓库未代理 sources 包时属正常情况，记录后跳过
                        missing.add(coordinate);
                        t.getLogger().info("[dependency-sources] 无源码包: {}", coordinate);
                    }
                }

                t.getLogger().lifecycle("[dependency-sources] " + project.getName() + ": 成功 "
                        + downloaded.size() + " 个，无源码包 " + missing.size() + " 个 -> " + outDir);
            });
        });
    }
}
