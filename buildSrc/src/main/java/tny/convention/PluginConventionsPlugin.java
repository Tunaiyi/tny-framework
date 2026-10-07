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

import org.gradle.api.Plugin;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.io.File;

/**
 * Gradle 插件模块线总入口（id {@code tny.plugin-conventions}，consolidate-assembly-line 任务 6.1-6.2
 * 由 tny.plugin-module.gradle 吸收立口，同时注销 tny.compile-baseline 注册 id——两线均改按类复用
 * {@link CompileBaselinePlugin}，实现类保留）。前身为 gradle/plugin-module.gradle
 * （adopt-gradle-official-dsl 插件化）；当前成员：tny-game-doc-gradle。
 *
 * <p>目录页（内部装配次序逐字等于被收编行原行序：根 gradleProjects 段原
 * tny.compile-baseline 行在前、tny.plugin-module 行在后）：
 * <ol>
 * <li>{@link CompileBaselinePlugin} 按类应用——缓存与编码共享块（原根第 55 行语义；
 *     java 线侧由 {@link JavaConventionsPlugin} 同样按类复用，两处声明构成共享而非逐字复制，
 *     gradle-build-style 共享配置块收编条款以二进制实现类为组合单元）；</li>
 * <li>本方法内联段——groovy 与 java-gradle-plugin 应用、Java 21 toolchain 对齐、
 *     gradleApi/localGroovy 依赖、手写 sourcesJar 与 createProject（迁移自原脚本全部行为；
 *     例外组号依托的发布通道 tny.publish 与门禁 tny.publish.gate 仍由根装配线先行逐行应用，
 *     属发布族冻结面）。</li>
 * </ol>
 *
 * <p>边界：BOM/仓库/组号版本派生在 tny.dependency-conventions；本线不走 tny.java-conventions 的
 * 发布元数据链（插件 id 与组号同谱系，spec central-publishing"Gradle 插件构件本次不入 Central"）。
 * 本入口不引入任何未命名 maven 仓库声明（仓命名契约见 consolidate-assembly-line design 风险节）。
 */
public class PluginConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        new CompileBaselinePlugin().apply(project);

        project.getPluginManager().apply("groovy");
        project.getPluginManager().apply("java-gradle-plugin");

        // 构建可复现：Groovy/插件模块编译与 javaProjects 对齐钉定 Java 21 toolchain（align-groovy-build-jdk-21）
        JavaPluginExtension javaExt = project.getExtensions().getByType(JavaPluginExtension.class);
        int javaVersion = Integer.parseInt(project.getProviders().gradleProperty("javaVersion").get());
        javaExt.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(javaVersion));

        project.getDependencies().add("api", project.getDependencies().gradleApi());
        project.getDependencies().add("api", project.getDependencies().localGroovy());

        // 插件模块线保留手写 sourcesJar（groovy 源集接线与 java 模块线不同，等价性未实测，
        // 见 sweep-gradle-build-style D5 否决备选）；惰性注册并把依赖写进注册闭包。
        // 注意本线 duplicatesStrategy 为 INCLUDE，与 java 线的 EXCLUDE 是有意差异（形同义异，注释写明差异来由）。
        project.getTasks().register("sourcesJar", Jar.class, task -> {
            task.dependsOn("classes");
            task.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
            task.getArchiveClassifier().set("sources");
            SourceSet main = javaExt.getSourceSets().getByName("main");
            task.from(main.getAllSource());
        });

        // 原 createProject 任务（groovy 与 resources 源目录脚手架；java 线版本取 java 源集，本线取 groovy 源集）。
        // SourceSet 为 ExtensionAware，groovy 源目录集由 groovy 插件注册于其扩展容器按名可取——
        // 与 Groovy 形态 sourceSets*.groovy 同一对象，非内部类型通道。
        project.getTasks().register("createProject", task -> task.doLast(t ->
                javaExt.getSourceSets().forEach(sourceSet -> {
                    SourceDirectorySet groovy = (SourceDirectorySet)
                            ((ExtensionAware) sourceSet).getExtensions().getByName("groovy");
                    groovy.getSrcDirs().forEach(File::mkdirs);
                    sourceSet.getResources().getSrcDirs().forEach(File::mkdirs);
                })));
    }
}
