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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import tny.convention.GitCli;
import tny.convention.GitFlow;

/**
 * 编排任务接线的共享簿记小段（convert-orchestration-to-java 任务 6.1）：命令输出取文本、
 * 读行、脏树检查两分支、历史占号文件读取——全部原样承自 tny.release/tny.integrate 脚本
 * 顶部闭包（通道与语义分居纪律不变：解析在 GitFacts，门禁判定在 Release/IntegrationGateCheck，
 * 本类只做接线簿记）。
 */
final class ChannelSupport {

    private ChannelSupport() {
    }

    static String out(Map<String, Object> run) {
        return ((String) run.get("out")).trim();
    }

    /** 等价 Groovy String.readLines()：按行拆分并去尾部换行产生的空尾元素。 */
    static List<String> lines(String text) {
        if (text.isEmpty()) {
            return List.of();
        }
        String[] parts = text.split("\n", -1);
        int end = parts.length;
        if (parts[parts.length - 1].isEmpty()) {
            end--;
        }
        return Arrays.asList(Arrays.copyOfRange(parts, 0, end));
    }

    static List<String> runOutLines(GitCli git, List<String> args) {
        return lines((String) git.run(args).get("out"));
    }

    /** 历史占号黑名单（gradle/released-legacy.txt；注释行与空行丢弃，逐字承脚本）。 */
    static List<String> legacyReleasedLines(Project project) {
        File legacyFile = project.getRootProject().file("gradle/released-legacy.txt");
        if (!legacyFile.exists()) {
            return List.of();
        }
        try {
            return Files.readAllLines(legacyFile.toPath(), StandardCharsets.UTF_8).stream()
                    .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                    .toList();
        } catch (IOException failure) {
            throw new UncheckedIOException(failure);
        }
    }

    /** 脏树检查两分支：预览仅告警、真实执行阻断（porcelain 口径由 GitFlow 保持）。 */
    static void requireCleanTree(GitFlow gitFlow, String action, boolean preview, Logger logger) {
        List<String> dirty = gitFlow.trackedDirtyPaths();
        if (dirty.isEmpty()) {
            return;
        }
        if (preview) {
            logger.lifecycle("[dryRun][warn] 跟踪文件有未提交变更，真实执行将被阻断：\n" + String.join("\n", dirty));
        } else {
            throw new GradleException(action + " 要求跟踪文件无未提交变更，当前脏项：\n" + String.join("\n", dirty));
        }
    }

    static List<String> branchLines(GitFlow gitFlow, String remoteName) {
        return gitFlow.branchNames();
    }
}
