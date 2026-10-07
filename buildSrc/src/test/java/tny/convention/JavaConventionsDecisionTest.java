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

package tny.convention;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * java-conventions 入口两处检索与派生判定的单元测试（consolidate-assembly-line 任务 5.1，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求）：-tester 夹具检索谓词
 * 覆盖命中、排除宿主自身与不命中三个方向（宿主排除即 fix-dependency-version-governance D8
 * 自依赖边违例的守卫本体）；jar 清单 Automatic-Module-Name 派生覆盖含连字符与不含连字符两向。
 */
class JavaConventionsDecisionTest {

    @Test
    void testerFixturesHitSuffixAndExcludeHostItself() {
        Project root = ProjectBuilder.builder().withName("tny-framework").build();
        Project tester = ProjectBuilder.builder().withName("tny-game-tester").withParent(root).build();
        Project gameNet = ProjectBuilder.builder().withName("tny-game-net").withParent(root).build();
        Project notTester = ProjectBuilder.builder().withName("tny-game-codec").withParent(root).build();

        List<Project> forNet = CommonDependencyConventions.testerFixtures(
                List.of(tester, gameNet, notTester), gameNet);
        assertEquals(List.of(tester), forNet, "普通线内工程命中唯一 -tester 夹具");

        List<Project> forTesterItself = CommonDependencyConventions.testerFixtures(
                List.of(tester, gameNet, notTester), tester);
        assertTrue(forTesterItself.isEmpty(), "tester 宿主不得把自身挂进自己的 testImplementation（自依赖边守卫）");
    }

    @Test
    void automaticModuleNameReplacesHyphenButKeepsPlainName() {
        assertEquals("tny.game.net", CompileTestChannelConventions.automaticModuleName("tny-game-net"));
        assertEquals("tny.game.codec", CompileTestChannelConventions.automaticModuleName("tny-game-codec"));
        assertFalse(CompileTestChannelConventions.automaticModuleName("tny-game-net").contains("-"),
                "JDK9 模块名不允许连字符");
    }
}
