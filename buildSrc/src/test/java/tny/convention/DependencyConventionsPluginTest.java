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

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DependencyConventionsPlugin 版本键读取判定的单元测试（consolidate-assembly-line 任务 4.2）：
 * 违例用例覆盖"事实源版本键缺位即配置期报红并指名键名"；取到值的通过用例由组 4.5 的根侧配置期
 * 对账与零差异样件承担（ProjectBuilder 不加载 gradle.properties，绿向在本单测族不可构造，
 * 该边界已随 versionOf 的注释如实登记）。派生版本缺位的消费期报红由组 4.4 端到端破坏探针承担。
 */
class DependencyConventionsPluginTest {

    @Test
    void missingFactSourceVersionKeyFailsLoudNamingTheKey() {
        Project project = ProjectBuilder.builder().withName("tny-game-core").build();
        GradleException ex = assertThrows(GradleException.class,
                () -> DependencyConventionsPlugin.versionOf(project, "log4j2Version"));
        assertTrue(ex.getMessage().contains("log4j2Version"), "报红须指名缺失的事实源版本键");
        assertTrue(ex.getMessage().contains("gradle.properties"), "报红须指明缺失来源");
    }
}
