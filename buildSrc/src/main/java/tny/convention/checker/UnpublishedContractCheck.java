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
 * 零发布合同的判定逻辑与报错文案（consolidate-module-modes-plugin 设计决策 D2 的依赖边就地核对；
 * 判定语义逐字迁移自原预编译脚本，pilot-binary-build-conventions 任务 5.1；复验 CRITICAL-2 修复后
 * 本类承载完整的映射判定——输入工程依赖边清单与目标不发布事实位，输出违例问题清单，红绿两向都有
 * 单元测试，接线类只保留 Gradle 生命周期部分）。
 *
 * <p>沿依赖边强制评估被依赖方、读取 ModuleSetting 角色声明的时机语义留在接线类
 * ModuleCheckerPlugin——那部分依赖 Gradle 评估生命周期，其端到端语义由主构建的破坏探针验证
 * （pilot-binary-build-conventions 设计决策 D3 的覆盖分工）；本类判定函数与之的分工是：
 * 接线类把每条边求值为 ProjectEdge 事实记录（targetUnpublished 位由 ModuleSetting.enabled
 * 查得），本类对事实清单做纯映射判定。报错时机为单个工程评估收尾后统一抛首条违例，
 * 报错文案与原脚本逐字一致。
 */
public final class UnpublishedContractCheck {

    /** 单条工程依赖边的对账输入事实：依赖方路径、命中配置名、被依赖方路径与其不发布声明位。 */
    public record ProjectEdge(String evaluatorPath, String configurationName, String targetPath, boolean targetUnpublished) {
    }

    private UnpublishedContractCheck() {
    }

    /**
     * 对边清单做映射判定，返回违例文案列表（空清单即合同成立）。
     * 自依赖边（依赖目标是自身）不判红——该规则同时存在于接线类（防对未完成的自身再发起强制
     * 评估）与本类（测试内双保险复现），与 fix-dependency-version-governance 设计决策 D8 注记一致。
     */
    public static List<String> violations(List<ProjectEdge> edges) {
        List<String> violations = new ArrayList<>();
        for (ProjectEdge edge : edges) {
            if (edge.targetPath().equals(edge.evaluatorPath())) {
                continue;
            }
            if (edge.targetUnpublished()) {
                violations.add(violation(edge.evaluatorPath(), edge.configurationName(), edge.targetPath()));
            }
        }
        return violations;
    }

    /** 零发布合同违例的报错文案，与原脚本 GradleException 消息逐字一致。 */
    public static String violation(String evaluatorPath, String configurationName, String targetPath) {
        return "零发布合同违例：" + evaluatorPath + " 的配置 " + configurationName
                + " 依赖声明不发布工程 '" + targetPath + "'（声明处：tny.module-setting enableUnpublished；见 benchmark-harness 规格）";
    }
}
