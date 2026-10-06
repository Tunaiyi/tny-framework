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
import org.gradle.api.artifacts.VersionCatalog;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CatalogNotations 的单元测试（consolidate-assembly-line 任务 4.2，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求）：
 * 通过用例直验"组:名:版本"拼接纯函数，违例用例以动态代理构造"别名不在目录"的
 * 最小 VersionCatalog 替身，断言报红指名缺失别名与变更册裁决理由。
 */
class CatalogNotationsTest {

    private static VersionCatalog emptyCatalog() {
        return (VersionCatalog) Proxy.newProxyInstance(
                CatalogNotations.class.getClassLoader(),
                new Class<?>[]{VersionCatalog.class},
                (proxy, method, args) -> {
                    if ("findLibrary".equals(method.getName())) {
                        return Optional.empty();
                    }
                    if ("getName".equals(method.getName())) {
                        return "libs";
                    }
                    return null;
                });
    }

    @Test
    void joinNotationComposesGroupColonNameColonVersion() {
        assertEquals("com.google.guava:guava:33.0.0-jre",
                CatalogNotations.joinNotation("com.google.guava", "guava", "33.0.0-jre"));
    }

    @Test
    void missingCatalogAliasFailsLoudNamingTheAlias() {
        CatalogNotations notations = new CatalogNotations(emptyCatalog());
        GradleException ex = assertThrows(GradleException.class, () -> notations.notation("vertxGrpc"));
        assertTrue(ex.getMessage().contains("vertxGrpc"), "报红须指名缺失的别名");
        assertTrue(ex.getMessage().contains("版本目录"), "报红须指明缺失来源是版本目录");
    }
}
