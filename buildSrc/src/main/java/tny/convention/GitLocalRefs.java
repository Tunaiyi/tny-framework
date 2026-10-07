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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地引用查询支撑类（convert-orchestration-to-java 任务 7.3 长度界线拆出：主规格"扫读测试与长度界线"
 * 需求按单类计量 250 行，原 GitFlow 本地脏项、分支与标签查询段整体迁入本类，GitFlow 公开方法名
 * 逐字保留为委托壳）。与 {@link GitRemoteQueries} 对称：本类只读本地 refs 与工作树，
 * 解析与判定一律委托 {@link GitFacts}，本类只做通道编排。
 */
final class GitLocalRefs {

    private final GitCli cli;

    GitLocalRefs(GitCli cli) {
        this.cli = cli;
    }

    /** 跟踪文件脏项路径（未跟踪的 ?? 行丢弃）——脏检查口径"仅跟踪文件变更"，等价映射见 GitFacts。 */
    List<String> trackedDirtyPaths() {
        return GitFacts.porcelainTrackedPaths(
                GitFacts.outLines((String) cli.run(List.of("status", "--porcelain=1")).get("out")));
    }

    List<String> branchNames() {
        return GitFacts.outLinesNonBlank(
                (String) cli.run(List.of("for-each-ref", "--format=%(refname:lstrip=2)", "refs/heads/")).get("out"));
    }

    List<String> tagNames() {
        return GitFacts.outLinesNonBlank(
                (String) cli.run(List.of("for-each-ref", "--format=%(refname:lstrip=2)", "refs/tags/")).get("out"));
    }

    /**
     * 标签形态与指向（releaseTag 幂等判定的等价替换）：不存在返回 null；
     * 附注标签的 commit 取解引用（peel）后的提交号，轻量标签 annotated 为 false。
     * 返回键 annotated/commit 的 Map（消费方既有属性访问形态保持）。
     */
    Map<String, Object> tagInfo(String tag) {
        Map<String, Object> exists = cli.run(List.of("rev-parse", "-q", "--verify", "refs/tags/" + tag));
        if ((Integer) exists.get("exit") != 0) {
            return null;
        }
        String type = cli.run(List.of("cat-file", "-t", "refs/tags/" + tag)).get("out").toString().trim();
        Map<String, Object> peeled = cli.run(List.of("rev-parse", "refs/tags/" + tag + "^{}"));
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("annotated", "tag".equals(type));
        info.put("commit", (Integer) peeled.get("exit") == 0
                ? peeled.get("out").toString().trim() : exists.get("out").toString().trim());
        return info;
    }
}
