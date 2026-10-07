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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 远端查询支撑类（convert-orchestration-to-java 任务 7.3 长度界线拆出：主规格"扫读测试与长度界线"
 * 需求按单类计量 250 行，原 GitFlow 远端段整体迁入本类，GitFlow 公开方法名逐字保留为委托壳）。
 * 远端查询方法面的沿革契约不变（consolidate-git-queries-into-gitflow D1）：remoteRefs 是全仓唯一的
 * ls-remote 输出解析位置（守卫解析在 {@link GitFacts#parseLsRemoteLines}，调用点不各自持有）；
 * 本类只做通道编排与参数拼装，判定与解析一律委托 GitFacts。
 */
final class GitRemoteQueries {

    private final GitCli cli;

    GitRemoteQueries(GitCli cli) {
        this.cli = cli;
    }

    Map<String, String> remoteRefs(String remoteName, List<String> flagsAndPatterns) {
        List<String> all = new ArrayList<>(List.of("ls-remote"));
        all.addAll(flagsAndPatterns);
        all.add(remoteName);
        return GitFacts.parseLsRemoteLines(GitFacts.outLines((String) cli.run(all).get("out")));
    }

    Map<String, String> remoteRefs(String remoteName) {
        return remoteRefs(remoteName, List.of());
    }

    /** 键集合形态（存在性与前缀撞名判定用）：heads/tags 标志映射与原调用点一致。 */
    List<String> remoteRefNames(String remoteName, boolean heads, boolean tags) {
        List<String> flags = (heads && !tags) ? List.of("--heads")
                : (!heads && tags) ? List.of("--tags") : List.of();
        return List.copyOf(remoteRefs(remoteName, flags).keySet());
    }

    /** 系列已发布补丁号集合：只认 ^{} 解引用行（轻量标签无该行，天然不参与计数）。 */
    List<Integer> remoteReleasedPatches(String remoteName, String base) {
        return GitFacts.releasedPatchesFromRefs(
                remoteRefs(remoteName, List.of("--tags", "refs/tags/v" + base + "*")).keySet(), base);
    }

    /** 分支存在性：精确键查询（后缀匹配带来的邻近行由键全等过滤兜掉）。 */
    boolean remoteBranchExists(String remoteName, String branch) {
        return remoteRefs(remoteName, List.of("--heads", "refs/heads/" + branch))
                .containsKey("refs/heads/" + branch);
    }

    /**
     * 推送/查询目标的远端名推定：分支配了上游取上游前缀，否则仅当仓库只配置一个远端才采用；
     * 推定不出返回 null 由调用方报错。上游读取按显式分支参数化（原 grgit trackingBranch 的等价）。
     */
    String resolveRemoteName(String branch) {
        Map<String, Object> upstream = cli.run(
                List.of("rev-parse", "--abbrev-ref", "--symbolic-full-name", branch + "@{upstream}"));
        if ((Integer) upstream.get("exit") == 0) {
            String name = upstream.get("out").toString().trim();
            if (name.contains("/")) {
                return name.substring(0, name.indexOf('/'));
            }
        }
        List<String> remotes = GitFacts.outLinesNonBlank((String) cli.run(List.of("remote")).get("out"));
        return remotes.size() == 1 ? remotes.get(0).trim() : null;
    }
}
