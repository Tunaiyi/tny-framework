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
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import tny.convention.GitCli;
import tny.convention.GitFacts;
import tny.convention.GitFlow;

/**
 * 分支流转门禁任务接线（convert-orchestration-to-java 任务 6.1，自 tny.integrate.gradle 逐段迁入；
 * capability branch-integration-gates，redesign-devline-integration-model 设计 D3/D4/D5）。
 * integrateMain 固化"先同步、后整体合并、再下线"顺序并执行集成顺序检查；mergeUpward 以整条分支
 * 合并方式把补丁向上合并进更更新的维护分支与 main、提交前执行合并结果完整性检查；retireGuard 为
 * 删除分支前的核对清单（未集成提交枚举与线谱系登记行）。合并冲突一律 abort 加 reset 回原头交人工。
 * 通道边界：写动作经 GitCli，远端读取经 GitFlow 查询方法面；与动作强耦合的状态查询
 * （rev-parse 记原头、log 提标记）属动作簿记留在本类（consolidate-git-queries-into-gitflow D4）。
 * 话术与判定文案逐字承脚本；判定语义在 IntegrationGateCheck。
 */
public final class IntegrationChannelTasks {

    private IntegrationChannelTasks() {
    }

    public static void register(Project project, GitFlow gitFlow, GitCli git) {
        String current = gitFlow.getBranchName();
        String remoteName = gitFlow.resolveRemoteName(current);

        project.getTasks().register("integrateMain", task -> {
            task.setGroup("release");
            task.setDescription("Integrate the current dev/<N.M>.x branch into main with the fixed order "
                    + "(merge main in first, then merge the branch into main with a merge commit, then retire "
                    + "the branch). Enforces the integration order check. Optional -PreleaseVersion=<N.M>.0 "
                    + "for the ledger message, -PdryRun for preview.");
            task.doLast(t -> integrateMain(project, gitFlow, git, task, current, remoteName));
        });

        project.getTasks().register("mergeUpward", task -> {
            task.setGroup("release");
            task.setDescription("Merge the current release maintenance branch upward into the newer release "
                    + "maintenance branches and main (merge-forward), with the merge-result completeness "
                    + "check before each commit. Targets default to all higher-numbered release/N.M.x "
                    + "branches plus main; override with -Pinto=release/5.9.x,main. -Pmarkers=BUG-101,CVE-2 "
                    + "lists the defect markers that must survive each merge; default extracts markers from "
                    + "merged commit messages. Needs -PdryRun for preview.");
            task.doLast(t -> mergeUpward(project, gitFlow, git, task, current, remoteName));
        });

        project.getTasks().register("retireGuard", task -> {
            task.setGroup("release");
            task.setDescription("Check uncollected commits on the current release maintenance branch before "
                    + "retiring (deleting) it: lists commits not yet contained in main and refuses unless "
                    + "the lineage registry disposition column records their keep-or-discard decision.");
            task.doLast(t -> retireGuard(project, gitFlow, git, task, current, remoteName));
        });
    }

    private static void integrateMain(Project project, GitFlow gitFlow, GitCli git, Task task,
                                      String current, String remoteName) {
        IntegrationGateCheck.requireDevBranch(current);
        requireRemote(remoteName, current);
        boolean preview = project.hasProperty("dryRun");
        ChannelSupport.requireCleanTree(gitFlow, "integrateMain", preview, task.getLogger());
        int myKey = GitFacts.seriesKey(current);
        Object releaseVersion = project.findProperty("releaseVersion");
        String rv = releaseVersion == null ? null : releaseVersion.toString();
        // 集成顺序检查（规格：发布顺序等于版本顺序）：任一低编号开发版本分支仍存在
        // （本模型梯毕即删线，存在即未集成未下线），即拒绝；确需放弃的低编号线先按
        // 改号跳号程序注销（删除分支并在提交说明登记）。本地与远端并集枚举。
        List<String> allDevLines = new ArrayList<>(gitFlow.branchNames());
        allDevLines.addAll(remoteBranchesMatching(gitFlow, remoteName, "^refs/heads/dev/\\d+\\.\\d+\\.x$"));
        IntegrationGateCheck.assertIntegrationOrder(allDevLines, current, myKey);
        String plan = "integrateMain 计划：\n"
                + "  同步        先把 " + remoteName + "/main 合并进 " + current + "（冲突在本分支解决）\n"
                + "  集成        切 main，--no-ff 合入 " + current + "，提交说明 integrate " + current
                + " to main" + (rv != null ? "：" + rv : "") + "\n"
                + "  下线        删除 " + current + "（本地与远端）——该系列后续补丁只走 release 维护分支\n"
                + "  前置        集成顺序检查已通过（低编号在途开发分支为零）";
        if (preview) {
            task.getLogger().lifecycle("[dryRun] 未做任何变更\n" + plan);
            return;
        }
        task.getLogger().lifecycle(plan);
        git.require(List.of("fetch", remoteName, "refs/heads/main:refs/remotes/" + remoteName + "/main"),
                "刷新 main 远端引用失败");
        if ((Integer) git.run(List.of("merge", "--no-edit", remoteName + "/main")).get("exit") != 0) {
            git.run(List.of("merge", "--abort"));
            throw new GradleException("主干同步合并发生冲突（在 " + current + " 上）：已 abort 保持原状。"
                    + "请人工 git merge " + remoteName + "/main 解决后重跑本任务");
        }
        git.require(List.of("push", remoteName, "refs/heads/" + current), "推送同步结果失败");
        git.require(List.of("switch", "main"), "切换 main 失败");
        git.require(List.of("merge", "--ff-only", remoteName + "/main"),
                "main 本地头与远端不一致且无法快进对齐");
        String msg = "integrate " + current + " to main" + (rv != null ? ": " + rv : "");
        git.require(List.of("merge", "--no-ff", "--no-edit", "-m", msg, current), "整体合并进 main 失败");
        var push = git.run(List.of("push", remoteName, "refs/heads/main"));
        if ((Integer) push.get("exit") != 0) {
            throw new GradleException("推送 main 失败：" + ((String) push.get("err")).trim()
                    + "\n本地合并已完成——远端 main 被他人推进时先 git pull --ff-only "
                    + remoteName + " main 再重跑推送");
        }
        git.require(List.of("branch", "-d", current), "删除已集成分支 " + current + " 失败");
        if (gitFlow.remoteBranchExists(remoteName, current)) {
            git.run(List.of("push", remoteName, "--delete", current));
        }
        task.getLogger().lifecycle("integrateMain 完成：" + current + " 已集成进 main 并下线。下一步 "
                + "./gradlew releaseCut -PreleaseVersion=" + (rv != null ? rv : "<N.M>.0")
                + "（在 main 上）切该系列的 release 维护分支。");
    }

    private static void mergeUpward(Project project, GitFlow gitFlow, GitCli git, Task task,
                                    String current, String remoteName) {
        IntegrationGateCheck.requireMaintenanceSource(current);
        requireRemote(remoteName, current);
        String source = current;
        int myKey = GitFacts.seriesKey(source);
        List<String> targets = listProperty(project, "into");
        if (targets == null) {
            targets = new ArrayList<>(remoteBranchesMatching(gitFlow, remoteName,
                    "^refs/heads/release/\\d+\\.\\d+\\.x$")).stream()
                    .filter(line -> GitFacts.seriesKey(line) > myKey).collect(java.util.stream.Collectors.toList());
            targets = new ArrayList<>(targets);
            targets.add("main");
        }
        List<String> explicitMarkers = listProperty(project, "markers");
        String plan = "mergeUpward 计划（向上合并，机制为整条分支合并）：\n"
                + "  源分支      " + source + "（本地头 " + head10(gitFlow.getCommitId()) + "）\n"
                + "  目标序列    " + String.join("、", targets) + "\n"
                + "  每目标动作  merge 源分支（冲突即 abort 回原头）→ 合并结果完整性检查（提交前）→ 普通推送\n"
                + "  检查标记    " + (explicitMarkers != null && !explicitMarkers.isEmpty()
                ? "显式清单 " + String.join("、", explicitMarkers) : "自动从被搬运提交说明提取缺陷编号模式");
        if (project.hasProperty("dryRun")) {
            task.getLogger().lifecycle("[dryRun] 未做任何变更\n" + plan);
            return;
        }
        task.getLogger().lifecycle(plan);
        for (String target : targets) {
            String targetHead = ChannelSupport.out(git.run(List.of("rev-parse", "--verify", "-q", target)));
            if (targetHead.isEmpty()) {
                git.require(List.of("switch", "-c", target, remoteName + "/" + target),
                        "检出目标 '" + target + "' 失败（本地与远端均不存在？）");
                targetHead = ChannelSupport.out(git.run(List.of("rev-parse", target)));
            } else {
                git.require(List.of("switch", target), "切换目标 '" + target + "' 失败");
            }
            git.require(List.of("fetch", remoteName, "refs/heads/" + target + ":refs/remotes/"
                    + remoteName + "/" + target), "刷新目标远端引用失败");
            var merged = git.run(List.of("merge", "--no-edit", "-m",
                    "merge-upward: " + source + " -> " + target, source));
            if ((Integer) merged.get("exit") != 0) {
                git.run(List.of("merge", "--abort"));
                git.require(List.of("reset", "--hard", targetHead), "回退目标 '" + target + "' 到原头失败");
                git.require(List.of("switch", source), "切回源分支 '" + source + "' 失败");
                throw new GradleException("向上合并 " + source + " -> " + target + " 发生冲突："
                        + "已 abort 并使目标与源保持原状。人工处置：git switch " + target + " && git merge "
                        + source + " 解决后自行执行合并结果完整性检查（见规格场景），完成后推送并重跑本任务跳过已入账部分");
            }
            List<String> markers = explicitMarkers != null && !explicitMarkers.isEmpty()
                    ? explicitMarkers
                    : GitFacts.extractMarkers(ChannelSupport.runOutLines(git, List.of("log", "--no-merges",
                    "--format=%s", targetHead + ".." + source)));
            // 检索排除叙述性路径（docs、drill、openspec 的正文天然引用历史缺陷号；
            // e2e 第十一轮实锤：演练脚本自含标记令检索恒绿——完整性检查若不排除
            // 叙述文件会自我蒙蔽）。判定对象是"修复的承载内容在不在"，不是编号被提及过。
            List<List<String>> hitsPerMarker = new ArrayList<>();
            for (String marker : markers) {
                hitsPerMarker.add(ChannelSupport.runOutLines(git, List.of("grep", "-c", marker, "HEAD",
                        "--", ":(exclude)docs", ":(exclude)drill", ":(exclude)openspec")));
            }
            List<String> missing = IntegrationGateCheck.missingMarkers(markers, hitsPerMarker);
            if (!missing.isEmpty()) {
                git.require(List.of("reset", "--hard", targetHead), "回退目标 '" + target + "' 到原头失败");
                git.require(List.of("switch", source), "切回源分支 '" + source + "' 失败");
                IntegrationGateCheck.assertNoMissingMarkers(source, target, missing);
            }
            var push = git.run(List.of("push", remoteName, "refs/heads/" + target));
            if ((Integer) push.get("exit") != 0) {
                throw new GradleException("推送目标 '" + target + "' 失败：" + ((String) push.get("err")).trim()
                        + "\n本地合并与检查已完成——目标被他人推进时先在该目标 git pull --no-edit "
                        + remoteName + " " + target + " 并入后重跑本任务");
            }
            task.getLogger().lifecycle("mergeUpward：" + source + " -> " + target + " 完成（标记核对 "
                    + markers.size() + " 项），已推送。");
        }
        git.require(List.of("switch", source), "切回源分支 '" + source + "' 失败");
        task.getLogger().lifecycle("mergeUpward 全部完成：" + source + " 的补丁已逐级到达 "
                + String.join("、", targets) + "；源分支与既有标签未移动。");
    }

    private static void retireGuard(Project project, GitFlow gitFlow, GitCli git, Task task,
                                    String current, String remoteName) {
        requireRemote(remoteName, current);
        IntegrationGateCheck.requireRetireCandidateBranch(current);
        git.require(List.of("fetch", remoteName, "refs/heads/" + current + ":refs/remotes/"
                + remoteName + "/" + current), "刷新维护分支远端引用失败");
        git.run(List.of("fetch", remoteName, "refs/heads/main:refs/remotes/" + remoteName + "/main"));
        List<String> uncollected = ChannelSupport.runOutLines(git, List.of("log", "--oneline",
                        remoteName + "/main.." + remoteName + "/" + current)).stream()
                .map(String::trim).filter(line -> !line.isEmpty()).toList();
        IntegrationGateCheck.assertNoUncollected(current, uncollected);
        File lineage = project.getRootProject().file("docs/release-process.md");
        List<String> lineageLines = List.of();
        if (lineage.exists()) {
            try {
                lineageLines = Files.readAllLines(lineage.toPath(), StandardCharsets.UTF_8);
            } catch (IOException failure) {
                throw new UncheckedIOException(failure);
            }
        }
        IntegrationGateCheck.assertLineageDispositioned(current, lineageLines);
        task.getLogger().lifecycle("retireGuard 通过：" + current + " 无未集成提交且已在登记表记状态；"
                + "可由仓库管理员删除远端分支（本地 git push " + remoteName + " --delete " + current + "）。");
    }

    /** 集成面远端推定报错文案（与发布面措辞不同，各自逐字承原脚本）。 */
    private static void requireRemote(String remoteName, String current) {
        if (remoteName == null) {
            throw new GradleException("无法确定远端：分支 '" + current + "' 没有上游且仓库远端数量不是 1");
        }
    }

    private static List<String> remoteBranchesMatching(GitFlow gitFlow, String remoteName, String regex) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        return gitFlow.remoteRefNames(remoteName, true, false).stream()
                .filter(ref -> pattern.matcher(ref).find())
                .map(ref -> ref.replace("refs/heads/", ""))
                .toList();
    }

    private static List<String> listProperty(Project project, String name) {
        Object value = project.findProperty(name);
        if (value == null) {
            return null;
        }
        List<String> items = new ArrayList<>();
        for (String part : value.toString().split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }
        return items.isEmpty() ? null : items;
    }

    private static String head10(String commitId) {
        return commitId.substring(0, Math.min(10, commitId.length()));
    }
}
