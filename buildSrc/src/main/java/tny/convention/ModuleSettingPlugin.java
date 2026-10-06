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

/**
 * 模块横切角色统一插件（consolidate-module-modes-plugin）：取代 tny.demo-app 与 tny.unpublished
 * 两个标记插件与两张根工程登记清单。应用本插件的工程通过 {@code moduleSetting} 扩展方法声明角色
 * （enableApp / enableUnpublished），实现类 {@link ModuleSetting}（就地核对模型见其类头）。
 *
 * <p>边界：线成员归属（发布线/BOM/插件线等）由命名后缀判定（settings.gradle 约定注释），不在此处。
 *
 * <p>载体沿革：本类为同名 id 的二进制实现（consolidate-binary-conventions 任务 5.1，用户裁决
 * "约定插件全部 Java 实现"），前身为预编译脚本 tny.module-setting.gradle（一行
 * {@code extensions.create('moduleSetting', ModuleSetting, project)}，注册行与脚本删除同提交，
 * 同一插件 id 两形态不并存）。
 */
public class ModuleSettingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("moduleSetting", ModuleSetting.class, project);
    }
}
