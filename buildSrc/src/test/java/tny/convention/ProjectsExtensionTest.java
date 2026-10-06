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

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ProjectsExtension 命名约定判定的单元测试（pilot-binary-build-conventions 任务 4.2，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求的首个套用）：
 * 以假工程集合覆盖三个派生集合与三类谓词的命中方向与不命中方向，其中 -tester 成员
 * 留在 javaProjects 是自原 ext 块移植的既有语义（tester 属 java 装配线成员，
 * fix-dependency-version-governance 设计决策 D8 注记）。
 */
class ProjectsExtensionTest {

    private ProjectsExtension extension;

    private Project gameCore;

    private Project gameBom;

    private Project docGradle;

    private Project integrationTestModule;

    private Project testerModule;

    private Project benchmarkModule;

    @BeforeEach
    void setUp() {
        Project root = ProjectBuilder.builder().withName("tny-framework").build();
        gameCore = ProjectBuilder.builder().withName("tny-game-core").withParent(root).build();
        gameBom = ProjectBuilder.builder().withName("tny-game-bom").withParent(root).build();
        docGradle = ProjectBuilder.builder().withName("tny-game-doc-gradle").withParent(root).build();
        integrationTestModule = ProjectBuilder.builder().withName("tny-game-net-integration-test").withParent(root).build();
        testerModule = ProjectBuilder.builder().withName("tny-game-tester").withParent(root).build();
        benchmarkModule = ProjectBuilder.builder().withName("tny-benchmark").withParent(root).build();
        extension = new ProjectsExtension(root);
    }

    @Test
    void moduleProjectsContainsAllGameModulesOnly() {
        Set<String> names = namesOf(extension.moduleProjects());
        assertEquals(Set.of("tny-game-core", "tny-game-bom", "tny-game-doc-gradle",
                "tny-game-net-integration-test", "tny-game-tester"), names);
        assertFalse(names.contains("tny-benchmark"), "非 tny-game 前缀的工程不得进入构件模块集");
    }

    @Test
    void javaProjectsExcludesThreeCategoriesButKeepsTester() {
        Set<String> names = namesOf(extension.javaProjects());
        assertEquals(Set.of("tny-game-core", "tny-game-tester"), names);
        assertFalse(names.contains("tny-game-bom"), "-bom 无编译构件，必须排除");
        assertFalse(names.contains("tny-game-doc-gradle"), "-gradle 走插件线，必须排除");
        assertFalse(names.contains("tny-game-net-integration-test"), "-integration-test 仅测试代码，必须排除");
    }

    @Test
    void gradleProjectsContainsGradleSuffixMembersOnly() {
        assertEquals(Set.of("tny-game-doc-gradle"), namesOf(extension.gradleProjects()));
    }

    @Test
    void predicatesAgreeWithDerivedSets() {
        assertTrue(extension.isGameModule(gameCore));
        assertTrue(extension.isBom(gameBom));
        assertTrue(extension.isGradlePlugin(docGradle));
        assertTrue(extension.isIntegrationTest(integrationTestModule));
        assertFalse(extension.isBom(gameCore));
        assertFalse(extension.isGradlePlugin(gameCore));
        assertFalse(extension.isIntegrationTest(testerModule), "-tester 后缀不落入任何排除类别");
        assertFalse(extension.isGameModule(benchmarkModule));
    }

    @Test
    void groupFactsArePlainProperties() {
        extension.setProjectGroup("com.example.a");
        extension.setPluginLegacyGroup("com.example.b");
        assertEquals("com.example.a", extension.getProjectGroup());
        assertEquals("com.example.b", extension.getPluginLegacyGroup());
    }

    private static Set<String> namesOf(Set<Project> projects) {
        return projects.stream().map(Project::getName).collect(Collectors.toSet());
    }
}
