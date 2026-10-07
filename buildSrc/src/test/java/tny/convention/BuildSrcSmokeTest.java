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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * buildSrc 测试基建的打通锚：证明 ProjectBuilder 与 JUnit 平台在 buildSrc 类路径上
 * 可以构造工程并访问扩展容器（pilot-binary-build-conventions D5）。后续检查类单测
 * （tny.module-checker 三个检查类）沿用同一形态；本类不测任何业务逻辑，基建被移除时随删。
 */
class BuildSrcSmokeTest {

    @Test
    void projectBuilderCanCreateProjectWithExtensionsContainer() {
        Project project = ProjectBuilder.builder().build();
        assertNotNull(project.getExtensions().getExtensionsSchema());
    }
}
