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

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.io.File;

/**
 * java 发布线基线装配段（tny.java-conventions 入口的第一段，迁移自 tny.java-module.gradle
 * 原第 13 至 54 行：插件应用、source/target 钉定、sourcesJar 手写注册、java 块构件与 toolchain、
 * createProject 任务）。provenance 与次序纪律见 {@link JavaConventionsPlugin} 类 javadoc 目录页。
 */
final class JavaPluginBaselineConventions {

    private JavaPluginBaselineConventions() {
    }

    static void apply(Project project) {
        // 原 plugins{} 块四行逐字保序（idea 对已应用者幂等；maven-publish 在本线重复声明是
        // 行内成员自防形态，唯一供给点在 tny.dependency-conventions，此处仅幂等重言）
        project.getPluginManager().apply("idea");
        project.getPluginManager().apply("java");
        project.getPluginManager().apply("java-library");
        project.getPluginManager().apply("maven-publish");

        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        String javaVersion = project.getProviders().gradleProperty("javaVersion").get();
        // 经 java 扩展对象钉定 source/target（2026-10-03 弃用修正：脚本动态属性直写会命中已弃用的
        // JavaPluginConvention 老式门面；javaVersion 取值仍来自 gradle.properties 单一事实源，语义等值）
        java.setSourceCompatibility(JavaVersion.toVersion(javaVersion));
        java.setTargetCompatibility(JavaVersion.toVersion(javaVersion));

        // sourcesJar 必须先以本声明注册、再由下方 java 块 withSourcesJar() 复用：
        // 复用路径只把构件挂入 java 组件（发布仍产出 sources jar），不自建则会把任务接入
        // assemble，使 build 执行面偏离历史形态（sweep-gradle-build-style D5 实施修正，实测 51 项差异定罪）。
        project.getTasks().register("sourcesJar", Jar.class, task -> {
            task.dependsOn("classes");
            task.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);
            task.getArchiveClassifier().set("sources");
            SourceSet main = java.getSourceSets().getByName("main");
            task.from(main.getAllSource());
        });

        // 构建可复现：编译/测试/javadoc 钉在 Java 21 toolchain，与 daemon 所在 JVM 解耦（align-build-jdk-21）。
        // Central 准入四件套之 javadoc 与 sources 构件（central-publishing spec）：sources 由上方手写注册
        // 与 withSourcesJar 复用共同承载，javadoc 由声明生成同名任务，均经发布段 from components.java 自动挂入。
        java.withJavadocJar();
        java.withSourcesJar();
        java.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(Integer.parseInt(javaVersion)));

        // 原 createProject 任务（sourceSets 目录脚手架）
        project.getTasks().register("createProject", task -> task.doLast(t -> {
            java.getSourceSets().forEach(sourceSet -> {
                SourceDirectorySet javaDirs = sourceSet.getJava();
                javaDirs.getSrcDirs().forEach(File::mkdirs);
                sourceSet.getResources().getSrcDirs().forEach(File::mkdirs);
            });
        }));
    }
}
