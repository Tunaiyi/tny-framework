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

package tny.convention.releaseops;

import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * IntegrationGateCheck 分支流转门禁判定的单元测试（convert-orchestration-to-java 任务 4.1，
 * capability branch-integration-gates 三执法点各覆盖通过向与违例向）。
 */
class IntegrationGateCheckTest {

    @Test
    void integrateMainRequiresDevBranch() {
        assertDoesNotThrow(() -> IntegrationGateCheck.requireDevBranch("dev/5.7.x"));
        GradleException ex = assertThrows(GradleException.class,
                () -> IntegrationGateCheck.requireDevBranch("5.7.x"));
        assertTrue(ex.getMessage().contains("当前分支 '5.7.x'"));
    }

    @Test
    void integrationOrderRefusesLowerInFlightLines() {
        assertDoesNotThrow(() -> IntegrationGateCheck.assertIntegrationOrder(
                List.of("dev/5.7.x", "dev/5.8.x"), "dev/5.7.x", 5007), "更高编号在途不拦");
        GradleException ex = assertThrows(GradleException.class,
                () -> IntegrationGateCheck.assertIntegrationOrder(
                        List.of("dev/5.6.x", "dev/5.7.x"), "dev/5.7.x", 5007));
        assertTrue(ex.getMessage().contains("dev/5.6.x") && ex.getMessage().contains("集成顺序检查失败"),
                "须指名低编号在途线");
        assertDoesNotThrow(() -> IntegrationGateCheck.assertIntegrationOrder(
                List.of("dev/5.7.x", "main", "release/5.5.x"), "dev/5.7.x", 5007), "非 dev 形态不参与");
    }

    @Test
    void mergeUpwardRequiresMaintenanceSource() {
        assertDoesNotThrow(() -> IntegrationGateCheck.requireMaintenanceSource("release/5.7.x"));
        assertTrue(assertThrows(GradleException.class,
                () -> IntegrationGateCheck.requireMaintenanceSource("dev/5.7.x")).getMessage()
                .contains("不走本任务"));
    }

    @Test
    void completenessCheckComputesMissingAndRefuses() {
        List<String> markers = List.of("BUG-101", "CVE-2");
        assertEquals(List.of("CVE-2"),
                IntegrationGateCheck.missingMarkers(markers, List.of(List.of("head:2"), List.of("head:0"))));
        // 保守语义（实现钉住并在接线注释成文）：命中行清单截断或全非数字时，
        // 其后标记一律按缺失处理——防"检索失败被当无缺失"（JMH 空列表不报错教训同型）。
        assertEquals(List.of("BUG-101", "CVE-2"),
                IntegrationGateCheck.missingMarkers(markers, List.of(List.of("x:notnum"), List.of())));
        // 多行命中求和（grep -c 每文件一行）：两行各 0 命中仍判缺失，任一行有数字即放行
        assertEquals(List.of(), IntegrationGateCheck.missingMarkers(
                List.of("BUG-101"), List.of(List.of("a.java:0", "b.java:2"))));
        assertDoesNotThrow(() -> IntegrationGateCheck.assertNoMissingMarkers(
                "release/5.7.x", "main", List.of()));
        GradleException ex = assertThrows(GradleException.class,
                () -> IntegrationGateCheck.assertNoMissingMarkers(
                        "release/5.7.x", "main", List.of("CVE-2")));
        assertTrue(ex.getMessage().contains("合并结果完整性检查拦截 release/5.7.x -> main")
                && ex.getMessage().contains("检索不到标记 CVE-2"));
    }

    @Test
    void retireGuardTwoChecksRefuseRespectively() {
        assertTrue(assertThrows(GradleException.class,
                () -> IntegrationGateCheck.requireRetireCandidateBranch("dev/5.7.x")).getMessage()
                .contains("待退役的 release 维护分支"));
        GradleException uncollected = assertThrows(GradleException.class,
                () -> IntegrationGateCheck.assertNoUncollected("release/5.7.x",
                        List.of("abc123 fix: BUG-9")));
        assertTrue(uncollected.getMessage().contains("1 笔未集成提交") && uncollected.getMessage().contains("mergeUpward"));
        assertDoesNotThrow(() -> IntegrationGateCheck.assertNoUncollected("release/5.7.x", List.of()));
        assertTrue(assertThrows(GradleException.class,
                () -> IntegrationGateCheck.assertLineageDispositioned("release/5.7.x",
                        List.of("| release/5.6.x | 已退役 | 理由 |"))).getMessage()
                .contains("线谱系登记表中没有 'release/5.7.x' 的行"));
        assertDoesNotThrow(() -> IntegrationGateCheck.assertLineageDispositioned("release/5.7.x",
                List.of("pre | release/5.7.x | 已退役 | 处置登记 | post")));
    }
}
