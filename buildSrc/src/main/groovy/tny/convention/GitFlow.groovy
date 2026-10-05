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
// 根工程的 git 派生事实契约。它的唯一写入方是 tny.git 约定插件（经带句柄的构造调用一步完成构造与注册），
// 消费方按类型拉取。本类由 expose-git-info-extension 变更引入，gitinfo-self-init-and-getters 变更
// 将扩展名更名为 gitFlow、类名更名为 GitFlow，并把派生值初始化收进构造器：句柄注入后按既定时序就地求值，
// 属性全部为只读，构造完成即不可变（名称沿革详见两册的验收记录）。
// 字段与参数用 Groovy 动态类型：buildSrc 编译期不依赖 grgit 类，运行时类经根构建脚本先行应用
// org.ajoberstar.grgit 插件后可达（expose-git-info-extension 设计文档决策 D1，与 adopt-gradle-official-dsl
// 变更设计决策 D9 的"buildSrc 不引插件栈"约束一致）。
// 推导方法体承自 expose-git-info-extension 的逐行迁移，不顺手重写（其设计文档决策 D2）；实测教训注释随语句同行。
package tny.convention

import org.gradle.api.GradleException

class GitFlow {

    // —— 后缀与前缀常量（裸三段号规范出处：openspec change adopt-plain-ga-versioning；
    // dev/ 与 release/ 前缀形态由 openspec change redesign-devline-integration-model 引入） ——
    final RELEASE_VERSION_SUFFIX = '.release'
    // RELEASE_PACK_SUFFIX 是历史正式版后缀形态专用（2.x 至 5.5 存量制品，见 gradle/released-legacy.txt）；
    // 新规范下正式版发布派生裸三段号（openspec change adopt-plain-ga-versioning 决策 D1），派生路径不再使用该常量。
    final RELEASE_PACK_SUFFIX = '-RELEASE'
    final SNAPSHOT_PACK_SUFFIX = '-SNAPSHOT'
    final DEV_BRANCH_PREFIX = 'dev/'
    final RELEASE_BRANCH_PREFIX = 'release/'

    // —— grgit 仓库句柄与六个派生值：final 属性自动生成 getter、不设 setter，值在构造器内定死 ——
    final grgiter
    final branchName
    final branchVersion
    final projectVersion
    final commitId
    final commitTime
    final buildTime
    // 祖父条款登记（redesign-devline-integration-model D7）：分支名到身份（maintenance/dev/read-only）的映射，
    // 由 tny.git 插件读 gradle/branch-legacy-registry.txt 注入；门禁按登记身份放行旧形态。
    final legacyRegistry

    // 构造时序承自原 ext 块尾段与 expose-git-info-extension 变更的填充序：先分支名，其后由分支名与 HEAD 逐个派生；
    // buildTime 取构造时刻的系统时钟而非 git 事实。全部求值一次，值为 String。
    // releaseVersion 是 -PreleaseVersion 参数的原样透传（release 维护分支发裸号时由门禁与 release 任务校验，
    // 本类只在派生 projectVersion 时做前缀一致性核对，不一致派生为 null 交由门禁 fail-closed 处理）。
    GitFlow(git, Map registry = [:], String releaseVersion = null) {
        grgiter = git
        legacyRegistry = registry
        // 兜底守卫（upgrade-grgit-for-worktrees D1 第二道）：位置判据未命中而 HEAD 仍解析不出时
        // 给通用异常，替代裸 NPE；worktree 场景已在 tny.git 的守卫处拦截，不会走到这里
        if (git.head() == null) {
            throw new GradleException('git 仓库句柄异常：HEAD 无法解析（仓库可能损坏或处于未受支持的 git 布局；' +
                    '注意 gradle 不支持在 git linked worktree 中构建，请改用完整克隆）')
        }
        branchName = gitBranchName()
        branchVersion = parseBranchVersion(branchName)
        projectVersion = parseProjectVersion(branchName, releaseVersion)
        commitId = gitCommitId()
        commitTime = gitCommitDateTime().format('yyyyMMdd_HHmm')
        buildTime = new Date().format('yyyyMMdd_HHmm', TimeZone.getDefault())
    }

    // 登记身份查询：未登记返回 null。
    def legacyIdentity(String branch) {
        return legacyRegistry[branch]
    }

    // 是否祖父登记的豁免线（身份为 maintenance 或 dev 的旧形态线）。
    def isLegacyEligible(String branch) {
        return legacyIdentity(branch) != null
    }

    def gitCommitId() {
        return grgiter.head().id
    }

    def gitCommitDateTime() {
        return grgiter.head().dateTime
    }

    def gitHeadCommit() {
        return grgiter.head().dateTime
    }

    def gitBranchName() {
        return grgiter.branch.current().getName()
    }

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

    def isConfigChange(dir) {
        def status = grgiter.status()
        def changeConfig = status.unstaged.getAllChanges().findAll { it.startsWith(dir) }
        return !changeConfig.isEmpty()
    }

    /**
     * 获取当前提交可追溯的最近发布标签。仅匹配裸号形态 v<主>.<次>.<补丁>
     * （新规范见 openspec change adopt-plain-ga-versioning 决策 D6）；
     * 历史 "v*-RELEASE" 形态的标签不参与匹配，找不到时返回 null。
     */
    def gitTag() {
        def tag = grgiter.describe {
            tags = true
            match = ['v[0-9]*']
        }
        // describe 的 glob 没有负向锚：v3.4.1-RELEASE 与 v9.9.9-rc1 都会命中 "v[0-9]*"，
        // 因此取回结果后再用严格正则过滤，方法保证只返回裸号形态（实测教训记入本注释）。
        // 已知局限：若 HEAD 沿祖先链最近的匹配标签是旧代形态而更远处存在裸号标签，
        // 本方法返回 null 而非跳过继续找——旧代与新代标签混存的过渡期才会遇到。
        if (tag == null || !(tag ==~ /v\d+\.\d+\.\d+/)) {
            return null
        }
        def tags = grgiter.tag.list()
                .findAll { it.name == tag }
        if (tags.isEmpty()) {
            return null
        }
        return tags[0 as String]
    }

    def isReleaseVersion(versionNumber) {
        return versionNumber.endsWith(RELEASE_VERSION_SUFFIX)
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
