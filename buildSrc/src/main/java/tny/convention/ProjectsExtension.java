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

import org.gradle.api.Project;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 根工程装配线成员集合与组号事实源的类型化扩展（pilot-binary-build-conventions D4，
 * 前身是根 build.gradle 的 ext 五键块）。职责边界：本扩展只提供"哪些工程属于哪条装配线"
 * 的命名约定判定与两个组号事实值，不做任何装配配置；消费方（各约定插件与根脚本装配线）
 * 经 {@code extensions.getByType(ProjectsExtension.class)} 按类型拉取，与 GitFlow 同一先例。
 *
 * <p>命名约定的权威定义记录在 settings.gradle 的模块清单注释中（zero-enumeration-shared-scripts
 * 规定的通道）；本类是这些约定的唯一代码化判定点，消费脚本不得再重复书写后缀字面量。
 * 三个后缀中 -bom 无编译构件、-gradle 走例外组号（spec central-publishing"Gradle 插件构件
 * 不入 Central"）、-integration-test 仅测试代码（add-integration-testing 设计决策 D3）。
 *
 * <p>configure-on-demand 语义等价性论断（迁移自原 ext 块，design D4）：派生集合的求值只遍历
 * 根工程 subprojects 的名字与组信息，不触发任何子工程评估；每次调用现算而非缓存快照，
 * 与原形态"根脚本配置阶段求值一次、消费方读取"在结果上等价，因为工程名字段在 settings
 * 评估完成后不再变化。
 */
public class ProjectsExtension {

    private final Project root;

    private String projectGroup;

    private String pluginLegacyGroup;

    public ProjectsExtension(Project root) {
        this.root = root;
    }

    /** 发布构件组号的单一事实源（spec central-publishing 设计决策 D2）：模块不得另行声明组号，
     * Central 命名空间验证与凭据迁移记录见该规格；默认值由 tny.projects 插件设置。 */
    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(String projectGroup) {
        this.projectGroup = projectGroup;
    }

    /** 唯一例外组号：Gradle 插件模块组号与插件 id 同谱系（spec central-publishing）。 */
    public String getPluginLegacyGroup() {
        return pluginLegacyGroup;
    }

    public void setPluginLegacyGroup(String pluginLegacyGroup) {
        this.pluginLegacyGroup = pluginLegacyGroup;
    }

    /** 是否为 tny-game 构件模块（java 线与插件线的公共父集）。 */
    public boolean isGameModule(Project project) {
        return project.getName().startsWith("tny-game-");
    }

    /** BOM 平台模块：无编译构件，从 javaProjects 排除、进 Central 聚合成员。 */
    public boolean isBom(Project project) {
        return project.getName().endsWith("-bom");
    }

    /** Gradle 插件模块：走例外组号与插件线装配。 */
    public boolean isGradlePlugin(Project project) {
        return project.getName().endsWith("-gradle");
    }

    /** 集成测试模块：仅测试代码，从 java 发布线与 BOM 约束面排除。 */
    public boolean isIntegrationTest(Project project) {
        return project.getName().endsWith("-integration-test");
    }

    /** 全部 tny-game 构件模块（原 ext 键 moduleProjects）。 */
    public Set<Project> moduleProjects() {
        Set<Project> result = new LinkedHashSet<>();
        for (Project sub : root.getSubprojects()) {
            if (isGameModule(sub)) {
                result.add(sub);
            }
        }
        return result;
    }

    /** java 发布线成员（原 ext 键 javaProjects）：构件模块排除 -bom、-gradle、-integration-test 三类。 */
    public Set<Project> javaProjects() {
        Set<Project> result = new LinkedHashSet<>();
        for (Project sub : moduleProjects()) {
            if (!isBom(sub) && !isGradlePlugin(sub) && !isIntegrationTest(sub)) {
                result.add(sub);
            }
        }
        return result;
    }

    /** Gradle 插件线成员（原 ext 键 gradleProjects）。 */
    public Set<Project> gradleProjects() {
        Set<Project> result = new LinkedHashSet<>();
        for (Project sub : moduleProjects()) {
            if (isGradlePlugin(sub)) {
                result.add(sub);
            }
        }
        return result;
    }
}
