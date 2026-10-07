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

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import tny.convention.BenchmarkSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
 * <p>目录页（gradle-build-style"扫读测试与长度界线"需求的跨类装配单元判据，每项行为指名所在类）：
 * <ol>
 * <li>本类枚举与对账段——jmhList（枚举清单；原 ext.listFile 动态属性以 final 局部变量承载等价
 *     替代，任务名与 finalizedBy 边不变）、jmhListVerify（空匹配显式判红）、jmhSuiteVerify
 *     （接线后判定在 {@link BenchmarkSuiteCheck}，红绿单测覆盖）；</li>
 * <li>{@link BenchmarkExportConventions}——benchRoutineExport 落盘与日期指纹；</li>
 * <li>{@link BenchmarkSelectionConventions}——两段 afterEvaluate 的选择面与 -PbenchParams 覆写
 *     （D1 注册次序即优先级契约，随拆类逐字保持）。</li>
 * </ol>
 *
 * <p>配置面：benchmarkSuite 五清单（{@link BenchmarkSuite}）；清单语义的双承载出处见
 * jmhSuiteVerify 段注释。边界：jmh 静态参数与依赖声明在模块构建文件，执行面规模选择在本插件
 * afterEvaluate（move-bench-suite-selection-into-plugin，gradle-build-style 需求一）；
 * 族目录与清单对账语义出处见 split-bench-suites 与 refine-bench-routine-triggering。
 *
 * <p>正则拼接来由（自模块 ext 迁入，机制侧知识）：插件会把 includes 列表逗号拼接成单个 JMH
 * 位置参数（实测），而 JMH 位置参数语义为"一个正则"——多正则必须自拼竖线；臂属筛选走 -p 而非
 * include 正则（实测：":prod_" 分支 0 选中；JMH 1.37 的 -p 语义是覆写参数域而非过滤，对未声明
 * 该字段的基准无排除效应）；速览档与完整档共用同一 include 面，差异只在 quickRunExcludes 显式
 * 清单（矩阵类负向前瞻正则的静默失效陷阱由此消灭，refine-bench-routine-triggering D2）。
 */
public class BenchmarkModulePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        BenchmarkSuite suite = project.getExtensions()
                .create("benchmarkSuite", BenchmarkSuite.class);

        // 列清单（锚集定义与 jmh 运行同源 benchmarkSuite 扩展）：CI PR 通道用它证明"清单可枚举"。
        // BenchmarkList 由插件 jmhRunBytecodeGenerator 产出于 generatedResourcesDir（非注解处理器阶段）。
        File listFile = new File(project.getLayout().getBuildDirectory()
                .dir("tmp/jmhList").get().getAsFile(), "jmhList.txt");
        project.getTasks().register("jmhList", JavaExec.class, task -> {
            task.setGroup("benchmark");
            task.setDescription("List JMH benchmarks honoring routine-suite filters. 全量清单：-PbenchAll");
            task.dependsOn("jmhCompileGeneratedClasses");
            task.getMainClass().set("org.openjdk.jmh.Main");
            task.setArgs(new ArrayList<>(List.of("-l")));
            task.doFirst(running -> {
                JavaExec exec = (JavaExec) running;
                exec.setClasspath(jmhListClasspath(project));
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
                exec.setStandardOutput(openOutput(listFile));
            });
            task.finalizedBy("jmhListVerify");
        });

        // 空匹配不可静默绿（spec：名称过滤无匹配不误报绿——JMH -l 空列表退 0，此处显式判红）
        project.getTasks().register("jmhListVerify", task -> {
            task.setGroup("benchmark");
            task.doLast(t -> {
                long n = readLines(listFile).stream().filter(line -> line.startsWith("com.")).count();
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
                exec.setClasspath(jmhListClasspath(project));
                exec.setStandardOutput(openOutput(suiteListFile));
            });
            task.doLast(t -> {
                List<String> classes = BenchmarkSuiteCheck.distinctClasses(readLines(suiteListFile));
                List<String> problems = BenchmarkSuiteCheck.violations(classes,
                        suite.getRoutineFamily().get(), suite.getDevtestFamily().get(),
                        suite.getFacilityProbes().get());
                if (!problems.isEmpty()) {
                    throw new GradleException(problems.get(0));
                }
                BenchmarkSuiteCheck.Summary summary = BenchmarkSuiteCheck.summaryOf(classes,
                        suite.getRoutineFamily().get(), suite.getDevtestFamily().get(),
                        suite.getFacilityProbes().get());
                t.getLogger().lifecycle("jmhSuiteVerify: routine {} 类 / devtest {} 类 / 探针 {} 类，"
                                + "全量 {} 类皆已归族",
                        summary.routineCount(), summary.devtestCount(),
                        summary.probeHits().size(), classes.size());
            });
        });

        BenchmarkExportConventions.apply(project);
        BenchmarkSelectionConventions.apply(project, suite);
    }

    /** jmh 枚举两任务共用类路径：jmh 源集运行时加字节生成任务的生成资源目录（原脚本同构）。 */
    private static FileCollection jmhListClasspath(Project project) {
        Object generatedResourcesDir = generatedResourcesDirOf(
                project.getTasks().named("jmhRunBytecodeGenerator").get());
        SourceSet jmhSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
                .getSourceSets().getByName("jmh");
        return jmhSourceSet.getRuntimeClasspath().plus(project.files(generatedResourcesDir));
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

    private static FileOutputStream openOutput(File file) {
        try {
            file.getParentFile().mkdirs();
            return new FileOutputStream(file);
        } catch (IOException failure) {
            throw new UncheckedIOException(failure);
        }
    }

    private static List<String> readLines(File file) {
        try {
            return file.exists() ? Files.readAllLines(file.toPath(), StandardCharsets.UTF_8) : List.of();
        } catch (IOException failure) {
            throw new UncheckedIOException(failure);
        }
    }
}
