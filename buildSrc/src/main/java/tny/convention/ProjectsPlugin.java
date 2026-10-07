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

/**
 * 装配线成员判定扩展的注册插件（id {@code tny.projects}，pilot-binary-build-conventions D4）：
 * 在根工程创建名为 {@code projects} 的 {@link ProjectsExtension} 并设入两个组号事实源的默认值。
 * 职责边界：只做扩展注册与组号默认值；命名约定的判定逻辑与被判定集合都在扩展类内，
 * 装配配置不属于本插件。组号事实值的规格出处与"模块不得另行声明组号"的约束见
 * ProjectsExtension 的 javadoc（central-publishing 设计决策 D2 随迁于此，原注记位于
 * 根 build.gradle 的 ext 块，脚本现仅保留一行引入语句与指针注释）。
 *
 * <p>应用位置约定：只应用于根工程，且必须先于一切消费方（根脚本装配线行与各约定插件
 * 内按类型拉取处），与 tny.release-ops 的 GitFlow 扩展同一模式——消费方经 getByType 按
 * 类型获取，扩展不在位即配置期报错。根脚本现序 tny.release-ops 先于本插件应用。
 *
 * <p>派生版本注入（consolidate-assembly-line 变更设计决策 D4 变体乙，
 * convert-orchestration-to-java 变更任务 5.1 转正）：gitFlow 扩展在位时本方法经
 * {@link GitVersionSource} 契约读取其 projectVersion 并写入 projects 扩展的
 * derivedProjectVersion；gitFlow 不在位时留空不抛（保持 tny.projects 独立可用的既有
 * 测试与复用形态）。consolidate-assembly-line 变更立项时的编译墙（buildSrc 先编译
 * Java 后编译 Groovy，该变更 design.md"探针结论"小节的探针五沙箱实验实证）已随
 * GitFlow 转为 Java 类消失，该变更为此登记的 GroovyObject 弱型反射降级就此撤销。
 * 注入语义含随提交 34fc8b11 迁入的 redesign-devline-integration-model 变更设计决策
 * D2 回落规则（取值等于 {@code rootGitFlow.projectVersion ?: Project.DEFAULT_VERSION}，
 * 出处见本包 ProjectsExtension 的 getDerivedProjectVersion javadoc）；
 * "tny.release-ops 未先应用即报红"的消费期契约由约定插件
 * tny.dependency-conventions 的版本派生承担，与原预编译脚本
 * getByType(GitFlow) 的报错时机同段（子工程配置期）。
 */
public class ProjectsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ProjectsExtension projects = project.getExtensions().create("projects", ProjectsExtension.class, project);
        projects.setProjectGroup("com.tnydev.game");
        projects.setPluginLegacyGroup("com.tny.game");
        Object gitFlow = project.getExtensions().findByName("gitFlow");
        if (gitFlow instanceof GitVersionSource source) {
            // 类型化注入（convert-orchestration-to-java 变更任务 5.1）：GitFlow 转 Java 后
            // 经 GitVersionSource 契约读取，consolidate-assembly-line 变更登记的弱型反射就此撤销。
            String projectVersion = source.getProjectVersion();
            projects.setDerivedProjectVersion(
                    projectVersion != null ? projectVersion : Project.DEFAULT_VERSION);
        }
    }
}
