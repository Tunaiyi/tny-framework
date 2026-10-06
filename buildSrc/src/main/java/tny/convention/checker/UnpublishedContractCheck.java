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

/**
 * 零发布合同的报错文案判定（consolidate-module-modes-plugin 设计决策 D2 的依赖边就地核对；
 * 判定语义逐字迁移自原预编译脚本，pilot-binary-build-conventions 任务 5.1）。
 *
 * <p>本类只承载"认定违例后输出什么报错"的判定；目标工程是否声明不发布（ModuleSetting 读取、
 * 沿依赖边强制评估的时机语义）留在接线类 ModuleCheckerPlugin——那部分依赖 Gradle 评估生命周期，
 * 其端到端语义由主构建的破坏探针验证（pilot-binary-build-conventions 设计决策 D3 的覆盖分工），
 * 本类单测覆盖违例文案与不违例方向。
 */
public final class UnpublishedContractCheck {

    private UnpublishedContractCheck() {
    }

    /** 零发布合同违例的报错文案，与原脚本 GradleException 消息逐字一致。 */
    public static String violation(String evaluatorPath, String configurationName, String targetPath) {
        return "零发布合同违例：" + evaluatorPath + " 的配置 " + configurationName
                + " 依赖声明不发布工程 '" + targetPath + "'（声明处：tny.module-setting enableUnpublished；见 benchmark-harness 规格）";
    }
}
