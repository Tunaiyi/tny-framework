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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 零发布合同报错文案判定的单元测试（pilot-binary-build-conventions 任务 5.1）。
 * 本类只测文案判定的红方向与判定输入契约；"沿依赖边强制评估后读角色声明"的端到端语义
 * 由主构建破坏探针验证（设计决策 D3 的覆盖分工），不在单测范围。
 */
class UnpublishedContractCheckTest {

    @Test
    void violationNamesBothPartiesAndDeclarationSource() {
        String message = UnpublishedContractCheck.violation(":tny-game-rpc", "implementation", ":tny-benchmark");
        assertEquals("零发布合同违例：:tny-game-rpc 的配置 implementation 依赖声明不发布工程 "
                + "':tny-benchmark'（声明处：tny.module-setting enableUnpublished；见 benchmark-harness 规格）", message);
    }

    @Test
    void messageCarriesEvaluatorConfigurationAndTarget() {
        String message = UnpublishedContractCheck.violation(":a", "api", ":b");
        // 判红对象与理由的三要素缺一不可：依赖方路径、命中配置名、被依赖方路径
        org.junit.jupiter.api.Assertions.assertAll(
                () -> assertEquals(true, message.contains(":a")),
                () -> assertEquals(true, message.contains("api")),
                () -> assertEquals(true, message.contains("':b'")));
    }
}
