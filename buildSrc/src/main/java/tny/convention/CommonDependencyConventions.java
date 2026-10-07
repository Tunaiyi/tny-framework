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
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.provider.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * java 发布线公共依赖装配段（tny.java-conventions 入口第三段，迁移自 tny.java-module.gradle
 * 原第 106 至 124 行：slf4j 与 commons-lang3 实现依赖、五枚公共测试依赖、-tester 测试夹具自动挂接）。
 *
 * <p>装配时机说明（consolidate-assembly-line 组 4.5 同族事实）：版本目录扩展于工程构建脚本评估时
 * 注册，根装配段急切应用时刻子工程拿不到 {@link VersionCatalogsExtension}——本段由
 * {@link JavaConventionsPlugin} 后置到工程评估收尾执行；依赖在解析期才被消费，时机等价。
 */
final class CommonDependencyConventions {

    /** 公共依赖别名清单（版本目录 libs 具名条目，次序与集合自原脚本逐字保持）。 */
    private static final List<String> IMPLEMENTATION_ALIASES = List.of("slf4jApi", "commonsLang3");

    private static final List<String> TEST_ALIASES = List.of(
            "mockitoJunitJupiter", "junitJupiter", "jmockJunit5", "springTest", "slf4jSimple");

    private CommonDependencyConventions() {
    }

    static void apply(Project project) {
        VersionCatalog catalog = project.getExtensions()
                .getByType(VersionCatalogsExtension.class).named("libs");
        IMPLEMENTATION_ALIASES.forEach(alias ->
                project.getDependencies().addProvider("implementation", library(catalog, alias)));
        TEST_ALIASES.forEach(alias ->
                project.getDependencies().addProvider("testImplementation", library(catalog, alias)));
        // 公共测试夹具按 -tester 后缀检索自动挂接（zero-enumeration-shared-scripts D4；当前唯一命中 tny-game-tester）。
        // 谓词排除宿主工程自身（fix-dependency-version-governance D8）：tester 也属 javaProjects 装配线成员，
        // 原形态下它会把自己的工程对象挂进自身的 testImplementation 形成自依赖边（无 POM 泄漏面，属脚本卫生违例；
        // tny.module-checker 零发布合同里"依赖目标是自身即跳过"的分支恰是该形态存在的自证，保留作双保险）。
        // -tester 检索谓词无第二消费方、按三次法则保持原样（pilot-binary-build-conventions D4 注记）。
        ProjectsExtension rootProjectsExt = project.getRootProject()
                .getExtensions().getByType(ProjectsExtension.class);
        testerFixtures(rootProjectsExt.moduleProjects(), project).forEach(fixture ->
                project.getDependencies().add("testImplementation", fixture));
    }

    /** 测试夹具检索判定（纯函数供单测直验）：命中 -tester 后缀且排除宿主自身。 */
    static List<Project> testerFixtures(Collection<Project> moduleProjects, Project self) {
        List<Project> fixtures = new ArrayList<>();
        for (Project candidate : moduleProjects) {
            if (candidate.getName().endsWith("-tester") && !candidate.equals(self)) {
                fixtures.add(candidate);
            }
        }
        return fixtures;
    }

    /** 目录别名取 Provider（addProvider 即 Groovy 形态 implementation libs.x 的解析落点，惰性语义一致）。 */
    private static Provider<MinimalExternalModuleDependency> library(
            VersionCatalog catalog, String alias) {
        return catalog.findLibrary(alias)
                .orElseThrow(() -> new org.gradle.api.GradleException(
                        "java 线公共依赖装配失败：版本目录 libs 中不存在别名 '" + alias + "'"));
    }
}
