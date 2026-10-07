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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ModuleSetting 的"零发布合同声明期自检"单元测试（consolidate-binary-conventions 任务 4.1）。
 *
 * <p>背景（实测教训完整句）：根 ext 五键在 pilot-binary-build-conventions 决策 D4 中收敛为
 * 类型化 tny.projects 扩展后，Groovy 形态的 enableUnpublished 自检仍读 rootProject.ext 的
 * 已退役键并以 has() 三目兜底空集合，"发布线成员声明不发布即报红"守卫自此静默失效
 * （归档后审计 CRITICAL-1，登记于本册 apply-notes 归档账目更正第一条）。本测试先于修复
 * 编写：违例用例在失效代码上必然失败，用以证明用例打中的正是缺陷本体；修复后四用例全绿。
 *
 * <p>夹具形态沿 ProjectsExtensionTest 先例：直建假工程集合，projects 扩展经容器 add 注册
 * （被测守卫只依赖扩展在位与派生集合成员关系，不依赖 tny.projects 插件装配行为）。
 */
class ModuleSettingTest {

    private Project rootWithProjectsExtension(boolean withExtension) {
        Project root = ProjectBuilder.builder().withName("tny-framework").build();
        if (withExtension) {
            root.getExtensions().add("projects", new ProjectsExtension(root));
        }
        return root;
    }

    private ModuleSetting settingOn(Project root, String childName) {
        Project child = ProjectBuilder.builder().withName(childName).withParent(root).build();
        return new ModuleSetting(child);
    }

    @Test
    void javaLineMemberDeclaresUnpublished_failsFastAtDeclaration() {
        Project root = rootWithProjectsExtension(true);
        ModuleSetting setting = settingOn(root, "tny-game-core");
        GradleException ex = assertThrows(GradleException.class, setting::enableUnpublished);
        assertTrue(ex.getMessage().contains(":tny-game-core"), "报红文案须指名违例工程路径");
        assertTrue(ex.getMessage().contains("javaProjects"), "报红文案须指名判定所依据的发布线集合");
    }

    @Test
    void integrationTestSuffixMemberDeclaresUnpublished_accepted() {
        Project root = rootWithProjectsExtension(true);
        ModuleSetting setting = settingOn(root, "tny-game-net-integration-test");
        assertDoesNotThrow(setting::enableUnpublished);
        assertTrue(setting.has(ModuleSetting.Mode.UNPUBLISHED));
    }

    @Test
    void nonGamePrefixFacilityDeclaresUnpublished_accepted() {
        Project root = rootWithProjectsExtension(true);
        ModuleSetting setting = settingOn(root, "tny-benchmark");
        assertDoesNotThrow(setting::enableUnpublished);
    }

    @Test
    void missingProjectsExtension_failsLoudWithoutFallback() {
        Project root = rootWithProjectsExtension(false);
        ModuleSetting setting = settingOn(root, "tny-game-core");
        // 兜底吞判是本缺陷的成因形态：扩展不在位必须报错，MUST NOT 静默放行声明
        RuntimeException ex = assertThrows(RuntimeException.class, setting::enableUnpublished);
        assertTrue(ex.getMessage().contains("ProjectsExtension") || ex.getMessage().contains("projects"),
                "报错须指明缺失的根扩展，供读者定位应用顺序问题");
    }
}
