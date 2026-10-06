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
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.provider.Provider;

import java.util.Optional;

/**
 * 版本目录条目到依赖声明写法字符串的转换支撑类（consolidate-assembly-line 任务 4.1，
 * 前身是 tny.dependency-management.gradle 脚本尾的 static dependencyNotation(provider)）。
 *
 * <p>职责边界：只做"目录别名 → 组:名:版本"单段转换，不复制条目表——按别名经
 * {@link VersionCatalog#findLibrary(String)} 现查（gradle-build-style"依赖版本与坐标由单一
 * 事实源管理"允许的二进制侧取用形态），目录仍是唯一具名引用表。
 *
 * <p>声明写法按公开属性逐段拼接，不依赖对象的 toString（其输出格式非官方承诺）——
 * 该实测教训自脚本原文逐段随迁。集中声明接口只接受"组:名:版本"形式的声明写法字符串
 * （传依赖对象会被拒绝，2026-10-03 实测）。
 */
final class CatalogNotations {

    private final VersionCatalog catalog;

    CatalogNotations(VersionCatalog catalog) {
        this.catalog = catalog;
    }

    /** 把目录别名指向的条目转成依赖声明写法字符串；别名不在目录内即配置期报红指名缺失项。 */
    String notation(String alias) {
        Optional<Provider<MinimalExternalModuleDependency>> found = catalog.findLibrary(alias);
        if (found.isEmpty()) {
            throw new GradleException("集中托管声明失败：版本目录 libs 中不存在别名 '" + alias
                    + "'（托管条目增删须经变更册裁决后改动，先例见 fix-dependency-version-governance）");
        }
        MinimalExternalModuleDependency dependency = found.get().get();
        return joinNotation(dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }

    /** 逐段拼接"组:名:版本"声明写法（纯函数，供单测直验）。 */
    static String joinNotation(String group, String name, String version) {
        return group + ":" + name + ":" + version;
    }
}
