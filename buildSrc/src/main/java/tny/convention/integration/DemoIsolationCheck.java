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

package tny.convention.integration;

import java.util.ArrayList;
import java.util.List;

/**
 * 受控隔离子进程目标的判定逻辑（tny.integration-test 后缀门控段的红判纯函数，
 * 迁移自 tny.integration-test.gradle 原第 122 至 135 行的三处 fail-fast；
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求的套用对象）。
 *
 * <p>受控目标＝本工程 integrationImplementation 直接工程依赖与声明 enableApp() 的应用蓝图
 * （tny.module-setting，就地核对无登记清单）的交集，按声明序；交集为空、目标缺 jar 任务或
 * 缺 runtimeClasspath 配置均在 integrationTest 执行前报红。该非空约束是
 * DemoPlayerObjectCodecableCodec 串染事故后的显式防线，交集即审计对象。
 */
public final class DemoIsolationCheck {

    /** 单个被声明工程的事实快照（接线类采集，判定类消费）。 */
    public record DeclaredProject(String path, boolean appBlueprintDeclared,
                                  boolean jarTaskPresent, boolean runtimeClasspathPresent) {
    }

    private DemoIsolationCheck() {
    }

    /** 受控隔离目标＝按声明序过滤应用蓝本声明（接线类的段拼装与判定共用同一交集，审计对象唯一化）。 */
    public static List<DeclaredProject> appTargets(List<DeclaredProject> declared) {
        return declared.stream().filter(DeclaredProject::appBlueprintDeclared).toList();
    }

    /** 输入按声明序的直接工程依赖事实，输出问题清单（空清单即放行）。 */
    public static List<String> violations(List<DeclaredProject> declared) {
        List<String> problems = new ArrayList<>();
        List<DeclaredProject> targets = appTargets(declared);
        if (targets.isEmpty()) {
            problems.add("tny.integration-test: 受控隔离目标交集为空——integrationImplementation 的直接工程依赖 "
                    + "(" + (declared.isEmpty() ? "无" : pathList(declared)) + ") 中无任何声明 enableApp() 的应用蓝本。"
                    + "两个可能成因：本工程未把被测 demo 加入 integrationImplementation；或所声明工程未声明 enableApp()。"
                    + "该非空约束是 DemoPlayerObjectCodecableCodec 串染事故后的显式防线，交集即审计对象。");
            return problems;
        }
        for (DeclaredProject target : targets) {
            if (!target.jarTaskPresent()) {
                problems.add("tny.integration-test: 目标工程 '" + target.path()
                        + "' 无 jar 任务，不能作为受控隔离目标（期望：应用 java 线的可发布工程）。");
            }
            if (!target.runtimeClasspathPresent()) {
                problems.add("tny.integration-test: 目标工程 '" + target.path()
                        + "' 无 runtimeClasspath 配置，不能作为受控隔离目标。");
            }
        }
        return problems;
    }

    /** 工程路径列表的报红呈现形态（与原 Groovy 列表 toString 的 "[a, b]" 形态逐字一致）。 */
    private static String pathList(List<DeclaredProject> declared) {
        List<String> paths = declared.stream().map(DeclaredProject::path).toList();
        return paths.toString();
    }
}
