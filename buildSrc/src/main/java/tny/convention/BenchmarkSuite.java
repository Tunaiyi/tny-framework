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

package tny.convention;

import org.gradle.api.provider.ListProperty;

/**
 * 基准族清单配置面（tny.benchmark-module 的五个数据属性，declarative-it-demo-isolation design D5）。
 * 族属双承载（split-bench-suites D1）：目录分置族子包即族属，此处按族子包正则声明清单，
 * CI 两面（评审枚举 / 执行选择）只从这份单一事实派生；目录与声明的对账由 jmhSuiteVerify 钉死。
 * 各条目的行为来由注释（JMH 拼接语义、-p 筛选实测等）留在 tny.benchmark-module 插件内。
 *
 * <p>载体沿革：Groovy 抽象类经 consolidate-assembly-line 任务 8.2 转 Java（managed property
 * 抽象类形态两语言等价，声明面 benchmarkSuite {} 块的属性赋值语义不变）。
 */
public abstract class BenchmarkSuite {

    /** 常规族成员正则（单一 alternation 语义：多正则自拼竖线由插件承担，插件会逗号拼接实测在前）。 */
    public abstract ListProperty<String> getRoutineFamily();

    /** devtest 族成员正则（jmhSuiteVerify 归族对账用）。 */
    public abstract ListProperty<String> getDevtestFamily();

    /** 设施探针：devtest 族成员，恒随执行面运行——执行链存活哨兵，其条目不构成框架性能断言对象。 */
    public abstract ListProperty<String> getFacilityProbes();

    /** 常规族生产臂参数域（-p 覆写而非过滤，理由见插件内来由注释）。 */
    public abstract ListProperty<String> getRoutineAlgoArms();

    /**
     * 速览排除清单：完整规模 include 面上需要退出合入计时速览的基准类正则（矩阵类在此登记，
     * 新增矩阵类只加这一行——负向前瞻烙类名的静默失效陷阱由此消灭）。
     */
    public abstract ListProperty<String> getQuickRunExcludes();
}
