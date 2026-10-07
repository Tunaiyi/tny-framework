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

package tny.convention.benchmark;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BenchmarkSuiteCheck 的单元测试（consolidate-assembly-line 任务 8.1，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求）：通过用例覆盖全量归族、
 * 族界互斥与探针命中；违例用例覆盖未归族、多族命中、族空面、探针失配四段报红与
 * find 匹配语义（族正则为部分匹配——类名带方法段前后缀仍能归族，与原 Groovy 等号波浪线形态同判）。
 */
class BenchmarkSuiteCheckTest {

    private static final List<String> ROUTINE = List.of(".*\\.benchmark\\.net\\.routine\\..*");
    private static final List<String> DEVTEST = List.of(".*\\.benchmark\\.net\\.devtest\\..*");
    private static final List<String> PROBES =
            List.of(".*\\.benchmark\\.net\\.devtest\\.SmokeBenchmark.*");

    @Test
    void fullyClassifiedSuitePassesAndCountsByFamily() {
        List<String> lines = List.of(
                "com.tny.game.benchmark.net.routine.RelayBenchmark.relay1",
                "com.tny.game.benchmark.net.devtest.SmokeBenchmark.smoke",
                "com.tny.game.benchmark.net.devtest.OtherBenchmark.other");
        List<String> classes = BenchmarkSuiteCheck.distinctClasses(lines);
        assertEquals(List.of("com.tny.game.benchmark.net.routine.RelayBenchmark",
                "com.tny.game.benchmark.net.devtest.SmokeBenchmark",
                "com.tny.game.benchmark.net.devtest.OtherBenchmark"), classes,
                "去末段方法名并保持声明序去重");
        assertTrue(BenchmarkSuiteCheck.violations(classes, ROUTINE, DEVTEST, PROBES).isEmpty());
        BenchmarkSuiteCheck.Summary summary =
                BenchmarkSuiteCheck.summaryOf(classes, ROUTINE, DEVTEST, PROBES);
        assertEquals(1, summary.routineCount());
        assertEquals(2, summary.devtestCount());
        assertEquals(1, summary.probeHits().size());
    }

    @Test
    void unclassifiedMultiFamilyEmptyFamilyAndProbeMissEachFailLoud() {
        List<String> unclassified = List.of("com.tny.game.benchmark.net.orphan.OrphanBenchmark");
        assertTrue(BenchmarkSuiteCheck.violations(unclassified, ROUTINE, DEVTEST, PROBES).get(0)
                .contains("基准类未归族"));
        List<String> multi = List.of("com.tny.game.benchmark.net.routine.routine.X");
        List<String> bothFamilies = List.of(
                "com.tny.game.benchmark.net.routine.routine.MultiHitBenchmark");
        // 故意让两族模式同时命中同一名字，验证多族报红
        List<String> problems = BenchmarkSuiteCheck.violations(bothFamilies,
                List.of(".*\\.routine\\..*"), List.of(".*routine\\..*"), List.of(".*never-match.*"));
        assertTrue(problems.stream().anyMatch(p -> p.contains("同时命中多族清单")), "多族命中须报红");
        // 族空面：只有 routine 没有 devtest 也须报红
        List<String> onlyRoutine = List.of("com.tny.game.benchmark.net.routine.A");
        assertTrue(BenchmarkSuiteCheck.violations(onlyRoutine, ROUTINE, DEVTEST, PROBES).stream()
                .anyMatch(p -> p.contains("族清单空面")));
        // 探针失配独立报红（族齐但探针清单漂移）
        List<String> suite = List.of("com.tny.game.benchmark.net.routine.A",
                "com.tny.game.benchmark.net.devtest.B");
        assertTrue(BenchmarkSuiteCheck.violations(suite, ROUTINE, DEVTEST, List.of(".*gone.*"))
                .get(0).contains("设施探针清单无命中"));
    }
}
