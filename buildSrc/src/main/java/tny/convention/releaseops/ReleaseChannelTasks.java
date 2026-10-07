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

import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.Task;
import tny.convention.GitCli;
import tny.convention.GitFlow;

/**
 * 发布快速通道任务接线（convert-orchestration-to-java 任务 6.1，自 tny.release.gradle 逐段迁入；
 * 语义契约 capability release-versioning 与 docs/release-process.md）。新形态：main 上 releaseCut
 * 切系列唯一常驻 release 维护分支（不建标签），release/N.M.x 上 releaseTag 过下一补丁号校验与
 * 远端头对齐后建附注标签并推送；祖父轨保留旧一次性发布分支流程（随 5.7 系列退役整体删除）；
 * 任务 releaseMergeBack 已由 redesign-devline-integration-model 删除、语义由 mergeUpward 取代——
 * 以上任务形态与全部话术逐字保持，判定在 ReleaseGateCheck、簿记在 ChannelSupport。
 * 通道分界（D6）不变：写与执行期引用的查询走 GitCli 门面（-PgitExe 覆盖）；-PdryRun 只读派生值
 * 与登记表、不派生子进程。GitHub Packages 镜像两来源话术随脚本尾注留档于 releaseTag 完成语。
 */
public final class ReleaseChannelTasks {

    private ReleaseChannelTasks() {
    }

    public static void register(Project project, GitFlow gitFlow, GitCli git) {
        String current = gitFlow.getBranchName();
        String currentRemote = gitFlow.resolveRemoteName(current);

        project.getTasks().register("releaseCut", task -> {
            task.setGroup("release");
            task.setDescription("New model: on main, cut the series-unique release maintenance branch "
                    + "release/<N.M>.x for first release <N.M>.0 (no tag yet). Grandfather track: on a "
                    + "registered legacy maintenance line, cut a one-off <V>.release branch as before. "
                    + "Needs -PreleaseVersion=N.M.K, optional -PreleaseFrom (grandfather only), -PdryRun for preview.");
            task.doLast(t -> cut(project, gitFlow, git, task, current, currentRemote));
        });

        project.getTasks().register("releaseTag", task -> {
            task.setGroup("release");
            task.setDescription("Create and push the annotated release tag at the release maintenance branch "
                    + "HEAD (new model, runs on release/<N.M>.x with next-patch-number check and remote-head "
                    + "alignment) or at a legacy one-off branch head (grandfather track). Needs "
                    + "-PreleaseVersion=N.M.K, -PdryRun for preview.");
            task.doLast(t -> tag(project, gitFlow, git, task, current, currentRemote));
        });
    }

    private static void cut(Project project, GitFlow gitFlow, GitCli git, Task task,
                            String current, String currentRemote) {
        String v = ReleaseGateCheck.requireReleaseVersion("releaseCut", stringProperty(project, "releaseVersion"));
        ReleaseGateCheck.assertNotLegacyOccupied(v, ChannelSupport.legacyReleasedLines(project));
        String base = v.replaceAll("\\.\\d+$", "");
        String tagName = "v" + v;
        boolean preview = project.hasProperty("dryRun");
        ChannelSupport.requireCleanTree(gitFlow, "releaseCut", preview, task.getLogger());
        boolean onMain = current.equals("main");
        boolean onLegacy = "maintenance".equals(gitFlow.legacyIdentity(current));
        ReleaseGateCheck.requireCutBranch(onMain, onLegacy, current);
        if (onMain) {
            ReleaseGateCheck.requireSeriesStart(v, base);
            String target = "release/" + base + ".x";
            ReleaseGateCheck.assertLocalBranchFree(target, gitFlow.branchNames().contains(target));
            String remoteName = gitFlow.resolveRemoteName("main");
            if (remoteName == null) {
                throw new org.gradle.api.GradleException(
                        "无法确定 main 的远端：main 没有上游且仓库远端数量不是 1");
            }
            ReleaseGateCheck.assertRemoteMaintenanceFree(target,
                    gitFlow.remoteRefNames(remoteName, true, false).contains("refs/heads/" + target));
            String plan = "releaseCut 计划（新形态切常驻维护分支）：\n"
                    + "  维护分支    " + target + "（切自 " + remoteName + "/main 当前头；"
                    + "前提：dev/" + base + ".x 已完成集成到主干）\n"
                    + "  标签        不创建——由 releaseTag 在该维护分支头创建（标签先于制品）\n"
                    + "后续步骤：  git switch " + target + "\n"
                    + "            然后  ./gradlew releaseTag -PreleaseVersion=" + v + "   然后  ./gradlew publish";
            if (preview) {
                task.getLogger().lifecycle("[dryRun] 未做任何变更\n" + plan);
                return;
            }
            git.require(List.of("fetch", remoteName, "refs/heads/main:refs/remotes/" + remoteName + "/main"),
                    "刷新 main 远端引用失败");
            git.require(List.of("branch", target, remoteName + "/main"), "切出维护分支失败");
            git.require(List.of("push", remoteName,
                    "refs/heads/" + target + ":refs/heads/" + target), "推送维护分支失败");
            task.getLogger().lifecycle("releaseCut 完成：" + target + " 已切自 " + remoteName
                    + "/main 并推送。git switch " + target + " 后执行 ./gradlew releaseTag -PreleaseVersion="
                    + v + "，再 ./gradlew publish。");
            return;
        }
        // 祖父轨：旧一次性发布分支流程原样保留，随 5.7 系列退役整体删除
        String targetBranch = v + ".release";
        String from = stringProperty(project, "releaseFrom") != null
                ? stringProperty(project, "releaseFrom") : current;
        if (gitFlow.branchNames().contains(targetBranch)) {
            throw new org.gradle.api.GradleException(
                    "本地已存在分支 '" + targetBranch + "'，同号发布禁止重复切支");
        }
        if (gitFlow.tagNames().contains(tagName)) {
            throw new org.gradle.api.GradleException(
                    "本地已存在标签 '" + tagName + "'，同号发布禁止重复建标");
        }
        String remoteName = currentRemote;
        ReleaseGateCheck.requireRemote(remoteName, current);
        List<String> remoteRefs = gitFlow.remoteRefNames(remoteName, true, true);
        if (remoteRefs.stream().anyMatch(ref -> ref.startsWith("refs/heads/" + targetBranch))) {
            throw new org.gradle.api.GradleException(
                    "远端 '" + remoteName + "' 已存在分支 '" + targetBranch + "'");
        }
        if (remoteRefs.stream().anyMatch(ref -> ref.startsWith("refs/tags/" + tagName))) {
            throw new org.gradle.api.GradleException("远端 '" + remoteName + "' 已存在标签 '" + tagName
                    + "'（或存在前缀撞名，请核对后处理）");
        }
        String plan = "releaseCut 计划（祖父轨旧流程，当前分支 '" + current + "' 为登记维护线）：\n"
                + "  来源        " + from + "\n"
                + "  一次性发布分支  " + targetBranch + "（切自来源并推送；标签创建前只接受快进式追加修复）\n"
                + "  标签        不创建——由 releaseTag 在制品定型后创建\n"
                + "后续步骤：（如有修复先完成并推送）  ./gradlew releaseTag   然后  ./gradlew publish";
        if (preview) {
            task.getLogger().lifecycle("[dryRun] 未做任何变更\n" + plan);
            return;
        }
        git.require(List.of("switch", "-c", targetBranch, from), "切一次性发布分支失败");
        git.require(List.of("push", remoteName,
                "refs/heads/" + targetBranch + ":refs/heads/" + targetBranch), "推送一次性发布分支失败");
        task.getLogger().lifecycle("releaseCut 完成：" + targetBranch + " 已从 " + from
                + " 切出并推送。完成修复（如有）后执行 ./gradlew releaseTag -PreleaseVersion=" + v
                + "，再 ./gradlew publish。");
    }

    private static void tag(Project project, GitFlow gitFlow, GitCli git, Task task,
                            String current, String currentRemote) {
        String v = ReleaseGateCheck.requireReleaseVersion("releaseTag", stringProperty(project, "releaseVersion"));
        String base = v.replaceAll("\\.\\d+$", "");
        String tagName = "v" + v;
        boolean onMaintenance = current.equals("release/" + base + ".x");
        ReleaseGateCheck.requireTagBranch(v, base, current);
        ReleaseGateCheck.assertNotLegacyOccupied(v, ChannelSupport.legacyReleasedLines(project));
        java.util.Map<String, Object> existing = gitFlow.tagInfo(tagName);
        ReleaseGateCheck.assertTagIdempotent(tagName, existing, gitFlow.getCommitId());
        String remoteName = currentRemote;
        ReleaseGateCheck.requireRemote(remoteName, current);
        boolean preview = project.hasProperty("dryRun");
        if (onMaintenance) {
            if (!preview) {
                git.require(List.of("fetch", remoteName, "refs/heads/" + current
                        + ":refs/remotes/" + remoteName + "/" + current), "刷新维护分支远端引用失败");
                String remoteHead = gitFlow.remoteRefs(remoteName,
                        List.of("--heads", "refs/heads/" + current)).get("refs/heads/" + current);
                if (remoteHead != null && !remoteHead.equals(gitFlow.getCommitId())) {
                    throw new org.gradle.api.GradleException("本地 HEAD 与维护分支远端头不一致（远端 "
                            + remoteHead.substring(0, Math.min(10, remoteHead.length()))
                            + " / 本地 " + gitFlow.getCommitId().substring(0, 10)
                            + "）——先把分支头推送对齐再重跑");
                }
                List<Integer> patches = gitFlow.remoteReleasedPatches(remoteName, base);
                ReleaseGateCheck.assertNextPatchNumber(base, v, patches);
                // 例外入账可见化（capability branch-integration-gates 例外通道）：本次发版
                // 携带的 propagated-as 登记提交逐条打印，使"例外随版发出"对发布者与审计可见。
                if (!patches.isEmpty()) {
                    List<String> carried = ChannelSupport.runOutLines(git, List.of("log", "--oneline",
                                    "--grep=propagated-as", "v" + base + "." + patches.stream()
                                            .mapToInt(Integer::intValue).max().getAsInt() + "..HEAD"))
                            .stream().map(String::trim).filter(line -> !line.isEmpty()).toList();
                    if (!carried.isEmpty()) {
                        task.getLogger().lifecycle("releaseTag：本次 " + v + " 将随版发出 " + carried.size()
                                + " 笔例外登记提交：\n" + String.join("\n", carried));
                    }
                }
            } else {
                task.getLogger().lifecycle("[dryRun] 提示：远端头对齐与下一补丁号校验在真实执行时进行");
            }
        }
        boolean remoteTagExists = gitFlow.remoteRefNames(remoteName, false, true).stream()
                .anyMatch(ref -> ref.startsWith("refs/tags/" + tagName));
        if (remoteTagExists) {
            throw new org.gradle.api.GradleException("远端 '" + remoteName + "' 已存在标签 '" + tagName
                    + "'（或存在前缀撞名，请核对后处理）");
        }
        ChannelSupport.requireCleanTree(gitFlow, "releaseTag", preview, task.getLogger());
        String plan = "releaseTag 计划：\n"
                + "  分支        " + current + "（HEAD 即制品构建提交）\n"
                + "  附注标签    " + tagName + "（单独推送——标签先于制品，落定即封存点"
                + (onMaintenance ? "；已含下一补丁号校验与远端头对齐" : "（祖父轨一次性发布分支语义）") + "）"
                + (existing != null ? "\n  注            本地附注标签已指向当前构建提交，将跳过创建仅补推送" : "") + "\n"
                + "后续步骤：  ./gradlew publish"
                + (onMaintenance ? "   发布后按补丁影响清单执行  ./gradlew mergeUpward" : "");
        if (preview) {
            task.getLogger().lifecycle("[dryRun] 未做任何变更\n" + plan);
            return;
        }
        task.getLogger().lifecycle(plan);
        if (existing == null) {
            git.require(List.of("tag", "-a", tagName, "-m", "release " + v + " from " + current),
                    "创建附注标签失败");
        }
        git.require(List.of("push", remoteName,
                "refs/tags/" + tagName + ":refs/tags/" + tagName), "推送标签失败");
        // 话术指路的 ./gradlew publish 覆盖内网 Nexus（及本机凭据在位时的 Central 快照通道）；
        // GitHub Packages 镜像有持续集成与本机两个来源（publish-mirror-via-credential-pair 拍板）：
        // 持续集成链统一在 .github/workflows/publish.yml 单文件内（consolidate-publish-workflows
        // 合并）：正式版走 publish-release 作业、快照走 snapshot-mirror 作业定时；发布者本机成对
        // 配置 githubPackagesUsername/githubPackagesToken 两属性时，上面的 ./gradlew publish 亦扇出
        // 镜像；未成对配置的机器不含镜像属凭据守卫设计。
        task.getLogger().lifecycle("releaseTag 完成：" + tagName + " 指向当前构建提交并已推送至 "
                + remoteName + "。现在执行 ./gradlew publish（门禁核对 release 标签存证）。");
    }

    private static String stringProperty(Project project, String name) {
        Object value = project.findProperty(name);
        return value == null ? null : value.toString();
    }
}
