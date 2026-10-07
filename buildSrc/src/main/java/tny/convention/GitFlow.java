/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or as required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention;

import org.gradle.api.GradleException;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 根工程的 git 派生事实契约。唯一写入方是 tny.git 约定插件（经带门面的构造调用一步完成构造与注册），
 * 消费方按类型拉取。通道沿革：本类初版经 grgit（JGit）读 .git（expose-git-info-extension）；
 * 全仓 git 能力统一走 git CLI 由变更 retire-grgit-channel 完成——JGit 三处实测缺陷
 * （写 API 残缺、附注标签解引用缺失、linked worktree 不跟随 commondir，证据见归档
 * migrate-git-calls-to-grgit 与 upgrade-grgit-for-worktrees）随依赖退场一并消除，
 * linked worktree 构建禁令同时解除。
 *
 * <p>载体沿革：Groovy 类经 convert-orchestration-to-java 任务 5.1 转 Java——字段与公开方法名
 * 逐字保持（Groovy 消费方属性访问映射到同名 getter，takyInfo/remoteRefs 等 Map 返回形态不变）；
 * 解析与派生判定委托 {@link GitFacts}（红绿单测在案），本类保留构造期派生与通道编排。
 * 实现 {@link GitVersionSource} 供 tny.projects 注入点类型化读取（弱型反射就此撤销）。
 * 字段派生值构造期一次求值、此后只读（P4 不变量语义不变）。
 */
public class GitFlow implements GitVersionSource {

    // —— 后缀常量（裸三段号规范出处：openspec change adopt-plain-ga-versioning）——
    public static final String RELEASE_VERSION_SUFFIX = GitFacts.RELEASE_VERSION_SUFFIX;
    // RELEASE_PACK_SUFFIX 是历史正式版后缀形态专用（2.x 至 5.5 存量制品，见 gradle/released-legacy.txt）；
    // 新规范下正式版发布派生裸三段号（adopt-plain-ga-versioning 决策 D1），派生路径不再使用该常量。
    public static final String RELEASE_PACK_SUFFIX = "-RELEASE";
    public static final String SNAPSHOT_PACK_SUFFIX = GitFacts.SNAPSHOT_PACK_SUFFIX;
    public static final String DEV_BRANCH_PREFIX = GitFacts.DEV_BRANCH_PREFIX;
    public static final String RELEASE_BRANCH_PREFIX = GitFacts.RELEASE_BRANCH_PREFIX;

    // —— 派生值：final 字段配 getter，值在构造器内定死（原自动 getter 语义保持）——
    private final GitCli cli;
    private final String branchName;
    private final String branchVersion;
    private final String projectVersion;
    private final String commitId;
    private final String commitTime;
    private final String buildTime;
    // 祖父条款登记（redesign-devline-integration-model D7）：分支名到身份的映射，
    // 由 tny.git 插件读 gradle/branch-legacy-registry.txt 注入；门禁按登记身份放行旧形态。
    private final Map<String, String> legacyRegistry;

    // 构造时序沿用既有填充序：先分支名，其后由分支名与 HEAD 逐个派生；buildTime 取构造时刻
    // 的系统时钟而非 git 事实。commitTime 语义保持"提交时刻换算到 JVM 默认时区"：
    // 经 %ct（epoch 秒）自建格式化，不用 git 的本地化日期输出，防止时区口径漂移
    // （旧式 new Date(ms).format 的 java.time 等价，时区显式取 JVM 默认）。
    // 空仓（unborn HEAD）时 rev-parse 非零退出，报错取代初版的裸 NPE 兜底。
    public GitFlow(GitCli cli, Map<String, String> registry, String releaseVersion) {
        this.cli = cli;
        this.legacyRegistry = registry == null ? Map.of() : Map.copyOf(registry);
        Map<String, Object> headProbe = cli.run(List.of("rev-parse", "--verify", "HEAD"));
        if ((Integer) headProbe.get("exit") != 0) {
            throw new GradleException("git 仓库无提交可解析（HEAD 未定）：仓库可能损坏、为空仓或处于不支持的布局；"
                    + "git 命令输出：" + ((String) headProbe.get("err")).trim());
        }
        String nameProbe = cli.require(List.of("rev-parse", "--abbrev-ref", "HEAD"), "读取当前分支名失败")
                .get("out").toString();
        branchName = nameProbe.trim();
        branchVersion = GitFacts.parseBranchVersion(branchName);
        projectVersion = GitFacts.parseProjectVersion(branchName, releaseVersion);
        commitId = ((String) headProbe.get("out")).trim();
        String epochOut = cli.require(List.of("show", "-s", "--format=%ct", "HEAD"), "读取提交时间失败")
                .get("out").toString();
        long epochSeconds = Long.parseLong(epochOut.trim());
        commitTime = GitFacts.formatCommitTime(epochSeconds, TimeZone.getDefault().toZoneId());
        buildTime = GitFacts.formatCommitTime(System.currentTimeMillis() / 1000L,
                TimeZone.getDefault().toZoneId());
    }

    public GitCli getCli() {
        return cli;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getBranchVersion() {
        return branchVersion;
    }

    @Override
    public String getProjectVersion() {
        return projectVersion;
    }

    public String getCommitId() {
        return commitId;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public Map<String, String> getLegacyRegistry() {
        return legacyRegistry;
    }

    // —— 兼容访问器（语义等价保留，通道 CLI；原 Groovy 方法名逐字）——
    public String gitCommitId() {
        return commitId;
    }

    public java.util.Date gitHeadCommit() {
        return gitCommitDateTime();
    }

    public java.util.Date gitCommitDateTime() {
        long epochSeconds = Long.parseLong(cli.require(
                List.of("show", "-s", "--format=%ct", "HEAD"), "读取提交时间失败")
                .get("out").toString().trim());
        return new java.util.Date(epochSeconds * 1000L);
    }

    public String gitBranchName() {
        return cli.run(List.of("rev-parse", "--abbrev-ref", "HEAD")).get("out").toString().trim();
    }

    public String gitBranchType() {
        return GitFacts.branchType(gitBranchName());
    }

    /** 跟踪文件脏项路径（未跟踪的 ?? 行丢弃）——脏检查口径"仅跟踪文件变更"，等价映射见 GitFacts。 */
    public List<String> trackedDirtyPaths() {
        return GitFacts.porcelainTrackedPaths(readLines(cli.run(List.of("status", "--porcelain=1"))));
    }

    public boolean isConfigChange(String dir) {
        return trackedDirtyPaths().stream().anyMatch(path -> path.startsWith(dir));
    }

    public List<String> branchNames() {
        return nonBlank(cli.run(List.of("for-each-ref", "--format=%(refname:lstrip=2)", "refs/heads/")));
    }

    public List<String> tagNames() {
        return nonBlank(cli.run(List.of("for-each-ref", "--format=%(refname:lstrip=2)", "refs/tags/")));
    }

    /**
     * 标签形态与指向（releaseTag 幂等判定的等价替换）：不存在返回 null；
     * 附注标签的 commit 取解引用（peel）后的提交号，轻量标签 annotated 为 false。
     * 返回键 annotated/commit 的 Map（消费方既有属性访问形态保持）。
     */
    public Map<String, Object> tagInfo(String tag) {
        Map<String, Object> exists = cli.run(List.of("rev-parse", "-q", "--verify", "refs/tags/" + tag));
        if ((Integer) exists.get("exit") != 0) {
            return null;
        }
        String type = cli.run(List.of("cat-file", "-t", "refs/tags/" + tag)).get("out").toString().trim();
        Map<String, Object> peeled = cli.run(List.of("rev-parse", "refs/tags/" + tag + "^{}"));
        java.util.Map<String, Object> info = new java.util.LinkedHashMap<>();
        info.put("annotated", "tag".equals(type));
        info.put("commit", (Integer) peeled.get("exit") == 0
                ? peeled.get("out").toString().trim() : exists.get("out").toString().trim());
        return info;
    }

    /**
     * 获取当前提交可追溯的最近发布标签。仅匹配裸号形态 v<主>.<次>.<补丁>
     * （新规范见 openspec change adopt-plain-ga-versioning 决策 D6）；
     * 历史 "v*-RELEASE" 形态的标签不参与匹配，找不到时返回 null。
     * describe 的 --match 没有负向锚：v3.4.1-RELEASE 与 v9.9.9-rc1 都会命中 "v[0-9]*"，
     * 因此取回结果后再用严格正则过滤（实测教训随 CLI 通道原样迁移）。
     * 已知局限：若 HEAD 沿祖先链最近的匹配标签是旧代形态而更远处存在裸号标签，
     * 本方法返回 null 而非跳过继续找——旧代与新代标签混存的过渡期才会遇到。
     */
    public String gitTag() {
        Map<String, Object> probe = cli.run(List.of("describe", "--tags", "--match", "v[0-9]*"));
        if ((Integer) probe.get("exit") != 0) {
            return null;
        }
        return GitFacts.describeTagOrNull((String) probe.get("out"));
    }

    /** 登记身份查询：未登记返回 null。 */
    public String legacyIdentity(String branch) {
        return legacyRegistry.get(branch);
    }

    /** 是否祖父登记的豁免线。 */
    public boolean isLegacyEligible(String branch) {
        return legacyIdentity(branch) != null;
    }

    // —— 远端查询方法面（consolidate-git-queries-into-gitflow D1）——
    // remoteRefs 是全仓唯一的 ls-remote 输出解析位置（守卫解析在 GitFacts，调用点不各自持有）。
    public Map<String, String> remoteRefs(String remoteName, List<String> flagsAndPatterns) {
        return GitFacts.parseLsRemoteLines(readLines(
                cli.run(concat(List.of("ls-remote"), flagsAndPatterns, List.of(remoteName)))));
    }

    public Map<String, String> remoteRefs(String remoteName) {
        return remoteRefs(remoteName, List.of());
    }

    /** 键集合形态（存在性与前缀撞名判定用）：heads/tags 标志映射与原调用点一致。 */
    public List<String> remoteRefNames(String remoteName, boolean heads, boolean tags) {
        List<String> flags = (heads && !tags) ? List.of("--heads")
                : (!heads && tags) ? List.of("--tags") : List.of();
        return List.copyOf(remoteRefs(remoteName, flags).keySet());
    }

    /** 系列已发布补丁号集合：只认 ^{} 解引用行（轻量标签无该行，天然不参与计数）。 */
    public List<Integer> remoteReleasedPatches(String remoteName, String base) {
        return GitFacts.releasedPatchesFromRefs(
                remoteRefs(remoteName, List.of("--tags", "refs/tags/v" + base + "*")).keySet(), base);
    }

    /** 分支存在性：精确键查询（后缀匹配带来的邻近行由键全等过滤兜掉）。 */
    public boolean remoteBranchExists(String remoteName, String branch) {
        return remoteRefs(remoteName, List.of("--heads", "refs/heads/" + branch))
                .containsKey("refs/heads/" + branch);
    }

    /**
     * 推送/查询目标的远端名推定：分支配了上游取上游前缀，否则仅当仓库只配置一个远端才采用；
     * 推定不出返回 null 由调用方报错。上游读取按显式分支参数化（原 grgit trackingBranch 的等价）。
     */
    public String resolveRemoteName(String branch) {
        Map<String, Object> upstream = cli.run(
                List.of("rev-parse", "--abbrev-ref", "--symbolic-full-name", branch + "@{upstream}"));
        if ((Integer) upstream.get("exit") == 0) {
            String name = upstream.get("out").toString().trim();
            if (name.contains("/")) {
                return name.substring(0, name.indexOf('/'));
            }
        }
        List<String> remotes = nonBlank(cli.run(List.of("remote")));
        return remotes.size() == 1 ? remotes.get(0).trim() : null;
    }

    public boolean isReleaseVersion(String version) {
        return GitFacts.isReleaseVersion(version);
    }

    /** 分支名到版本基的派生（redesign D2），委托 GitFacts。 */
    public String parseBranchVersion(String version) {
        return GitFacts.parseBranchVersion(version);
    }

    /** 项目版本派生（委托 GitFacts，语义注释见 GitFacts 对应方法）。 */
    public String parseProjectVersion(String version, String releaseVersion) {
        return GitFacts.parseProjectVersion(version, releaseVersion);
    }

    private List<String> readLines(Map<String, Object> runResult) {
        String out = runResult.get("out").toString();
        return java.util.Arrays.stream(out.split("\n", -1)).toList();
    }

    private List<String> nonBlank(Map<String, Object> runResult) {
        return readLines(runResult).stream().filter(line -> !line.isEmpty()).toList();
    }

    private static List<String> concat(List<String> a, List<String> b, List<String> c) {
        java.util.List<String> all = new java.util.ArrayList<>(a);
        all.addAll(b);
        all.addAll(c);
        return all;
    }
}
