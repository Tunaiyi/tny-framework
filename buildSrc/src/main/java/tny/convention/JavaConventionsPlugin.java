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
 * java 发布线总入口（id {@code tny.java-conventions}，consolidate-assembly-line 任务 5.1-5.3
 * 由 tny.java-module.gradle 吸收立口——同名行为整体迁移，脚本文件删除、注册行新增、
 * compile-baseline 注册 id 注销与根脚本相应行改写在同一提交）。
 *
 * <p>成员与引入方式：根构建脚本 configure(javaProjects) 段的本线成员集合经 tny.projects
 * 类型化扩展按命名约定派生（adopt-gradle-official-dsl 插件化，前身为 gradle/java-module.gradle）。
 *
 * <p>目录页（gradle-build-style"扫读测试与长度界线"需求的跨类装配单元判据——每项配置行为
 * 指名其所在类，读者从本页出发两跳内可定位任一行为的实现处）。内部装配次序逐字等于
 * 被收编行原行序（consolidate-assembly-line 设计决策 D2；javaProjects 段原第 61 行
 * tny.compile-baseline 与第 62 行 tny.java-module 并为本入口一行，原脚本内部第 13 至 204 行
 * 的语句次序由下列五段的调用次序承载）：
 * <ol>
 * <li>{@link CompileBaselinePlugin}（按类应用，注册 id 已注销）——changing 与动态版本零缓存、
 *     编译与 javadoc 编码钉定（原根第 61 行语义；java 线与插件线共享，插件线侧由
 *     tny.plugin-conventions 同样按类复用，两入口目录页各自声明，非配置块逐字复制）；</li>
 * <li>{@link JavaPluginBaselineConventions}——idea/java/java-library/maven-publish 应用、
 *     source 与 target 钉定、sourcesJar 先手写注册再由 withSourcesJar 复用的次序组合
 *     （sweep-gradle-build-style D5 的 51 项差异定罪判据）、toolchain 21、createProject；</li>
 * <li>{@link CompileTestChannelConventions}——logging 绑定排他（configureEach 惰性形态保持，
 *     后建的 integration 系配置同样吃到排除）、compileJava 依赖编排、javadoc doclint 豁免、
 *     编译参数、jar 排除与清单四属性、单元通道集成标签排除；</li>
 * <li>{@link DependencySourcesTaskConventions}——downloadDependencySources 任务；</li>
 * <li>{@link IdeAndPublicationConventions}——IDE 输出目录与 mavenJava 构件接线（创建保持即时，
 *     根装配段其后的 tny.publications 在根配置窗口内按名引用该发布物）；</li>
 * <li>{@link CommonDependencyConventions}——公共依赖与 -tester 夹具挂接，唯一后置到评估收尾的段。</li>
 * </ol>
 *
 * <p>公共依赖段的时机例外（本入口唯一的非即时段）：版本目录扩展于工程构建脚本评估时注册，
 * 根装配段急切应用时刻子工程拿不到它（consolidate-assembly-line 组 4.5 实测，同 tny.dependency-conventions
 * 的托管声明段）——{@link CommonDependencyConventions#apply} 后置到工程评估收尾执行，依赖在解析期
 * 才被消费、时机等价；其余四段保持评估期即时装配，与原脚本行为面一致。
 *
 * <p>边界：BOM/仓库/组号版本派生在 tny.dependency-conventions；两级验证通道的工程侧任务面在
 * tny.integration-test；发布元数据与签名在 tny.publications；插件模块线在 tny.plugin-conventions；
 * 本入口不触碰发布仓路由。
 */
public class JavaConventionsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        new CompileBaselinePlugin().apply(project);
        JavaPluginBaselineConventions.apply(project);
        CompileTestChannelConventions.apply(project);
        DependencySourcesTaskConventions.apply(project);
        // mavenJava 创建保持即时：根装配段其后的 tny.publications 应用在根配置窗口内即按名引用
        // publishing.publications.mavenJava（sign 挂接为求值期对象解析），后移会引用不存在的发布物。
        IdeAndPublicationConventions.apply(project);
        // 唯一后置段：公共依赖（版本目录通道，见类 javadoc 时机例外）
        project.afterEvaluate(CommonDependencyConventions::apply);
    }
}
