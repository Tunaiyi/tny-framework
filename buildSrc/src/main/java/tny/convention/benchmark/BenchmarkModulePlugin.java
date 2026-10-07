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
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import tny.convention.BenchmarkSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基准族执行通道约定插件（id {@code tny.benchmark-module}；adopt-gradle-official-dsl 插件化，
 * 前身为 gradle/bench-suite.gradle；declarative-it-demo-isolation 起族清单数据外提为
 * benchmarkSuite 扩展，由 :tny-benchmark 模块声明。consolidate-assembly-line 任务 8.1-8.3
 * 由预编译脚本同名 Java 化——id 保留，因 tny-benchmark 模块 plugins 块按 id 应用，脚本删除与
 * 注册行新增、jmh 插件供给迁移与模块版本号退役在同一提交）。
 *
 * <p>本插件负责：benchmarkSuite 扩展贡献与行为来由注释、jmhList/jmhListVerify/jmhSuiteVerify
 * 枚举对账任务与 benchRoutineExport 落盘任务，注册归属 :tny-benchmark 工程；任务名历次迁移不变，
 * 工程路径的模块段自 rename-bench-to-benchmark 起为 tny-benchmark。
 *
 * <p>目录页（每项行为指名所在类与方法）：
 * <ol>
 * <li>本类任务注册段——jmhList（枚举清单，ext.listFile 动态属性以 final 局部变量承载等价替代，
 *     任务名与 finalizedBy 边不变）、jmhListVerify（空匹配判红）、jmhSuiteVerify（接线后判定在
 *     {@link BenchmarkSuiteCheck}）、benchRoutineExport（落盘与日期指纹）；</li>
 * <li>本类两段 afterEvaluate（D1 注册次序即覆写优先级：选择段在前、benchParams 覆写段居末）；</li>
 * <li>{@link BenchmarkSuiteCheck}——族属双向对账判定纯函数（红绿单测在 DemoIsolationCheckTest 同目录）。</li>
 * </ol>
 *
 * <p>配置面：benchmarkSuite 五清单（{@link BenchmarkSuite}）；清单语义的双承载出处见 jmhSuiteVerify
 * 注释。边界：jmh 静态参数与依赖声明在模块构建文件，执行面规模选择在本插件 afterEvaluate
 * （move-bench-suite-selection-into-plugin，gradle-build-style 需求一）；族目录与清单对账语义出处见
 * split-bench-suites 与 refine-bench-routine-triggering。
 *
 * <p>正则拼接来由（自模块 ext 迁入，机制侧知识）：插件会把 includes 列表逗号拼接成单个 JMH 位置参数
 * （实测），而 JMH 位置参数语义为"一个正则"——多正则必须自拼竖线；臂属筛选走 -p 而非 include 正则
 * （实测：":prod_" 分支 0 选中；JMH 1.37 的 -p 语义是覆写参数域而非过滤，对未声明该字段的基准
 * 无排除效应）；速览档与完整档共用同一 include 面，差异只在 quickRunExcludes 显式清单
 * （矩阵类负向前瞻正则的静默失效陷阱由此消灭，refine-bench-routine-triggering D2）。
 */
public class BenchmarkModulePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        BenchmarkSuite suite = project.getExtensions()
                .create("benchmarkSuite", BenchmarkSuite.class);

        File listFile = new File(project.getLayout().getBuildDirectory()
                .dir("tmp/jmhList").get().getAsFile(), "jmhList.txt");

        // 列清单（锚集定义与 jmh 运行同源 benchmarkSuite 扩展）：CI PR 通道用它证明"清单可枚举"。
        // BenchmarkList 由插件 jmhRunBytecodeGenerator 产出于 generatedResourcesDir（非注解处理器阶段）。
        project.getTasks().register("jmhList", JavaExec.class, task -> {
            task.setGroup("benchmark");
            task.setDescription("List JMH benchmarks honoring routine-suite filters. 全量清单：-PbenchAll");
            task.dependsOn("jmhCompileGeneratedClasses");
            task.getMainClass().set("org.openjdk.jmh.Main");
            task.setArgs(new ArrayList<>(List.of("-l")));
            task.doFirst(running -> {
                JavaExec exec = (JavaExec) running;
                Object generatedResourcesDir = generatedResourcesDirOf(
                        project.getTasks().named("jmhRunBytecodeGenerator").get());
                SourceSet jmhSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
                        .getSourceSets().getByName("jmh");
                FileCollection runtime = jmhSourceSet.getRuntimeClasspath()
                        .plus(project.files(generatedResourcesDir));
                exec.setClasspath(runtime);
                if (!project.hasProperty("benchAll")) {
                    List<String> args = new ArrayList<>();
                    args.add("-l");
                    if (project.hasProperty("benchInclude")) {
                        args.add(project.property("benchInclude").toString());
                    } else {
                        args.addAll(suite.getRoutineFamily().get());
                    }
                    exec.setArgs(args);
                }
                try {
                    listFile.getParentFile().mkdirs();
                    exec.setStandardOutput(new FileOutputStream(listFile));
                } catch (IOException failure) {
                    throw new UncheckedIOException(failure);
                }
            });
            task.finalizedBy("jmhListVerify");
        });

        // 空匹配不可静默绿（spec：名称过滤无匹配不误报绿——JMH -l 空列表退 0，此处显式判红）
        project.getTasks().register("jmhListVerify", task -> {
            task.setGroup("benchmark");
            task.doLast(t -> {
                long n = countCompanion(listFile);
                if (n == 0) {
                    throw new GradleException("jmhList 过滤后无任何匹配（JMH -l 空列表不报错，此处显式判红）："
                            + "检查 benchmarkSuite.routineFamily 与 -PbenchAll");
                }
                t.getLogger().lifecycle("jmhList: {} 个基名条目", n);
            });
        });

        // 族属完备性与目录双向对账（split-bench-suites D1/3.2）：全量枚举逐类归族；
        // 未归族（搬而未登记或族正则漂移）、多族命中、族空面、探针失配皆显式判红
        // （JMH -l 空列表不报错教训沿用）。判定本体在 BenchmarkSuiteCheck（红绿单测覆盖）。
        File suiteListFile = new File(project.getLayout().getBuildDirectory()
                .dir("tmp/jmhSuiteVerify").get().getAsFile(), "suiteList.txt");
        project.getTasks().register("jmhSuiteVerify", JavaExec.class, task -> {
            task.setGroup("benchmark");
            task.setDescription("Verify every benchmark class is directory-classified (routine/devtest), "
                    + "suites non-empty, facility probe resolves.");
            task.dependsOn("jmhCompileGeneratedClasses");
            task.getMainClass().set("org.openjdk.jmh.Main");
            task.setArgs(List.of("-l"));
            task.doFirst(running -> {
                JavaExec exec = (JavaExec) running;
                Object generatedResourcesDir = generatedResourcesDirOf(
                        project.getTasks().named("jmhRunBytecodeGenerator").get());
                SourceSet jmhSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
                        .getSourceSets().getByName("jmh");
                exec.setClasspath(jmhSourceSet.getRuntimeClasspath()
                        .plus(project.files(generatedResourcesDir)));
                try {
                    suiteListFile.getParentFile().mkdirs();
                    exec.setStandardOutput(new FileOutputStream(suiteListFile));
                } catch (IOException failure) {
                    throw new UncheckedIOException(failure);
                }
            });
            task.doLast(t -> {
                List<String> classes = readLines(suiteListFile);
                List<String> distinct = BenchmarkSuiteCheck.distinctClasses(classes);
                List<String> problems = BenchmarkSuiteCheck.violations(distinct,
                        suite.getRoutineFamily().get(), suite.getDevtestFamily().get(),
                        suite.getFacilityProbes().get());
                if (!problems.isEmpty()) {
                    throw new GradleException(problems.get(0));
                }
                BenchmarkSuiteCheck.Summary summary = BenchmarkSuiteCheck.summaryOf(distinct,
                        suite.getRoutineFamily().get(), suite.getDevtestFamily().get(),
                        suite.getFacilityProbes().get());
                t.getLogger().lifecycle("jmhSuiteVerify: routine {} 类 / devtest {} 类 / 探针 {} 类，"
                                + "全量 {} 类皆已归族",
                        summary.routineCount(), summary.devtestCount(),
                        summary.probeHits().size(), distinct.size());
            });
        });

        // 常规族落盘（执行通道 push/夜间同款）：跑默认族清单后把 JSON 复制为
        // results/bench-<yyyymmdd>-routine.json（日期执行期生成）。
        project.getTasks().register("benchRoutineExport", task -> {
            task.setGroup("benchmark");
            task.setDescription("Run routine-suite benchmarks (incl. facility probe) and archive JSON "
                    + "result into results/ with a date fingerprint.");
            task.dependsOn("jmh");
            task.doLast(t -> {
                JmhParameters parameters = project.getExtensions().getByType(JmhParameters.class);
                File src = parameters.getResultsFile().get().getAsFile();
                if (!src.exists()) {
                    throw new GradleException("常规族运行声明完成但结果文件缺失：" + src
                            + "（spec：产物缺失可发现）");
                }
                // 日期统一钉 UTC（实况教训：runner 时区为 UTC，本地 Asia/Shanghai 差一日，
                // 速览首跑产物落成 bench-20261001-quick.json 与本地日期口径错位——与回写提交
                // 时间戳同基准）。原 SimpleDateFormat("yyyyMMdd") 加 UTC 时区改 java.time 同形。
                String dateStr = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE);
                // 产物规模标识（refine-bench-routine-triggering D2 与差量产物条款）：
                // 速览与完整各自成文件，同日期覆盖限定同规模，逐版对比要求同规模成立。
                String scopeSuffix = project.hasProperty("benchScope")
                        && "quick".equals(project.property("benchScope").toString()) ? "quick" : "routine";
                File dest = new File(project.getProjectDir(),
                        "results/bench-" + dateStr + "-" + scopeSuffix + ".json");
                dest.getParentFile().mkdirs();
                try {
                    // 原 ant.copy overwrite:true 的等价替代（执行期文件复制，不保留源时间戳语义一致）
                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException failure) {
                    throw new UncheckedIOException(failure);
                }
                t.getLogger().lifecycle("benchRoutineExport: {}", dest);
            });
        });

        // 执行面规模选择（move-bench-suite-selection-into-plugin：gradle-build-style 需求一——
        // 分支与中间变量累加属程序性，收编本插件；模块 jmh 块只留声明式静态基线参数）。
        // D1：本段注册在 benchParams 段之前——同工程 afterEvaluate 回调按注册顺序执行，
        // 显式 -PbenchParams 覆写参数域的优先级承前最后。
        project.afterEvaluate(checked -> applySelection(checked, suite));
        // 参数过滤经 JMH -p（include 正则不匹配参数串，实测证实）；分号分隔多字段。
        // 解析循环自模块 jmh{} 迁入本插件（gradle-build-style 需求一：程序性中间变量累加不落模块文件，
        // declarative-it-demo-isolation design D5 行数守恒项），语义保持"显式 benchParams 覆写参数域"。
        project.afterEvaluate(checked -> applyBenchParamsOverride(checked));
    }

    /** 选择段：缺省执行＝常规族（含设施探针）＋六臂参数域，可复现；全量矩阵以 -PbenchAll 显式打开。 */
    private void applySelection(Project project, BenchmarkSuite suite) {
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

    /** benchParams 覆写段：显式传入时整图替换参数域（Groovy 形态 map 赋值先 clear 再逐项 put 全等）。 */
    private void applyBenchParamsOverride(Project project) {
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
        // MapProperty.set(Map) 即 Groovy 形态整图赋值的等价（显式 benchParams 覆写参数域，承前居末）
        jmh.getBenchmarkParameters().set(replacement);
    }

    /**
     * 取 jmhRunBytecodeGenerator 任务的生成资源目录。原 Groovy 形态即运行时动态属性访问
     * （该任务类型由 jmh 插件内部注册、非本 buildSrc 编译期承诺的公开类型），
     * 反射保留同一"运行时求值"语义且失败即报红指名。
     */
    private static Object generatedResourcesDirOf(Task task) {
        try {
            return task.getClass().getMethod("getGeneratedResourcesDir").invoke(task);
        } catch (ReflectiveOperationException failure) {
            throw new GradleException("jmh 枚举清单装配失败：任务 '" + task.getPath()
                    + "' 无 getGeneratedResourcesDir() 访问器（jmh 插件升级断言，需随版本适配）", failure);
        }
    }

    private static long countCompanion(File file) {
        return readLines(file).stream().filter(line -> line.startsWith("com.")).count();
    }

    private static List<String> readLines(File file) {
        try {
            return file.exists() ? Files.readAllLines(file.toPath(), StandardCharsets.UTF_8) : List.of();
        } catch (IOException failure) {
            throw new UncheckedIOException(failure);
        }
    }
}
