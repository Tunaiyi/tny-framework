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

import org.gradle.api.GradleException;
import org.gradle.api.Project;

import java.util.EnumSet;
import java.util.Set;

/**
 * 模块横切角色声明（consolidate-module-modes-plugin D1）：应用 tny.module-setting 的工程经本扩展的
 * enableXxx 方法声明自身角色；角色存放在本工程扩展内，消费方沿依赖边核对（必要时 evaluationDependsOn
 * 拉起被核对工程评估），不存在全局登记清单与跨文件字符串键（取代 tnyDemoBlueprints/tnyUnpublishedProjects）。
 *
 * <p>Java 化沿革（consolidate-binary-conventions 任务 4.2，用户裁决"约定插件全部 Java 实现"的
 * 组成部分）：前身为本包 Groovy 支撑类；其 enableUnpublished 自检曾以
 * {@code rootProject.ext.has('javaProjects')} 三目兜底读取根 ext 键，根 ext 五键于
 * pilot-binary-build-conventions 决策 D4 退役后该读取恒假、判红分支永不可达，守卫静默失效
 * （pilot 归档后审计 CRITICAL-1，完整记录见本册 apply-notes 归档账目更正）。本类把发布线成员
 * 判定改经根工程类型化扩展 {@link ProjectsExtension} 按类型拉取：扩展不在位即配置期报错，
 * MUST NOT 保留任何兜底吞判——这正是不变量"声明不发布的工程不得属于发布线"重新成其为封装的
 * 达成方式（原则卷 P4）。红绿三向与扩展缺失向的用例见 ModuleSettingTest；端到端报红由本册
 * 任务 4.3 破坏探针承担。
 *
 * <p>对外 API 与 Groovy 形态逐面兼容（消费方：tny.integration-test 的受控隔离撮合、
 * tny.module-checker 接线类的沿边查询、三个模块构建文件的 moduleSetting {} 声明块）：
 * {@code enum Mode}、{@code has(Mode)}、{@code static enabled(Project, Mode)}、
 * {@code void enableApp()}、{@code void enableUnpublished()} 的调用形态不变。
 */
public class ModuleSetting {

    public enum Mode {
        /** 可作为 integrationTest 受控隔离子进程启动的应用蓝本。 */
        APP,
        /** 本工程不发布（零发布合同成员，benchmark-harness 情形泛化）。 */
        UNPUBLISHED
    }

    private final Project project;
    private final Set<Mode> modes = EnumSet.noneOf(Mode.class);

    public ModuleSetting(Project project) {
        this.project = project;
    }

    /** 本工程是应用形态蓝本（消费方：tny.demo-isolation 的受控隔离撮合）。 */
    public void enableApp() {
        modes.add(Mode.APP);
    }

    /** 本工程不发布：声明即自检"不得属于发布线成员"，并禁用本工程 publish 任务（语义承接原 tny.unpublished）。 */
    public void enableUnpublished() {
        modes.add(Mode.UNPUBLISHED);
        // 发布线成员判定按类型拉取根工程扩展：不在位即抛（UnknownDomainObjectException），禁止兜底吞判
        ProjectsExtension projects = project.getRootProject().getExtensions().getByType(ProjectsExtension.class);
        if (projects.javaProjects().contains(project)) {
            throw new GradleException("零发布合同违例：'" + project.getPath() + "' 声明 enableUnpublished()，"
                    + "却属于发布线 javaProjects 成员（命名后缀排除见 settings.gradle 约定注释）");
        }
        project.getTasks().matching(task -> task.getName().toLowerCase().startsWith("publish"))
                .configureEach(task -> task.setEnabled(false));
    }

    public boolean has(Mode mode) {
        return modes.contains(mode);
    }

    /** 安全查询：未应用 tny.module-setting 的工程返回 false；要求 target 已完成评估（消费方沿边核对时负责评估到位）。 */
    public static boolean enabled(Project target, Mode mode) {
        if (!target.getState().getExecuted()) {
            throw new GradleException("ModuleSetting.enabled: 工程 '" + target.getPath()
                    + "' 尚未完成评估，查询方须先 evaluationDependsOn");
        }
        ModuleSetting declared = target.getExtensions().findByType(ModuleSetting.class);
        return declared != null && declared.has(mode);
    }
}
