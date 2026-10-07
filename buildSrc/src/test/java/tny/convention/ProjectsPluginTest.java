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

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * ProjectsPlugin 派生版本注入的单元测试（consolidate-assembly-line 任务 4.2，设计决策 D4
 * 变体乙的注入点语义）：替身 gitFlow 以 {@link GroovyObject} 属性协议返回派生版本，
 * 通过用例覆盖"注入值在位逐字透传"与"projectVersion 为 null 时回落 DEFAULT_VERSION"两分支
 * （redesign-devline-integration-model 设计决策 D2 语义），违例形态用例钉住"gitFlow 缺席时
 * 留空不抛"的契约——消费期报红由 tny.dependency-conventions 的版本派生与组 4.4 端到端破坏探针承担。
 */
class ProjectsPluginTest {

    /** Groovy 类 GitFlow 的最小属性协议替身：仅 projectVersion 一个属性。 */
    private static final class FakeGitFlow implements GroovyObject {
        private final Object projectVersion;

        FakeGitFlow(Object projectVersion) {
            this.projectVersion = projectVersion;
        }

        @Override
        public Object getProperty(String property) {
            if ("projectVersion".equals(property)) {
                return projectVersion;
            }
            throw new IllegalArgumentException("未知属性: " + property);
        }

        @Override
        public void setProperty(String property, Object newValue) {
            throw new UnsupportedOperationException("替身只读");
        }

        @Override
        public Object invokeMethod(String name, Object args) {
            throw new UnsupportedOperationException("替身无方法调用");
        }

        @Override
        public MetaClass getMetaClass() {
            return null;
        }

        @Override
        public void setMetaClass(MetaClass metaClass) {
            // 替身不依赖元类协议
        }
    }

    private ProjectsExtension applyWithGitFlow(Object projectVersion) {
        Project root = ProjectBuilder.builder().withName("tny-framework").build();
        if (projectVersion != NO_GITFLOW) {
            root.getExtensions().add("gitFlow", new FakeGitFlow(projectVersion == ABSENT ? null : projectVersion));
        }
        new ProjectsPlugin().apply(root);
        return root.getExtensions().getByType(ProjectsExtension.class);
    }

    private static final Object NO_GITFLOW = new Object();

    private static final Object ABSENT = new Object();

    @Test
    void injectedVersionPassesThroughVerbatim() {
        ProjectsExtension extension = applyWithGitFlow("5.7.9");
        assertEquals("5.7.9", extension.getDerivedProjectVersion());
        assertEquals("com.tnydev.game", extension.getProjectGroup());
    }

    @Test
    void nullProjectVersionFallsBackToDefaultVersion() {
        ProjectsExtension extension = applyWithGitFlow(ABSENT);
        assertEquals(Project.DEFAULT_VERSION, extension.getDerivedProjectVersion());
    }

    @Test
    void missingGitFlowLeavesDerivedUnsetForConsumerFailLoud() {
        ProjectsExtension extension = applyWithGitFlow(NO_GITFLOW);
        assertNull(extension.getDerivedProjectVersion(),
                "gitFlow 缺席时注入点留空不抛——tny.projects 独立可用的既有形态保持");
    }
}
