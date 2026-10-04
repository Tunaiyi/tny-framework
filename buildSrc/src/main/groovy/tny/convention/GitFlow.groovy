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

    // —— 后缀常量（裸三段号规范出处：openspec change adopt-plain-ga-versioning） ——
    final RELEASE_VERSION_SUFFIX = '.release'
    // RELEASE_PACK_SUFFIX 是历史正式版后缀形态专用（2.x 至 5.5 存量制品，见 gradle/released-legacy.txt）；
    // 新规范下正式版发布派生裸三段号（openspec change adopt-plain-ga-versioning 决策 D1），派生路径不再使用该常量。
    final RELEASE_PACK_SUFFIX = '-RELEASE'
    final SNAPSHOT_PACK_SUFFIX = '-SNAPSHOT'

    // —— grgit 仓库句柄与六个派生值：final 属性自动生成 getter、不设 setter，值在构造器内定死 ——
    final grgiter
    final branchName
    final branchVersion
    final projectVersion
    final commitId
    final commitTime
    final buildTime

    // 构造时序承自原 ext 块尾段与 expose-git-info-extension 变更的填充序：先分支名，其后由分支名与 HEAD 逐个派生；
    // buildTime 取构造时刻的系统时钟而非 git 事实。全部求值一次，值为 String。
    GitFlow(git) {
        grgiter = git
        // 兜底守卫（upgrade-grgit-for-worktrees D1 第二道）：位置判据未命中而 HEAD 仍解析不出时
        // 给通用异常，替代裸 NPE；worktree 场景已在 tny.git 的守卫处拦截，不会走到这里
        if (git.head() == null) {
            throw new GradleException('git 仓库句柄异常：HEAD 无法解析（仓库可能损坏或处于未受支持的 git 布局；' +
                    '注意 gradle 不支持在 git linked worktree 中构建，请改用完整克隆）')
        }
        branchName = gitBranchName()
        branchVersion = parseBranchVersion(branchName)
        projectVersion = parseProjectVersion(branchName)
        commitId = gitCommitId()
        commitTime = gitCommitDateTime().format('yyyyMMdd_HHmm')
        buildTime = new Date().format('yyyyMMdd_HHmm', TimeZone.getDefault())
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

    def parseBranchVersion(version) {
        if (isReleaseVersion(version)) {
            return version.substring(0, version.length() - RELEASE_VERSION_SUFFIX.length())
        }
        return version
    }

    // 正式版派生改为裸三段号（openspec change adopt-plain-ga-versioning 决策 D1）：
    // 发布分支 N.M.K.release 直接产出 N.M.K，不再追加 RELEASE_PACK_SUFFIX；
    // RELEASE_PACK_SUFFIX 常量保留，仅用于历史黑名单语义与历史形态说明（gradle/released-legacy.txt）。
    def parseProjectVersion(version) {
        if (isReleaseVersion(version)) {
            return parseBranchVersion(version)
        }
        return version + SNAPSHOT_PACK_SUFFIX
    }

}
