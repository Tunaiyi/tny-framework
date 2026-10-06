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
// git 通道门面（consolidate-git-cli-channel 收编，retire-grgit-channel 后为全仓唯一 git 通道）：
// 写侧与依赖执行期最新引用的查询统一经本类，全仓库只保留这一份"解析 -PgitExe、派生进程、
// 取退出码与双流输出、失败即抛"的实现——此前 tny.release 与 tny.integrate 各自复制同款三行
// 闭包模板、tny.publish 的标签存证段另用 providers.exec 第三实现，收编于此。
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

    // 远端 v<base>.* 裸号附注标签的补丁号集合（只认 ^{} 解引用行，轻量标签天然不计入）。
    // 教训（redesign-devline-integration-model e2e 第九轮实锤）：Groovy 的 ~/.../ slashy
    // 字符串不做 ${} 插值，含插值的模式必须字符串拼接构造，否则模式带着字面 "${base}"
    // 永不匹配、枚举恒为空。
    List<Integer> remoteTagPatches(String remoteName, String base) {
        def pattern = '^([0-9a-f]+)\\s+refs/tags/v' + java.util.regex.Pattern.quote(base) + '\\.(\\d+)\\^\\{\\}$'
        def re = java.util.regex.Pattern.compile(pattern)
        run(['ls-remote', '--tags', remoteName, "refs/tags/v${base}*"]).out.readLines()
                .collect { re.matcher(it) }.findAll { it.matches() }.collect { it.group(2) as Integer }
    }
}
