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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 托管版本面对账判定的单元测试（pilot-binary-build-conventions 任务 5.1）：覆盖生效值与
 * 声明值一致方向、失配方向、代表坐标从托管面消失方向，以及超 12 条聚合截断文案。
 * 守卫坐标表覆盖全部八个族（真实构建的 gradle.properties 八键恒全，判定逻辑对每族恒扫描），
 * 夹具据此构造全族一致基线再注入单点扰动。真实 BOM 导入次序漂移的端到端形态按设计决策 D7
 * 挂观察账，不在单测范围。
 */
class ManagedVersionsCheckTest {

    private static final String NETTY = "nettyVersion";

    /** 全族声明值基线：每族版本键给族名加后缀的可读值。 */
    private static Map<String, String> fullDeclared() {
        Map<String, String> declared = new HashMap<>();
        ManagedVersionsCheck.GUARD_COORDS.keySet().forEach(key -> declared.put(key, "v-" + key));
        return declared;
    }

    /** 全族托管生效值基线：每族代表坐标都等于对应声明值；excludeCoord 为单点扰动挖除项。 */
    private static Map<String, String> fullManaged(Map<String, String> declared, String overrideCoord, String excludeCoord) {
        Map<String, String> managed = new HashMap<>();
        declared.forEach((key, value) -> {
            for (String coord : ManagedVersionsCheck.GUARD_COORDS.get(key)) {
                if (!coord.equals(excludeCoord)) {
                    managed.put(coord, coord.equals(overrideCoord) ? "v-drifted" : value);
                }
            }
        });
        return managed;
    }

    @Test
    void effectiveValueEqualToDeclaredPasses() {
        Map<String, String> declared = fullDeclared();
        List<String> drifts = ManagedVersionsCheck.drifts(declared, List.of(
                new ManagedVersionsCheck.ProjectManagedView(":tny-game-net", fullManaged(declared, null, null))));
        assertTrue(drifts.isEmpty());
    }

    @Test
    void driftedValueIsRedNamingFamilyCoordinateAndValues() {
        Map<String, String> declared = fullDeclared();
        String coord = "io.netty:netty-common";
        List<String> drifts = ManagedVersionsCheck.drifts(declared, List.of(
                new ManagedVersionsCheck.ProjectManagedView(":tny-game-net", fullManaged(declared, coord, null))));
        assertEquals(1, drifts.size());
        assertTrue(drifts.get(0).contains(":tny-game-net 的族 '" + NETTY + "'"), "须指名判红工程与族");
        assertTrue(drifts.get(0).contains(coord), "须指名失配坐标");
        assertTrue(drifts.get(0).contains("v-" + NETTY) && drifts.get(0).contains("v-drifted"), "须列出声明值与生效值");
    }

    @Test
    void missingManagedCoordinateCountsAsDriftWithNoSourceWord() {
        Map<String, String> declared = fullDeclared();
        String coord = "io.netty:netty-buffer";
        List<String> drifts = ManagedVersionsCheck.drifts(declared, List.of(
                new ManagedVersionsCheck.ProjectManagedView(":tny-game-net", fullManaged(declared, null, coord))));
        assertEquals(1, drifts.size());
        assertTrue(drifts.get(0).contains("（该族无托管源）"));
        assertTrue(drifts.get(0).contains(coord));
    }

    @Test
    void aggregateTruncatesBeyondTwelveAndStatesTotal() {
        List<String> drifts = IntStream.rangeClosed(1, 13).mapToObj(i -> "失配" + i).collect(Collectors.toList());
        String message = ManagedVersionsCheck.aggregate(drifts);
        assertTrue(message.contains("失配1") && message.contains("失配12"), "前 12 条全列");
        assertFalse(message.contains("失配13"), "第 13 条不逐列");
        assertTrue(message.contains("……共 13 条，仅列前 12 条"), "截断须报总数");
    }
}
