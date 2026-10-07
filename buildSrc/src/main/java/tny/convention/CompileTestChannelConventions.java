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
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;

/**
 * java 发布线编译与验证通道装配段（tny.java-conventions 入口第二段，迁移自 tny.java-module.gradle
 * 原第 56 至 104 行：logging 绑定排他、compileJava 依赖编排、javadoc doclint 豁免、编译参数、
 * jar 排除与清单四属性、单元通道集成标签排除）。provenance 见 {@link JavaConventionsPlugin} 目录页。
 */
final class CompileTestChannelConventions {

    private CompileTestChannelConventions() {
    }

    static void apply(Project project) {
        // 排除 Boot 默认 logback 绑定与 log4j-to-slf4j 反向桥：本框架统一 log4j2 绑定，
        // 多绑定共存使 Spring Boot 启动期即因 slf4j 双 provider 报错
        // （差异说明：插件模块线历史上不带此排除，两线共用会扩大行为面，故留本线不并入共享块）。
        // 惰性形态保持：configureEach 使工程后续创建的配置（如 tny.integration-test 的 integration 系
        // 配置）同样吃到排除，改容器快照即时遍历会丢后建配置的排除面（consolidate-assembly-line 形态险点二）。
        project.getConfigurations().configureEach(configuration -> {
            configuration.exclude(java.util.Map.of("module", "spring-boot-starter-logging"));
            configuration.exclude(java.util.Map.of("module", "log4j-to-slf4j"));
        });

        project.getTasks().named("compileJava").configure(task -> task.dependsOn("processResources"));

        // javadoc doclint 降级豁免（central-publishing design D6 实施补记）：
        // 实测严格 doclint 下每模块百级错误，主因是全仓 920 文件在用的 @date 自定义标签与
        // 块标签次序类告警，非注释内容缺陷；Central 只强制 javadoc jar 存在不校验质量。
        // 注册 @date 使日期在 HTML 中正常渲染，doclint 全关保证产出稳定。
        project.getTasks().withType(Javadoc.class).configureEach(javadoc -> {
            // Gradle 对 Javadoc 任务的选项实现为 CoreJavadocOptions，选项追加方法声明在该接口
            // （Groovy 原形态经动态派发命中同一对象，转型语义全等）
            org.gradle.external.javadoc.CoreJavadocOptions options =
                    (org.gradle.external.javadoc.CoreJavadocOptions) javadoc.getOptions();
            options.addBooleanOption("Xdoclint:none", true);
            options.addStringOption("tag", "date:a:Date:");
        });

        // 编译参数统一按类型钉定：-parameters 与 fork 对 main 与 test 编译同为期望语义
        // （sweep-gradle-build-style D6），编码由 tny.compile-baseline 共享块承担（经入口前置应用）。
        project.getTasks().withType(JavaCompile.class).configureEach(compile -> {
            compile.getOptions().getCompilerArgs().add("-parameters");
            compile.getOptions().setFork(true);
        });

        project.getTasks().named("jar", Jar.class).configure(jar -> {
            jar.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
            jar.exclude("**.sql", "**.xml", "**.properties");
            // 清单四属性（merge-tny-module-into-java-module 变更自单任务退役件逐字并入同块，
            // D3：同任务两段配置合为一个块）
            jar.getManifest().getAttributes().put("Implementation-Title", jar.getProject().getName());
            jar.getManifest().getAttributes().put("Implementation-Version", jar.getProject().getVersion());
            // Automatic-Module-Name 供 JDK9+ 模块系统（Jigsaw）把本 jar 作为具名模块解析；
            // 模块名不允许连字符，故以点号替换连字符保持与包名同谱系。
            jar.getManifest().getAttributes().put("Automatic-Module-Name",
                    automaticModuleName(jar.getProject().getName()));
            jar.getManifest().getAttributes().put("Created-By", jar.getProject().getProviders()
                    .systemProperty("java.version").map(v -> v + " (" + jar.getProject().getProviders()
                            .systemProperty("java.specification.vendor").get() + ")").get());
        });

        // 单元通道排除集成用例（integrationTest 任务专跑，add-integration-testing design D1）
        project.getTasks().named("test", Test.class).configure(test ->
                test.useJUnitPlatform(platform -> platform.excludeTags("integration")));
    }

    /** jar 清单 Automatic-Module-Name 派生：连字符以点号替换（纯函数，供单测直验）。 */
    static String automaticModuleName(String projectName) {
        return projectName.replace('-', '.');
    }
}
