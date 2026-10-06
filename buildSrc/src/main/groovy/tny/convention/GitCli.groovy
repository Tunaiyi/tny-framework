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
// git 纯通道门面（consolidate-git-cli-channel 收编，retire-grgit-channel 统一，
// consolidate-git-queries-into-gitflow 定型）：只负责"执行一条 git 命令并取回退出码与双流
// 输出"，全仓库只保留这一份"解析 -PgitExe、派生进程、取退出码与双流输出、失败即抛"的实现。
// 命令输出怎么解释成业务事实（远端引用解析、补丁号计数等）一律归 GitFlow 的查询方法面——
// 通道与语义分居，任何 ls-remote 解析只存在于 GitFlow 一处。
// 通道沿革：归档变更 revise-release-branch-flow 的设计 D6 曾定"写走 git CLI、仓库状态查询走
// grgit"双通道；JGit 三处实测缺陷（写 API 缺失回退 976896b4、附注标签解引用缺数、linked
// worktree 不跟随 commondir——见归档 migrate-git-calls-to-grgit 与 upgrade-grgit-for-worktrees）
// 在 retire-grgit-channel 中随依赖退场，GitFlow 的派生与查询亦走本门面。
// 调用方必须查 exit（D10（二）：查询失败不得被当成空输出）。
package tny.convention

class GitCli {

    final File rootDir
    final String exe

    private GitCli(File rootDir, String exe) {
        this.rootDir = rootDir
        this.exe = exe
    }

    // 项目绑定工厂：gitExe 解析规则（-PgitExe 覆盖 PATH 上的 git）收进门面，插件侧一行获取。
    static GitCli forProject(project) {
        return new GitCli(project.rootDir,
                (project.findProperty('gitExe') ?: 'git').toString())
    }

    // 返回 [exit, out, err]。git 各子命令输出量小，先尽读 stdout 再读 stderr 不会撑满管道缓冲；
    // 如未来出现大输出命令需改为并发流读取。
    Map run(List args) {
        def builder = new ProcessBuilder(([exe] + args.collect { it.toString() })).directory(rootDir)
        // 环境钉 LC_ALL=C（retire-grgit-channel D3）：git 消息与本地化日期输出受 locale
        // 影响（本仓零差异抓样口径同源教训），porcelain/rev-parse 数据行不受影响。
        builder.environment().put('LC_ALL', 'C')
        builder.redirectErrorStream(false)
        def proc = builder.start()
        def out = proc.inputStream.text
        def err = proc.errorStream.text
        [exit: proc.waitFor(), out: out, err: err]
    }

    Map require(List args, String failMsg) {
        def r = run(args)
        if (r.exit != 0) {
            throw new org.gradle.api.GradleException("${failMsg}：${r.err.trim()}")
        }
        return r
    }
}
