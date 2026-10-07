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

import org.gradle.api.GradleException;
import tny.convention.GitFacts;

import java.util.List;
import java.util.Map;

/**
 * 发布快速通道门禁判定（convert-orchestration-to-java 任务 4.2，自 tny.release.gradle 的
 * 校验闭包拆出；capability release-versioning 与 docs/release-process.md 语义契约不变）。
 * 报错文案逐字承脚本原句；红绿用例见 ReleaseGateCheckTest。
 */
public final class ReleaseGateCheck {

    private ReleaseGateCheck() {
    }

    /** 版本参数校验：缺位与形态两向（requireReleaseVersion 闭包等价）。 */
    public static String requireReleaseVersion(String action, String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new GradleException(action + " 需要 -PreleaseVersion=<主>.<次>.<补丁>，例如 -PreleaseVersion=5.7.8");
        }
        if (!GitFacts.isBareTripleVersion(raw)) {
            throw new GradleException("版本号 '" + raw + "' 不符合裸三段号形态 <主>.<次>.<补丁>");
        }
        return raw;
    }

    /** 历史占号黑名单（gradle/released-legacy.txt 行清单为输入）。 */
    public static void assertNotLegacyOccupied(String version, List<String> legacyReleasedLines) {
        if (legacyReleasedLines.contains(version)) {
            throw new GradleException("版本 '" + version + "' 已以 -RELEASE 后缀形态发布"
                    + "（见 gradle/released-legacy.txt），禁止裸号复用同号，请提高补丁号");
        }
    }

    /** releaseCut 分支形态守卫（main 或祖父登记维护线二者其一）。 */
    public static void requireCutBranch(boolean onMain, boolean onLegacy, String current) {
        if (!onMain && !onLegacy) {
            throw new GradleException("releaseCut 只允许在 main（新形态切常驻维护分支，版本须为 <N.M>.0）"
                    + "或祖父登记维护线（旧一次性发布分支流程）上执行，当前分支 '" + current + "'");
        }
    }

    /** main 切支仅系列首发；后续补丁在维护分支上 releaseTag。 */
    public static void requireSeriesStart(String version, String base) {
        if (!GitFacts.isSeriesStartVersion(version)) {
            throw new GradleException("在 main 上切维护分支只用于系列首发 <N.M>.0（先完成该开发分支的集成到主干）；"
                    + "后续补丁直接在 release/" + base + ".x 上执行 releaseTag；收到 '" + version + "'");
        }
    }

    /** 维护分支唯一性（本地存在即拒；远端存在性由接线查得后传入）。 */
    public static void assertLocalBranchFree(String target, boolean localExists) {
        if (localExists) {
            throw new GradleException("本地已存在分支 '" + target + "'，每系列维护分支唯一");
        }
    }

    public static void assertRemoteMaintenanceFree(String target, boolean remoteExists) {
        if (remoteExists) {
            throw new GradleException("远端已存在维护分支 '" + target + "'，每系列唯一");
        }
    }

    /** releaseTag 分支形态守卫（目标版本维护分支或同名一次性分支）。 */
    public static void requireTagBranch(String version, String base, String current) {
        boolean onMaintenance = current.equals("release/" + base + ".x");
        boolean onLegacyContainer = current.equals(version + ".release");
        if (!onMaintenance && !onLegacyContainer) {
            throw new GradleException("releaseTag 必须在目标版本的 release 维护分支 'release/" + base
                    + ".x'（新形态）或同名一次性发布分支 '" + version + ".release'（祖父轨）上执行，当前分支 '"
                    + current + "'");
        }
        return;
    }

    /** 标签幂等三态：无标签放行；附注且恰指当前构建提交放行（仅补推送）；轻量或错指拒绝（D11（一）语义保留）。 */
    public static void assertTagIdempotent(String tagName, Map<String, Object> existing, String buildCommit) {
        if (existing == null) {
            return;
        }
        boolean annotated = Boolean.TRUE.equals(existing.get("annotated"));
        String commit = (String) existing.get("commit");
        if (!annotated || !commit.equals(buildCommit)) {
            throw new GradleException("本地标签 '" + tagName + "' "
                    + (!annotated ? "是轻量标签（无解引用，门禁必拒）" : "指向非当前构建提交（" + commit + "）")
                    + "——未推送时人工删除本地错标后重跑本任务；已推送则绝对不动，提高补丁号重发");
        }
    }

    /** 下一补丁号校验（规格需求一；redesign D2）：远端最大补丁标签加一须等于目标补丁号。 */
    public static void assertNextPatchNumber(String base, String version, List<Integer> releasedPatches) {
        int patch = Integer.parseInt(version.split("\\.")[2]);
        int expected = GitFacts.nextPatchExpected(releasedPatches);
        if (patch != expected) {
            throw new GradleException("下一补丁号校验失败：系列 " + base + " 远端已有标签最大补丁号 "
                    + (releasedPatches.isEmpty() ? "（无）"
                    : String.valueOf(releasedPatches.stream().mapToInt(Integer::intValue).max().getAsInt()))
                    + "，目标版本 '" + version + "' 应为 '" + base + "." + expected + "'");
        }
    }

    /** 远端推定失败文案（resolveRemote 为 null 时）。 */
    public static void requireRemote(String purpose, String current) {
        if (purpose == null) {
            throw new GradleException("无法确定推送目标的远端：当前分支 '" + current + "' 没有上游，且仓库远端数量不是 1");
        }
    }
}
