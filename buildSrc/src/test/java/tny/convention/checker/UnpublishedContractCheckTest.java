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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 零发布合同映射判定的单元测试（pilot-binary-build-conventions 任务 5.1；复验 CRITICAL-2
 * 修复后覆盖红绿双向）：通过方向覆盖普通依赖边与自依赖边两种输入，违例方向断言判红对象
 * 与理由三要素。"沿依赖边强制评估后读角色声明"的生命周期语义由接线类承担、经主构建
 * 破坏探针验证（设计决策 D3 的覆盖分工），不在本测试范围。
 */
class UnpublishedContractCheckTest {

    @Test
    void publishedAndSelfEdgesPass() {
        List<UnpublishedContractCheck.ProjectEdge> edges = List.of(
                new UnpublishedContractCheck.ProjectEdge(":tny-game-rpc", "implementation", ":tny-game-common-lang", false),
                new UnpublishedContractCheck.ProjectEdge(":tny-game-tester", "testImplementation", ":tny-game-tester", true));
        assertTrue(UnpublishedContractCheck.violations(edges).isEmpty(),
                "普通依赖与自依赖（即使声明位为真）都不得判红");
    }

    @Test
    void emptyEdgeListPasses() {
        assertTrue(UnpublishedContractCheck.violations(List.of()).isEmpty());
    }

    @Test
    void multipleViolationsKeepInputOrderAndFirstIsReported() {
        List<UnpublishedContractCheck.ProjectEdge> edges = List.of(
                new UnpublishedContractCheck.ProjectEdge(":tny-game-a", "implementation", ":tny-unpub-first", true),
                new UnpublishedContractCheck.ProjectEdge(":tny-game-b", "api", ":tny-unpub-second", true));
        List<String> violations = UnpublishedContractCheck.violations(edges);
        assertEquals(2, violations.size(), "两条违例边都须入账");
        assertTrue(violations.get(0).contains(":tny-unpub-first"), "接线类取首条抛出——首条须按输入边序选中");
    }

    @Test
    void violationNamesBothPartiesAndDeclarationSource() {
        List<UnpublishedContractCheck.ProjectEdge> edges = List.of(
                new UnpublishedContractCheck.ProjectEdge(":tny-game-rpc", "implementation", ":tny-benchmark", true));
        List<String> violations = UnpublishedContractCheck.violations(edges);
        assertEquals(1, violations.size());
        assertEquals("零发布合同违例：:tny-game-rpc 的配置 implementation 依赖声明不发布工程 "
                + "':tny-benchmark'（声明处：tny.module-setting enableUnpublished；见 benchmark-harness 规格）",
                violations.get(0));
    }

    @Test
    void messageCarriesEvaluatorConfigurationAndTarget() {
        String message = UnpublishedContractCheck.violation(":a", "api", ":b");
        // 判红对象与理由的三要素缺一不可：依赖方路径、命中配置名、被依赖方路径
        assertAll(
                () -> assertTrue(message.contains(":a")),
                () -> assertTrue(message.contains("api")),
                () -> assertTrue(message.contains("':b'")));
    }
}
