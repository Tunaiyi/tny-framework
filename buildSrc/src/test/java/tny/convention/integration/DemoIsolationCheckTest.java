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

package tny.convention.integration;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DemoIsolationCheck 的单元测试（consolidate-assembly-line 任务 7.1，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求）：通过用例覆盖
 * "蓝本声明且构件齐备即放行"，违例用例覆盖交集为空（含声明列表呈现与"无"形态）、
 * 缺 jar 任务、缺 runtimeClasspath 配置三处 fail-fast 的报红文案与判红对象指名。
 * 接线类的生命周期语义（doFirst 时机、classpath 拼装）由组 7.3 样件比对承担，与判定本体分工。
 */
class DemoIsolationCheckTest {

    @Test
    void blueprintTargetsWithJarAndRuntimeClasspathPass() {
        List<DemoIsolationCheck.DeclaredProject> declared = List.of(
                new DemoIsolationCheck.DeclaredProject(":tny-game-net-demo", true, true, true),
                new DemoIsolationCheck.DeclaredProject(":tny-game-basics", false, true, true));
        assertTrue(DemoIsolationCheck.violations(declared).isEmpty(), "合法交集放行");
        assertEquals(List.of(":tny-game-net-demo"),
                DemoIsolationCheck.appTargets(declared).stream()
                        .map(DemoIsolationCheck.DeclaredProject::path).toList(),
                "交集按声明序仅含应用蓝本");
    }

    @Test
    void emptyIntersectionFailsNamingDeclaredPaths() {
        List<DemoIsolationCheck.DeclaredProject> declared = List.of(
                new DemoIsolationCheck.DeclaredProject(":tny-game-basics", false, true, true));
        List<String> problems = DemoIsolationCheck.violations(declared);
        assertEquals(1, problems.size());
        assertTrue(problems.get(0).contains("受控隔离目标交集为空"), "须指名违例类型");
        assertTrue(problems.get(0).contains("[:tny-game-basics]"),
                "须以工程路径列表形态呈现直接工程依赖（带冒号路径，与原 Groovy declared*.path 的 toString 逐字一致）");
        assertTrue(DemoIsolationCheck.violations(List.of()).get(0).contains("(无)"),
                "空依赖清单时以『无』呈现");
    }

    @Test
    void targetMissingJarOrRuntimeClasspathFailsPerTarget() {
        List<DemoIsolationCheck.DeclaredProject> declared = List.of(
                new DemoIsolationCheck.DeclaredProject(":tny-game-net-demo", true, false, true),
                new DemoIsolationCheck.DeclaredProject(":tny-game-actor", true, true, false));
        List<String> problems = DemoIsolationCheck.violations(declared);
        assertEquals(2, problems.size(), "两目标各报一条");
        assertTrue(problems.get(0).contains("':tny-game-net-demo' 无 jar 任务"), "须指名缺 jar 的工程路径");
        assertTrue(problems.get(1).contains("':tny-game-actor' 无 runtimeClasspath 配置"),
                "须指名缺运行时类路径的工程路径");
    }
}
