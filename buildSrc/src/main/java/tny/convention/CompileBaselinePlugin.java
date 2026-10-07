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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.GroovyCompile;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;

import java.util.concurrent.TimeUnit;

/**
 * 编译基线共享块（adopt-gradle-official-dsl D4 的两块由 consolidate-compile-baseline-blocks 合一；
 * 前身分别为 gradle/cache-policy.gradle 与 gradle/compile-encoding.gradle）：changing 模块与动态版本
 * 一律不缓存，Java/Groovy 编译与 javadoc 一律钉 encoding 属性（gradle.properties，值 UTF-8），
 * 共同消除构建对缓存时效与 daemon 区域设置的隐式依赖。java 发布模块线与插件模块线共用同一实现。
 *
 * <p>边界：仓路由由 settings.gradle 的 dependencyResolutionManagement 单点声明
 * （centralize-resolution-config；旧头注释所称 tny.repositories 插件已不存在，指称按现状修正，D3）；
 * -parameters 与 fork 等 JavaCompile 参数只属 java 发布模块线（插件模块线历史上不带，
 * 不并入本块以免行为面扩大，见 tny.java-module 内注与变更 verification-notes 的 D4 修正记录）。
 *
 * <p>载体沿革：同名 id 的二进制实现（consolidate-binary-conventions 任务 6.1，注册行与脚本删除
 * 同提交）。encoding 属性经 providers 配置模型读取（gradle-build-style"任务注册与配置使用惰性
 * 形态"需求：属性读取 SHALL 优先 providers 形态），替代脚本的裸属性引用。原脚本按
 * {@code AbstractCompile} 基类型钉编码——该基类型在公开 API 上无 options 访问器，Java 形态改按
 * 两个具体子类型 {@link JavaCompile} 与 {@link GroovyCompile} 分别惰性配置；本仓装配线内
 * AbstractCompile 的在位子类型恰仅此两类，行为面等值。
 */
public class CompileBaselinePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        String encoding = project.getProviders().gradleProperty("encoding").get();

        project.getConfigurations().configureEach(configuration -> {
            configuration.getResolutionStrategy().cacheChangingModulesFor(0, TimeUnit.SECONDS);
            configuration.getResolutionStrategy().cacheDynamicVersionsFor(0, TimeUnit.SECONDS);
        });

        project.getTasks().withType(JavaCompile.class).configureEach(
                compile -> compile.getOptions().setEncoding(encoding));
        project.getTasks().withType(GroovyCompile.class).configureEach(
                compile -> compile.getOptions().setEncoding(encoding));
        project.getTasks().withType(Javadoc.class).configureEach(
                javadoc -> javadoc.getOptions().setEncoding(encoding));
    }
}
