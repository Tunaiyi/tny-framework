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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 基准族属双向对账判定（jmhSuiteVerify 的判定本体纯函数，迁移自 tny.benchmark-module.gradle
 * 原第 74 至 101 行 doLast 的归族与四段判红；split-bench-suites D1 与族清单语义的机器面）。
 *
 * <p>正则匹配语义逐点判例（consolidate-assembly-line 组 9.2 判例表本册条目）：原 Groovy 形态
 * 全部用 {@code c =~ p}（find 部分匹配语义），Java 化对应 {@link java.util.regex.Matcher#find()}；
 * MUST NOT 误用 matches()（全串语义会让一切带前缀模式静默不命中——tny.integrate 的
 * num 闭包事故同型教训，修复提交见归档前史实）。
 */
public final class BenchmarkSuiteCheck {

    /** 归族统计结果（对账摘要行数据源）。 */
    public record Summary(List<String> unclassified, List<String> multiFamily,
                          long routineCount, long devtestCount, List<String> probeHits) {
    }

    private BenchmarkSuiteCheck() {
    }

    /** 全量枚举类名去重（每行去末段方法名取类全名，保持声明序）。 */
    public static List<String> distinctClasses(List<String> listLines) {
        LinkedHashSet<String> classes = new LinkedHashSet<>();
        for (String line : listLines) {
            if (!line.startsWith("com.")) {
                continue;
            }
            classes.add(line.substring(0, line.lastIndexOf('.')));
        }
        return new ArrayList<>(classes);
    }

    /** 族归属判定：命中族正则清单的名字集合（find 语义）。 */
    public static List<String> familiesOf(String className, List<String> routineFamily,
                                          List<String> devtestFamily) {
        List<String> hits = new ArrayList<>();
        if (anyMatch(className, routineFamily)) {
            hits.add("routine");
        }
        if (anyMatch(className, devtestFamily)) {
            hits.add("devtest");
        }
        return hits;
    }

    /** 输入去重类名与三张族清单，输出问题清单（空即放行）；四段判红文案逐字承原脚本。 */
    public static List<String> violations(List<String> classes, List<String> routineFamily,
                                          List<String> devtestFamily, List<String> facilityProbes) {
        List<String> problems = new ArrayList<>();
        List<String> unclassified = new ArrayList<>();
        List<String> multiFamily = new ArrayList<>();
        long routineCount = 0;
        long devtestCount = 0;
        for (String className : classes) {
            List<String> families = familiesOf(className, routineFamily, devtestFamily);
            if (families.isEmpty()) {
                unclassified.add(className);
            }
            if (families.size() > 1) {
                multiFamily.add(className);
            }
            if (families.equals(List.of("routine"))) {
                routineCount++;
            }
            if (families.equals(List.of("devtest"))) {
                devtestCount++;
            }
        }
        if (!unclassified.isEmpty()) {
            problems.add("基准类未归族（目录不在族子包，或族清单正则漂移）：" + unclassified);
        }
        if (!multiFamily.isEmpty()) {
            problems.add("基准类同时命中多族清单（族界须互斥）：" + multiFamily);
        }
        if (routineCount == 0 || devtestCount == 0) {
            problems.add("族清单空面：routine=" + routineCount + ", devtest=" + devtestCount);
        }
        List<String> probes = new ArrayList<>();
        for (String className : classes) {
            if (anyMatch(className, facilityProbes)) {
                probes.add(className);
            }
        }
        if (probes.isEmpty()) {
            problems.add("设施探针清单无命中：" + facilityProbes + "——Smoke 搬偏或清单漂移");
        }
        return problems;
    }

    /** 摘要行（对账通过后的 lifecycle 输出数据）。 */
    public static Summary summaryOf(List<String> classes, List<String> routineFamily,
                                    List<String> devtestFamily, List<String> facilityProbes) {
        List<String> unclassified = new ArrayList<>();
        List<String> multiFamily = new ArrayList<>();
        long routineCount = 0;
        long devtestCount = 0;
        List<String> probes = new ArrayList<>();
        for (String className : classes) {
            List<String> families = familiesOf(className, routineFamily, devtestFamily);
            if (families.isEmpty()) {
                unclassified.add(className);
            }
            if (families.size() > 1) {
                multiFamily.add(className);
            }
            if (families.equals(List.of("routine"))) {
                routineCount++;
            }
            if (families.equals(List.of("devtest"))) {
                devtestCount++;
            }
            if (anyMatch(className, facilityProbes)) {
                probes.add(className);
            }
        }
        return new Summary(unclassified, multiFamily, routineCount, devtestCount, probes);
    }

    private static boolean anyMatch(String value, List<String> patterns) {
        for (String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
