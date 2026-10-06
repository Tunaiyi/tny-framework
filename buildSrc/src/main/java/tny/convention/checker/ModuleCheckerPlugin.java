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

package tny.convention.checker;

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.ProjectDependency;
import tny.convention.ModuleSetting;
import tny.convention.ProjectsExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置阶段跨工程对账约定插件（id {@code tny.module-checker}；adopt-gradle-official-dsl 插件化，
 * 前身为 gradle/project-checks.gradle，pilot-binary-build-conventions 批次 1 由预编译脚本
 * tny.module-checker.gradle 退役二进制化——工程标识名 tny.module-checker 指向本插件内容重心，
 * 即构件模块硬合同的配置期对账）。
 *
 * <p>本插件负责且仅负责三项校验，判定逻辑各自拆入可单测检查类（gradle-build-style
 * "二进制实现的检查逻辑必须携带单元测试"需求）：
 * <ul>
 * <li>发布构件组号对账与构建文件存在性断言（central-publishing 设计决策 D2、
 *     fix-dependency-version-governance 设计决策 D8）→ {@link GroupAlignmentCheck}；</li>
 * <li>零发布合同（consolidate-module-modes-plugin 设计决策 D2：依赖边就地核对，沿依赖强制评估
 *     被依赖方后读其 tny.module-setting 声明，取代"根清单到齐点名"）→ 文案判定入
 *     {@link UnpublishedContractCheck}；</li>
 * <li>托管版本面对账守卫（fix-dependency-version-governance 设计决策 D1：大头版本族代表坐标的
 *     托管生效值必须等于事实源声明值，后导入 BOM 静默覆盖先导入者的次序漂移在配置阶段报红，
 *     规格见 gradle-build-style"托管版本面在配置期与声明事实源对账"需求）→
 *     {@link ManagedVersionsCheck}。</li>
 * </ul>
 *
 * <p>边界：不做装配配置；组号对账须由根构建脚本在 tny.projects 应用行之后的小节引入
 * （成员集合与组号事实源经该扩展拉取）。单测与端到端的覆盖分工（设计决策 D3）：检查类单测
 * 覆盖判定函数的红绿两向，本接线类承载的 Gradle 生命周期语义（projectsEvaluated 时机、
 * configure-on-demand 已评估过滤、afterEvaluate 沿依赖边强制评估）由主构建配置期输出与
 * 破坏探针验证，本类不写单测。
 *
 * <p>语言沿革完整句：pilot-binary-build-conventions 设计决策 D1 曾把本接线类记为 Groovy 例外，
 * 理由是托管版本扩展类型不在 buildSrc 编译类路径、只能动态属性访问。consolidate-binary-conventions
 * 任务 7.2 依用户裁决"约定插件全部 Java 实现"撤销该例外：io.spring.dependency-management 的 jar
 * 改由 buildSrc implementation 依赖供给（探针三实测三判据：带类型编译、按 id 运行时应用、扩展
 * 实例类加载器与 buildSrc 同一，均通过），本类随之转 Java 并对该扩展取编译期类型引用。
 * 零发布合同的"声明不发布的工程不得属于发布线"成员资格自检随声明即时执行，见
 * {@link ModuleSetting#enableUnpublished()}（其读取源在本册任务 4.2 重锚至
 * {@link ProjectsExtension}，此前曾因根 ext 退役而静默空转，教训完整记录见该方法 javadoc）。
 */
public class ModuleCheckerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ProjectsExtension projectsExt = project.getExtensions().getByType(ProjectsExtension.class);

        // 组号对账（时机与原脚本一致取 gradle.projectsEvaluated）
        project.getGradle().projectsEvaluated(gradle -> {
            List<GroupAlignmentCheck.ProjectFact> facts = new ArrayList<>();
            for (Project m : projectsExt.moduleProjects()) {
                // configure-on-demand 下仅校验本次已评估的工程，未评估工程组号尚未派生、不参与对账
                if (!m.getState().getExecuted()) {
                    continue;
                }
                facts.add(new GroupAlignmentCheck.ProjectFact(m.getPath(), m.getName(),
                        m.getGroup().toString(),
                        m.getBuildFile() != null && m.getBuildFile().exists(),
                        projectsExt.isGradlePlugin(m)));
            }
            List<String> violations = GroupAlignmentCheck.violations(facts,
                    projectsExt.getProjectGroup(), projectsExt.getPluginLegacyGroup());
            if (!violations.isEmpty()) {
                throw new GradleException(GroupAlignmentCheck.aggregate(violations));
            }
        });

        // 零发布合同（consolidate-module-modes-plugin D2：依赖边就地核对）。与旧的"根清单 +
        // projectsEvaluated"相比：按需配置评估下不再有"清单缺员静默漏检"盲区（合法性经探针实证：
        // afterEvaluate 窗口内 evaluationDependsOn 可用）；"声明不发布的工程不得属于发布线"的
        // 成员资格自检在声明处即时执行，见 ModuleSetting.enableUnpublished。
        // 分工（复验 CRITICAL-2 修复）：本段只负责生命周期——逐边解析目标、强制评估、查角色
        // 声明位；映射判定与报错文案在 UnpublishedContractCheck.violations（红绿两向有单测）。
        for (Project m : projectsExt.moduleProjects()) {
            m.afterEvaluate(evaluated -> {
                List<UnpublishedContractCheck.ProjectEdge> edges = new ArrayList<>();
                evaluated.getConfigurations().forEach(cfg ->
                        cfg.getDependencies().withType(ProjectDependency.class).forEach(d -> {
                            // getDependencyProject 已弃用（Gradle 9 移除），以工程路径解析等价替代
                            Project target = evaluated.getRootProject().project(d.getPath());
                            if (target.equals(evaluated)) {
                                // 自依赖边不进强制评估分支（对自身发起 evaluationDependsOn 非法）；
                                // 判定函数内的同名自依赖跳过规则保留作双保险（D8 注记）
                                edges.add(new UnpublishedContractCheck.ProjectEdge(
                                        evaluated.getPath(), cfg.getName(), target.getPath(), false));
                                return;
                            }
                            if (!target.getState().getExecuted()) {
                                evaluated.evaluationDependsOn(target.getPath());
                            }
                            edges.add(new UnpublishedContractCheck.ProjectEdge(
                                    evaluated.getPath(), cfg.getName(), target.getPath(),
                                    ModuleSetting.enabled(target, ModuleSetting.Mode.UNPUBLISHED)));
                        }));
                List<String> violations = UnpublishedContractCheck.violations(edges);
                if (!violations.isEmpty()) {
                    throw new GradleException(violations.get(0));
                }
            });
        }

        // 托管版本面对账守卫。时机与组号对账一致取 gradle.projectsEvaluated；
        // configure-on-demand 下仅核对本次已评估且带托管扩展的工程。
        project.getGradle().projectsEvaluated(gradle -> {
            Map<String, String> declaredValues = new LinkedHashMap<>();
            for (String versionKey : ManagedVersionsCheck.GUARD_COORDS.keySet()) {
                declaredValues.put(versionKey, project.property(versionKey).toString());
            }
            List<ManagedVersionsCheck.ProjectManagedView> views = new ArrayList<>();
            for (Project p : project.getAllprojects()) {
                if (!p.getState().getExecuted()) {
                    continue;
                }
                // 类型化取回（consolidate-binary-conventions D2 类型化路线）：替代原
                // findByName + Groovy 动态属性访问，编译期即锁定扩展契约
                DependencyManagementExtension dm =
                        p.getExtensions().findByType(DependencyManagementExtension.class);
                if (dm == null) {
                    continue;
                }
                Map<String, String> managed = new LinkedHashMap<>(dm.getManagedVersions());
                views.add(new ManagedVersionsCheck.ProjectManagedView(p.getPath(), managed));
            }
            List<String> drifts = ManagedVersionsCheck.drifts(declaredValues, views);
            if (!drifts.isEmpty()) {
                throw new GradleException(ManagedVersionsCheck.aggregate(drifts));
            }
        });
    }
}
