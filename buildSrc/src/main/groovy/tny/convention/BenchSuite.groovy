// 基准族清单配置面（tny.bench-suite 的五个数据属性，declarative-it-demo-isolation design D5）。
// 族属双承载（split-bench-suites D1）：目录分置族子包即族属，此处按族子包正则声明清单，
// CI 两面（评审枚举 / 执行选择）只从这份单一事实派生；目录↔声明对账由 jmhSuiteVerify 钉死。
// 各条目的行为来由注释（JMH 拼接语义、-p 筛选实测等）留在 tny.bench-suite 插件内。
package tny.convention

import org.gradle.api.provider.ListProperty

abstract class BenchSuite {

    // 常规族成员正则（单一 alternation 语义：多正则自拼 | 由插件承担，插件会逗号拼接实测在前）。
    abstract ListProperty<String> getRoutineFamily()

    // devtest 族成员正则（jmhSuiteVerify 归族对账用）。
    abstract ListProperty<String> getDevtestFamily()

    // 设施探针：devtest 族成员，恒随执行面运行——执行链存活哨兵，其条目不构成框架性能断言对象。
    abstract ListProperty<String> getFacilityProbes()

    // 常规族生产臂参数域（-p 覆写而非过滤，理由见插件内来由注释）。
    abstract ListProperty<String> getRoutineAlgoArms()

    // 速览排除清单：完整规模 include 面上需要退出合入计时速览的基准类正则（矩阵类在此登记，
    // 新增矩阵类只加这一行——负向前瞻烙类名的静默失效陷阱由此消灭）。
    abstract ListProperty<String> getQuickRunExcludes()

}
