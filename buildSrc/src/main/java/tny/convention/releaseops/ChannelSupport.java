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

    /** 集成面远端推定报错文案（与发布面措辞不同，各自逐字承原脚本；任务 7.3 界线拆入本类）。 */
    static void requireIntegrationRemote(String remoteName, String current) {
        if (remoteName == null) {
            throw new GradleException("无法确定远端：分支 '" + current + "' 没有上游且仓库远端数量不是 1");
        }
    }

    /** 本地分支名到 HEAD 短号（计划文案 "本地头 xxxxxxxxxx" 口径，逐字承脚本 head10 闭包）。 */
    static String head10(String commitId) {
        return commitId.substring(0, Math.min(10, commitId.length()));
    }

    /** 逗号分隔项目属性到清单（未给出返回 null 表示"缺省目标"由调用方分支；逐字承脚本 listProperty）。 */
    static List<String> listProperty(Project project, String name) {
        Object value = project.findProperty(name);
        if (value == null) {
            return null;
        }
        List<String> items = new java.util.ArrayList<>();
        for (String part : value.toString().split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }
        return items.isEmpty() ? null : items;
    }

    /** 远端分支名按正则过滤并剥 refs/heads/ 前缀（逐字承脚本 remoteBranchesMatching）。 */
    static List<String> remoteBranchesMatching(GitFlow gitFlow, String remoteName, String regex) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        return gitFlow.remoteRefNames(remoteName, true, false).stream()
                .filter(ref -> pattern.matcher(ref).find())
                .map(ref -> ref.replace("refs/heads/", ""))
                .toList();
    }
}
