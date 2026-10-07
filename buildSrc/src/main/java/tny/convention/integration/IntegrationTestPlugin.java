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

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.testing.Test;
import tny.convention.ModuleSetting;
import tny.convention.ProjectsExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 两级验证通道约定插件（id {@code tny.integration-test}；adopt-gradle-official-dsl 插件化，
 * 前身为 gradle/integration-test.gradle；merge-demo-isolation-into-integration-test 起并含
 * 集成测试专用模块的受控隔离子进程装配。consolidate-assembly-line 任务 7.1-7.3 由预编译脚本
 * 同名 Java 化——id 保留，因 tny-game-integration-test 模块 plugins 块按 id 应用，脚本删除与
 * 注册行新增同提交）。
 *
 * <p>本插件为 java 模块提供 integration source set 与命令入口任务 integrationTest；
 * 尾部门控段仅对 -integration-test 后缀的专用模块生效（命名约定见 settings.gradle，
 * 主规格"共享构建脚本禁止枚举具体工程名"需求的成员判定通道之一），其余模块经本插件零动作。
 *
 * <p>目录页（每项行为指名所在类与方法，两跳内可定位任一行为的实现处）：
 * <ol>
 * <li>{@link IntegrationSourceSetConventions#apply}——integration 源集与两配置的
 *     extendsFrom 接线（原第 31 至 43 行）；</li>
 * <li>本类 integrationTest 任务注册段（原第 51 至 97 行）：标签选择、docker 端点透传
 *     （{@link #resolveDockerHost}，-PdockerHost 优先于环境变量、再优先于标准私有 socket
 *     探测）、降并发与日志形态；</li>
 * <li>本类评估收尾段（原第 45 至 49 行）：三条 integration 公共测试依赖经版本目录取用——
 *     版本目录扩展在根装配段急切应用时刻于子工程不在位（consolidate-assembly-line 组 4.5
 *     实测同族事实），后置到评估收尾添加，解析期才消费、时机等价；</li>
 * <li>本类门控段（原第 99 至 156 行）：受控隔离子进程装配，目标交集与三处 fail-fast 判定在
 *     {@link DemoIsolationCheck}（可单测），本类只做生命周期接线与 classpath 段拼装。</li>
 * </ol>
 *
 * <p>用例约定标 @Tag("integration")（类名 *IT），依赖容器的叠加 @Tag("docker")；
 * -PincludeDocker 未开启时 docker 用例不进入执行集；开启但容器环境不可用时，由用例侧
 * @Testcontainers(disabledWithoutDocker = true) 判定为 skip（不算失败）。
 * 本任务不挂 check（门禁由 CI 三档显式调用，add-integration-testing design D7）。
 */
public class IntegrationTestPlugin implements Plugin<Project> {

    private static final List<String> INTEGRATION_TEST_ALIASES =
            List.of("assertj", "awaitility", "testcontainersJunit");

    @Override
    public void apply(Project project) {
        SourceSet integration = IntegrationSourceSetConventions.apply(project);

        project.getTasks().register("integrationTest", Test.class, task -> {
            task.setDescription("Integration tests (@Tag(\"integration\"); docker cases need -PincludeDocker)");
            task.setGroup("verification");
            task.setTestClassesDirs(integration.getOutput().getClassesDirs());
            task.setClasspath(integration.getRuntimeClasspath());
            task.useJUnitPlatform(platform -> {
                platform.includeTags("integration");
                if (!project.hasProperty("includeDocker")) {
                    platform.excludeTags("docker");
                }
            });
            // docker 端点透传（macOS OrbStack/Docker Desktop 不提供 /var/run/docker.sock，
            // testcontainers 默认探测会判"无 Docker"→ skip）
            String dockerHost = resolveDockerHost(project);
            if (dockerHost != null) {
                task.getEnvironment().put("DOCKER_HOST", dockerHost);
            }
            // CI 降并发换确定性（stabilize-build-test-infra 组 5 判决表②，并行 fork 放大尾延迟窗口；
            // 本地多核环境同样受此约束，双端一致性验收 5.5）
            task.setMaxParallelForks(1);
            task.shouldRunAfter(project.getTasks().named("test"));
            task.getTestLogging().setEvents(List.of("failed", "skipped"));
            task.getTestLogging().setExceptionFormat("full");
        });

        project.afterEvaluate(checked -> {
            VersionCatalog catalog = checked.getExtensions()
                    .getByType(VersionCatalogsExtension.class).named("libs");
            for (String alias : INTEGRATION_TEST_ALIASES) {
                checked.getDependencies().addProvider("integrationImplementation",
                        catalog.findLibrary(alias).orElseThrow(() -> new GradleException(
                                "integration 公共测试依赖装配失败：版本目录 libs 中不存在别名 '" + alias + "'")));
            }
        });

        ProjectsExtension rootProjectsExt = project.getRootProject()
                .getExtensions().getByType(ProjectsExtension.class);
        if (rootProjectsExt.isIntegrationTest(project)) {
            wireDemoIsolation(project, integration);
        }
    }

    /**
     * 受控隔离子进程装配门控段（自 tny.demo-isolation 整体迁入，闸门用 -integration-test 后缀
     * 命名约定，接线体逐行未改）。子进程隔离 classpath 属性（add-starter-net-integration-tests）：
     * demo 应用子进程禁止复用测试 JVM 的 classpath——那会把 harness 单元表/@Unit bean 面泄进
     * 子进程 Spring 扫描，同 fork 重用时反向毒化后续 IT（DemoPlayerObjectCodecableCodec 串染事故实证）。
     */
    private void wireDemoIsolation(Project project, SourceSet integration) {
        project.getTasks().named("integrationTest", Test.class).configure(task -> {
            // 任务依赖由集成运行时类路径配置整体携带：蓝图 jar 是其构件（Test 任务类路径属性本就
            // 隐式依赖构件生产任务），不逐工程引用 jar 任务；任务图零新增边由验收 dry-run 比对证实
            // （simplify-demo-isolation-wiring D2）。
            task.dependsOn(project.getConfigurations().getByName("integrationRuntimeClasspath"));
            task.doFirst(running -> collectAndWire(project, integration, (Test) running));
            // 进程级隔离：demo 类装载/DTO scheme 装载会写入 JVM 级静态 codec/单元表（串染同 fork
            // 后续 IT 的 Spring 装配面，事故实证 DemoPlayerObjectCodecableCodec），每类独占新 JVM 根治
            task.setForkEvery(1);
        });
    }

    /** doFirst 接线：就地核对采集事实、交判定类报红、按声明序拼装隔离 classpath 系统属性。 */
    private void collectAndWire(Project project, SourceSet integration, Test task) {
        // 就地核对：执行期所有工程已完成评估，直接读依赖对象的角色声明；
        // getDependencyProject 已弃用（Gradle 9 移除），以工程路径解析等价替代（与 tny.module-checker 同法）
        Configuration integrationImplementation =
                project.getConfigurations().getByName("integrationImplementation");
        List<DemoIsolationCheck.DeclaredProject> declared = new ArrayList<>();
        List<Project> declaredProjects = new ArrayList<>();
        for (ProjectDependency dependency : integrationImplementation.getDependencies()
                .withType(ProjectDependency.class)) {
            Project target = project.getRootProject().project(dependency.getPath());
            declaredProjects.add(target);
            declared.add(new DemoIsolationCheck.DeclaredProject(
                    target.getPath(),
                    ModuleSetting.enabled(target, ModuleSetting.Mode.APP),
                    target.getTasks().getNames().contains("jar"),
                    // 预期差异申报（consolidate-assembly-line design D6）：原 Groovy 形态用
                    // named('runtimeClasspath')==null 判空，named 对缺失配置实际抛异常、该报红分支
                    // 不可达；findByName 使原设计的报红文案可达，属把意外异常改为设计报红的修复，
                    // 文案本身逐字保持原脚本。
                    target.getConfigurations().findByName("runtimeClasspath") != null));
        }
        List<String> problems = DemoIsolationCheck.violations(declared);
        if (!problems.isEmpty()) {
            throw new GradleException(problems.get(0));
        }
        List<String> segments = new ArrayList<>();
        List<DemoIsolationCheck.DeclaredProject> targets = DemoIsolationCheck.appTargets(declared);
        for (DemoIsolationCheck.DeclaredProject fact : targets) {
            Project target = declaredProjects.stream()
                    .filter(candidate -> candidate.getPath().equals(fact.path())).findFirst().orElseThrow();
            // 子进程真实运行形态＝demo 自身 runtime classpath（其 jar 在各自段首），与测试 JVM 完全切割；
            // Gradle 8.5 systemProperty 不解包 Provider，故 doFirst 执行期求值（jar 经任务依赖已就绪）
            segments.add(target.getConfigurations().getByName("runtimeClasspath").getAsPath());
            segments.add(target.getTasks().named("jar", Jar.class).get()
                    .getArchiveFile().get().getAsFile().getAbsolutePath());
        }
        List<String> parts = new ArrayList<>();
        integration.getOutput().getClassesDirs().getFiles().forEach(dir -> parts.add(dir.getAbsolutePath()));
        parts.add(integration.getOutput().getResourcesDir().getAbsolutePath());
        // 受控隔离 classpath＝IT 自身产物 + 按声明序各目标的 runtimeClasspath 与其发布 jar：
        // 防在途未编译产物/新类泄入子进程。每目标无条件串接 runtimeClasspath 的 asPath
        // （空集双分隔符形态与 JVM classpath 容忍度均同历史行为），单目标结果与改造前逐字节等值。
        List<String> joined = new ArrayList<>();
        joined.add(String.join(File.pathSeparator, parts));
        joined.addAll(segments);
        task.systemProperty("it.demo.isolatedClasspath", String.join(File.pathSeparator, joined));
    }

    /** docker 端点解析：-PdockerHost 优先，其次环境变量，再次标准私有 socket 探测（配置见 gradle.properties）。 */
    private static String resolveDockerHost(Project project) {
        if (project.hasProperty("dockerHost")) {
            return project.property("dockerHost").toString();
        }
        String fromEnv = project.getProviders().environmentVariable("DOCKER_HOST").getOrNull();
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        String home = project.getProviders().systemProperty("user.home").get();
        // 候选路径取 gradle.properties 逗号清单，空键回退内置 orbstack/docker 默认
        // （centralize-resolution-config D4：换机调参不改脚本，CI 无键行为不变）
        List<String> configured = new ArrayList<>();
        for (String candidate : project.getProviders().gradleProperty("dockerSocketCandidates")
                .getOrElse("").split(",")) {
            String trimmed = candidate.trim();
            if (!trimmed.isEmpty()) {
                configured.add(trimmed);
            }
        }
        List<String> candidates = configured.isEmpty()
                ? List.of(home + "/.orbstack/run/docker.sock", home + "/.docker/run/docker.sock")
                : configured;
        for (String candidate : candidates) {
            if (new File(candidate).exists()) {
                return "unix://" + candidate;
            }
        }
        return null;
    }
}
