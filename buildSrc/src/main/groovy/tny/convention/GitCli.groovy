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
// git CLI 通道共享助手（redesign-devline-integration-model）：写侧与依赖执行期最新引用的查询
// 走 git 命令行的通道边界不变（原 tny.release 内部闭包提升为类，供 release 与 integrate 两插件复用，
// 第三处使用即规则三次线内）。调用方必须查 exit——查询失败不得被当成空输出。
package tny.convention

class GitCli {

    // 返回 [exit, out, err]。git 各子命令输出量小，先尽读 stdout 再读 stderr 不会撑满管道缓冲；
    // 如未来出现大输出命令需改为并发流读取。
    static Map run(File workingDir, String exe, List args) {
        def builder = new ProcessBuilder(([exe] + args.collect { it.toString() })).directory(workingDir)
        builder.redirectErrorStream(false)
        def proc = builder.start()
        def out = proc.inputStream.text
        def err = proc.errorStream.text
        [exit: proc.waitFor(), out: out, err: err]
    }

    static Map require(Map result, String failMsg) {
        if (result.exit != 0) {
            throw new org.gradle.api.GradleException("${failMsg}：${result.err.trim()}")
        }
        return result
    }

    // 远端 v<base>.* 裸号附注标签的补丁号集合（只认 ^{} 解引用行，轻量标签天然不计入）。
    // 教训（e2e 第九轮实锤）：Groovy 的 ~/.../ slashy 字符串不做 ${} 插值，含插值的
    // 模式必须用普通字符串拼接构造，否则模式带着字面 "${base}" 永不匹配、枚举恒为空。
    static List<Integer> remoteTagPatches(File workingDir, String exe, String remoteName, String base) {
        def pattern = '^([0-9a-f]+)\\s+refs/tags/v' + java.util.regex.Pattern.quote(base) + '\\.(\\d+)\\^\\{\\}$'
        def re = java.util.regex.Pattern.compile(pattern)
        run(workingDir, exe, ['ls-remote', '--tags', remoteName, "refs/tags/v${base}*"]).out.readLines()
                .collect { re.matcher(it) }.findAll { it.matches() }.collect { it.group(2) as Integer }
    }
}
