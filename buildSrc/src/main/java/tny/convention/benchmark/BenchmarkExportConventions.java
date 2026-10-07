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
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 常规族落盘任务装配段（tny.benchmark-module 目录页第三段，迁移自 tny.benchmark-module.gradle
 * 原第 116 至 120 行注册与 benchRoutineExport 动作块；执行通道 push/夜间同款）。
 */
final class BenchmarkExportConventions {

    private BenchmarkExportConventions() {
    }

    static void apply(Project project) {
        // 跑默认族清单后把 JSON 复制为 results/bench-<yyyymmdd>-<规模>.json（日期执行期生成）。
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
    }
}
