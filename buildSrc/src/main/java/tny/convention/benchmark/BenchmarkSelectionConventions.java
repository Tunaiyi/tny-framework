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

import me.champeau.jmh.JmhParameters;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import tny.convention.BenchmarkSuite;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行面规模选择与参数覆写装配段（tny.benchmark-module 目录页第二段，迁移自
 * tny.benchmark-module.gradle 原第 122 至 169 行；move-bench-suite-selection-into-plugin——
 * gradle-build-style 需求一：分支与中间变量累加属程序性，收编约定插件；模块 jmh 块只留
 * 声明式静态基线参数）。
 *
 * <p>D1 次序契约（拆类不改序）：本方法内两个 afterEvaluate 的注册先后即回调执行先后——
 * 选择段在前、显式 -PbenchParams 覆写段居末，优先级承前最后；该次序与拆分前的单文件形态
 * 逐字一致，由 consolidate-assembly-line 组 8.4 的 -PbenchParams 单臂覆写内省回归佐证。
 */
final class BenchmarkSelectionConventions {

    private BenchmarkSelectionConventions() {
    }

    static void apply(Project project, BenchmarkSuite suite) {
        // 缺省执行＝常规族（含设施探针）＋六臂参数域，可复现；全量矩阵以 -PbenchAll 显式打开。
        project.afterEvaluate(checked -> applySelection(checked, suite));
        // 参数过滤经 JMH -p（include 正则不匹配参数串，实测证实）；分号分隔多字段。
        // 解析循环自模块 jmh{} 迁入本插件（gradle-build-style 需求一：程序性中间变量累加不落模块文件，
        // declarative-it-demo-isolation design D5 行数守恒项），语义保持"显式 benchParams 覆写参数域"。
        project.afterEvaluate(BenchmarkSelectionConventions::applyBenchParamsOverride);
    }

    private static void applySelection(Project project, BenchmarkSuite suite) {
        JmhParameters jmh = project.getExtensions().getByType(JmhParameters.class);
        List<String> familyList = new ArrayList<>(suite.getRoutineFamily().get());
        familyList.addAll(suite.getFacilityProbes().get());
        String familyInclude = String.join("|", familyList);
        if (project.hasProperty("benchAll")) {
            jmh.getIncludes().set(List.of(".*"));
        } else if (project.hasProperty("benchInclude")) {
            jmh.getIncludes().set(List.of(project.property("benchInclude").toString()));
        } else if (project.hasProperty("benchScope")
                && "quick".equals(project.property("benchScope").toString())) {
            // 速览规模（refine-bench-routine-triggering D2）：与完整档共用 include 面，
            // 矩阵级退化由夜间与手动的完整规模兜底；排除面是显式清单（新增矩阵类在此登记）
            jmh.getIncludes().set(List.of(familyInclude));
            jmh.getExcludes().set(suite.getQuickRunExcludes().get());
        } else {
            // 缺省即完整规模（本地直接执行、夜间定时、手动触发）：常规族并设施探针，
            // 两族清单自拼竖线成单正则（插件逗号拼接语义），六臂参数域覆写沿用。
            jmh.getIncludes().set(List.of(familyInclude));
            jmh.getBenchmarkParameters().put("algo", project.getObjects()
                    .listProperty(String.class).value(suite.getRoutineAlgoArms().get()));
        }
        if (project.hasProperty("benchGc")) {
            jmh.getProfilers().set(List.of("gc"));            // 分配画像（P3 裁决数据源）
        }
        if (project.hasProperty("benchFast")) {
            // 选择面探针：只验证"哪些 benchmark 会被选中"，不出可信数字。
            jmh.getFork().set(1);
            jmh.getWarmupIterations().set(0);
            jmh.getIterations().set(1);
            jmh.getWarmup().set("100ms");
            jmh.getTimeOnIteration().set("100ms");
        }
    }

    private static void applyBenchParamsOverride(Project project) {
        if (!project.hasProperty("benchParams")) {
            return;
        }
        Map<String, String> parsed = new LinkedHashMap<>();
        for (String pair : project.property("benchParams").toString().split(";")) {
            String[] entry = pair.split("=", 2);
            parsed.put(entry[0], entry[1]);
        }
        JmhParameters jmh = project.getExtensions().getByType(JmhParameters.class);
        Map<String, ListProperty<String>> replacement = new LinkedHashMap<>();
        parsed.forEach((key, value) -> replacement.put(key,
                project.getObjects().listProperty(String.class).value(List.of(value))));
        // MapProperty.set(Map) 即 Groovy 形态整图赋值的等价（覆写参数域，承前居末）
        jmh.getBenchmarkParameters().set(replacement);
    }
}
