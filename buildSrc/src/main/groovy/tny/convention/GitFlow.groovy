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
// 根工程的 git 派生事实契约。唯一写入方是 tny.git 约定插件（经带门面的构造调用一步完成构造与注册），
// 消费方按类型拉取。通道沿革：本类初版经 grgit（JGit）读 .git（expose-git-info-extension）；
// 全仓 git 能力统一走 git CLI 由变更 retire-grgit-channel 完成——JGit 三处实测缺陷
// （写 API 残缺、附注标签解引用缺失、linked worktree 不跟随 commondir，证据见归档
// migrate-git-calls-to-grgit 与 upgrade-grgit-for-worktrees）随依赖退场一并消除，
// linked worktree 构建禁令同时解除。
// 字段与参数用 Groovy 动态类型保持既有书写形态；派生值构造期一次求值、此后只读（P4 不变量语义不变）。
package tny.convention

import org.gradle.api.GradleException

class GitFlow {

    // —— 后缀常量（裸三段号规范出处：openspec change adopt-plain-ga-versioning） ——
    final RELEASE_VERSION_SUFFIX = '.release'
    // RELEASE_PACK_SUFFIX 是历史正式版后缀形态专用（2.x 至 5.5 存量制品，见 gradle/released-legacy.txt）；
    // 新规范下正式版发布派生裸三段号（openspec change adopt-plain-ga-versioning 决策 D1），派生路径不再使用该常量。
    final RELEASE_PACK_SUFFIX = '-RELEASE'
    final SNAPSHOT_PACK_SUFFIX = '-SNAPSHOT'
    final DEV_BRANCH_PREFIX = 'dev/'
    final RELEASE_BRANCH_PREFIX = 'release/'

    // —— 派生值：final 属性自动生成 getter、不设 setter，值在构造器内定死 ——
    final cli
    final branchName
    final branchVersion
    final projectVersion
    final commitId
    final commitTime
    final buildTime
    // 祖父条款登记（redesign-devline-integration-model D7）：分支名到身份的映射，
    // 由 tny.git 插件读 gradle/branch-legacy-registry.txt 注入；门禁按登记身份放行旧形态。
    final legacyRegistry

    // 构造时序沿用既有填充序：先分支名，其后由分支名与 HEAD 逐个派生；buildTime 取构造时刻
    // 的系统时钟而非 git 事实。commitTime 语义保持"提交时刻换算到 JVM 默认时区"：
    // 经 %ct（epoch 秒）自建 Date 再格式化，不用 git 的本地化日期输出，防止时区口径漂移。
    // 空仓（unborn HEAD）时 rev-parse 非零退出，报错取代初版的裸 NPE 兜底。
    GitFlow(GitCli cli, Map registry = [:], String releaseVersion = null) {
        this.cli = cli
        legacyRegistry = registry
        def headProbe = cli.run(['rev-parse', '--verify', 'HEAD'])
        if (headProbe.exit != 0) {
            throw new GradleException('git 仓库无提交可解析（HEAD 未定）：仓库可能损坏、为空仓或处于不支持的布局；' +
                    'git 命令输出：' + headProbe.err.trim())
        }
        def nameProbe = cli.require(['rev-parse', '--abbrev-ref', 'HEAD'], '读取当前分支名失败')
        branchName = nameProbe.out.trim()
        branchVersion = parseBranchVersion(branchName)
        projectVersion = parseProjectVersion(branchName, releaseVersion)
        commitId = headProbe.out.trim()
        def epochProbe = cli.require(['show', '-s', '--format=%ct', 'HEAD'], '读取提交时间失败')
        commitTime = new Date((epochProbe.out.trim() as long) * 1000L).format('yyyyMMdd_HHmm')
        buildTime = new Date().format('yyyyMMdd_HHmm', TimeZone.getDefault())
    }

    // —— 兼容访问器（语义等价保留，通道改 CLI） ——
    def gitCommitId() { return commitId }
    def gitHeadCommit() { return gitCommitDateTime() }
    def gitCommitDateTime() { return new Date((cli.require(['show', '-s', '--format=%ct', 'HEAD'], '读取提交时间失败').out.trim() as long) * 1000L) }
    def gitBranchName() { return cli.run(['rev-parse', '--abbrev-ref', 'HEAD']).out.trim() }

    def gitBranchType() {
        def name = gitBranchName()
        if (name.startsWith(DEV_BRANCH_PREFIX) || name.startsWith(RELEASE_BRANCH_PREFIX)) {
            return 'version'
        }
        if (name.toLowerCase().endsWith('.release')) {
            return 'release'
        }
        if (name.toLowerCase().endsWith('.x')) {
            return 'dev'
        }
        return 'test'
    }

    // 跟踪文件脏项路径（未跟踪的 ?? 行丢弃）——脏检查口径"仅跟踪文件变更"，等价映射：
    // porcelain v1 每行"XY 路径"，X/Y 任一非空格即跟踪变更；重命名行保留"旧 -> 新"原文供报错展示。
    def trackedDirtyPaths() {
        return cli.run(['status', '--porcelain=1']).out.readLines()
                .findAll { it.length() > 3 && !(it.startsWith('??')) }
                .collect { it.substring(3) }
    }

    def isConfigChange(dir) {
        return trackedDirtyPaths().any { it.startsWith(dir) }
    }

    def branchNames() {
        return cli.run(['for-each-ref', '--format=%(refname:lstrip=2)', 'refs/heads/']).out.readLines().findAll { it }
    }

    def tagNames() {
        return cli.run(['for-each-ref', '--format=%(refname:lstrip=2)', 'refs/tags/']).out.readLines().findAll { it }
    }

    // 标签形态与指向（releaseTag 幂等判定的等价替换）：不存在返回 null；
    // 附注标签的 commit 取解引用（peel）后的提交号，轻量标签 annotated 为 false。
    def tagInfo(String tag) {
        def exists = cli.run(['rev-parse', '-q', '--verify', "refs/tags/${tag}"])
        if (exists.exit != 0) {
            return null
        }
        def type = cli.run(['cat-file', '-t', "refs/tags/${tag}"]).out.trim()
        def peeled = cli.run(['rev-parse', "refs/tags/${tag}^{}"])
        return [annotated: type == 'tag', commit: (peeled.exit == 0 ? peeled.out.trim() : exists.out.trim())]
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
    def gitTag() {
        def probe = cli.run(['describe', '--tags', '--match', 'v[0-9]*'])
        if (probe.exit != 0) {
            return null
        }
        def tag = probe.out.trim()
        return (tag ==~ /v\d+\.\d+\.\d+/) ? tag : null
    }

    // 登记身份查询：未登记返回 null。
    def legacyIdentity(String branch) {
        return legacyRegistry[branch]
    }

    // 是否祖父登记的豁免线。
    def isLegacyEligible(String branch) {
        return legacyIdentity(branch) != null
    }

    // 推送/查询目标的远端名推定：分支配了上游取上游前缀，否则仅当仓库只配置一个远端才采用；
    // 推定不出返回 null 由调用方报错。上游读取按显式分支参数化（原 grgit trackingBranch 的等价）。
    def resolveRemoteName(String branch) {
        def upstream = cli.run(['rev-parse', '--abbrev-ref', '--symbolic-full-name', "${branch}@{upstream}"])
        if (upstream.exit == 0) {
            def name = upstream.out.trim()
            if (name.contains('/')) {
                return name.substring(0, name.indexOf('/'))
            }
        }
        def remotes = cli.run(['remote']).out.readLines().findAll { it.trim() }
        return remotes.size() == 1 ? remotes[0].trim() : null
    }

    def isReleaseVersion(version) {
        return version.endsWith(RELEASE_VERSION_SUFFIX)
    }

    // 分支名到版本基的派生（redesign-devline-integration-model D2）：
    // dev/N.M.x 与 release/N.M.x 去前缀得 N.M.x；旧一次性形态 N.M.K.release 去后缀得 N.M.K；
    // 其余（祖父线 N.M.x、main、工位名）原样返回，发布资格由门禁形态判定把关。
    def parseBranchVersion(version) {
        if (version.startsWith(DEV_BRANCH_PREFIX) || version.startsWith(RELEASE_BRANCH_PREFIX)) {
            return version.substring(version.indexOf('/') + 1)
        }
        if (isReleaseVersion(version)) {
            return version.substring(0, version.length() - RELEASE_VERSION_SUFFIX.length())
        }
        return version
    }

    // 项目版本派生：开发版本分支与旧开发线得 N.M.x-SNAPSHOT（坐标字符串不含 dev/ 前缀，
    // 与迁移前下游声明完全兼容）；release 维护分支须由 -PreleaseVersion 注入裸号且前两段
    // 与分支编号一致，否则派生 null（门禁按 null 拒绝发布，构建与编译不受影响）；
    // 旧一次性发布分支派生裸三段号（adopt-plain-ga-versioning 决策 D1 语义保留至其退役）。
    def parseProjectVersion(version, String releaseVersion = null) {
        if (version.startsWith(DEV_BRANCH_PREFIX)) {
            return parseBranchVersion(version) + SNAPSHOT_PACK_SUFFIX
        }
        if (version.startsWith(RELEASE_BRANCH_PREFIX)) {
            def base = parseBranchVersion(version)
            if (releaseVersion == null || !(releaseVersion ==~ /\d+\.\d+\.\d+/)) {
                return null
            }
            return releaseVersion.startsWith(base.substring(0, base.lastIndexOf('.') + 1)) ? releaseVersion : null
        }
        if (isReleaseVersion(version)) {
            return parseBranchVersion(version)
        }
        return version + SNAPSHOT_PACK_SUFFIX
    }

}
