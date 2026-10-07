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

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Comparator;

/**
 * BOM 约束面条目约定插件（adopt-gradle-official-dsl 插件化，前身为 gradle/bom-platform.gradle）。
 * 本插件从根工程 tny.projects 类型化扩展的构件模块集派生 java-platform constraints 条目并按名排序，
 * 保证 BOM 与装配线同步。边界：BOM 发布接线与描述在 tny-game-bom 模块构建文件；
 * 排除面规格出处 add-integration-testing design D3。
 *
 * <p>载体沿革：同名 id 的二进制实现（consolidate-binary-conventions 任务 6.2，注册行与脚本删除
 * 同提交）。派生循环属程序性生成，依 gradle-build-style"工程配置写声明式语句"需求必须留在
 * 约定插件载体内，不下放模块脚本；成员集合与排除判定经根工程类型化扩展按类型拉取
 * （pilot-binary-build-conventions D4 形态）。
 */
public class BomPlatformPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ProjectsExtension projectsExt = project.getParent().getExtensions().getByType(ProjectsExtension.class);
        project.getDependencies().constraints(constraints ->
                projectsExt.moduleProjects().stream()
                        // 集成测试专用模块不进入 BOM 约束面（add-integration-testing design D3）；
                        // add("api", ...) 即脚本 constraints{ api it } 动态分发解析到的公开 API 形态
                        .filter(module -> !projectsExt.isIntegrationTest(module))
                        .sorted(Comparator.comparing(Project::getName))
                        .forEach(module -> constraints.add("api", module)));
    }
}
