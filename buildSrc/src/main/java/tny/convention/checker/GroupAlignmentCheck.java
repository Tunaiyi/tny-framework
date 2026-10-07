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

package tny.convention.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布构件组号对账与构建文件存在性断言的判定逻辑（tny.module-checker 二进制化第一检查，
 * 规格出处 central-publishing 设计决策 D2 与 fix-dependency-version-governance 设计决策 D8；
 * 判定语义逐字迁移自原预编译脚本，pilot-binary-build-conventions 任务 5.1）。
 *
 * <p>本类是纯输入输出判定：输入工程事实记录与两个组号事实值，输出问题清单字符串列表，
 * 不接触 Gradle 生命周期对象；projectsEvaluated 时机、configure-on-demand 下已评估过滤、
 * 事实记录采集都在 ModuleCheckerPlugin 接线类完成。例外组号判定走 gradlePlugin 布尔位
 * （由接线类经 ProjectsExtension.isGradlePlugin 求得），本类不重复书写后缀字面量。
 */
public final class GroupAlignmentCheck {

    /** 单个成员工程的对账输入事实。 */
    public record ProjectFact(String path, String name, String group, boolean buildFileExists, boolean gradlePlugin) {
    }

    private GroupAlignmentCheck() {
    }

    /** 逐项核对并返回问题清单（空清单即对账通过）；聚合报错文案见 aggregate()。 */
    public static List<String> violations(List<ProjectFact> facts, String projectGroup, String pluginLegacyGroup) {
        List<String> violations = new ArrayList<>();
        for (ProjectFact fact : facts) {
            if (!fact.buildFileExists()) {
                // 装配线成员构建文件存在性断言（fix-dependency-version-governance D8）：settings 引入而无
                // 构建文件的工程会以空工程形态过完整条装配线并产出只含清单的空构件（tny-game-codec-protoex 事故形态）
                violations.add(fact.path() + " 属于发布装配线但其构建文件不存在（settings.gradle 引入悬空）：删除该引入行，或补齐工程的构建文件");
            }
            if (fact.gradlePlugin()) {
                if (!fact.group().equals(pluginLegacyGroup)) {
                    violations.add(fact.path() + "（插件模块例外）组号应为 '" + pluginLegacyGroup + "'，实际 '" + fact.group() + "'");
                }
            } else if (!fact.group().equals(projectGroup)) {
                violations.add(fact.path() + " 组号应为单一事实源 '" + projectGroup + "'，实际 '" + fact.group()
                        + "'（请删除模块内组号声明，由根构建派生）");
            }
        }
        return violations;
    }

    /** 聚合抛错文案，与原脚本 GradleException 消息逐字一致。 */
    public static String aggregate(List<String> violations) {
        return "发布构件配置期对账失败（组号单一事实源与构建文件存在性）：\n- " + String.join("\n- ", violations);
    }
}
