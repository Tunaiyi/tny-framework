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

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;

import java.util.List;
import java.util.Optional;

/**
 * 子工程公共依赖管理装配约定插件（id {@code tny.dependency-conventions}，
 * consolidate-assembly-line 任务 4.1-4.3 由预编译脚本 tny.dependency-management.gradle
 * 吸收立口——同名行为整体迁移，脚本文件删除、注册行新增与根脚本两行并一在同一提交）。
 * 前身谱系：gradle/subprojects-baseline.gradle（adopt-gradle-official-dsl 插件化为
 * tny.dependency-management），本类是其第二代表体。
 *
 * <p>本插件负责每个子工程共用的事，入口内部应用次序逐字等于原根脚本行序
 * （io.spring.dependency-management 裸 id → idea → maven-publish → 配置体）：
 * <ul>
 * <li>应用第三方托管插件 io.spring.dependency-management——jar 由 buildSrc implementation
 *     供给（consolidate-binary-conventions 设计决策 D2 类型化路线，探针三实证裸 id 应用与
 *     扩展类加载器同一），原根脚本先行应用行随吸收退役；</li>
 * <li>应用 idea 与 maven-publish——maven-publish 连同 idea 由本入口无条件应用于全部子工程，
 *     它是插件线（tny-game-doc-gradle 正文的 publishing 块）与一切自身不声明 maven-publish 的
 *     子工程的唯一供给点（装配线册 design 接线表 MUST 条款；java-gradle-plugin 不传递供给
 *     经沙箱实证），MUST NOT 条件化或裁剪；</li>
 * <li>声明 BOM 导入与具名版本托管（{@link #applyManagedDeclarations}，声明写法经
 *     {@link CatalogNotations} 从版本目录生成）；</li>
 * <li>派生组号与版本（{@link #applyIdentity}，根工程类型化扩展单一事实源）。</li>
 * </ul>
 *
 * <p>工程依赖解析仓由 settings.gradle 的 dependencyResolutionManagement 单点声明
 * （centralize-resolution-config），本插件不接入。边界：java 线专属在 tny.java-conventions，
 * 插件线在 tny.plugin-conventions，发布元数据在 tny.publications；本插件不触碰任务注册。
 * 由根构建脚本 configure(subprojects) 一行引入。
 *
 * <p>形态沿革完整句：原脚本头部注释记载"预编译脚本体内 legacy apply 触发 ClassLoaderScope
 * 缺失（实测 Gradle 8.5）"，该约束仅限脚本作为应用发起方的方向；本类为二进制载体，
 * 以 PluginManager 按 id 引入第三方与按类引入核心插件均为规格"约定插件接线规则按载体定"
 * 明文允许的形态（8.14.5 复测口径见探针三与探针四记录）。第三方不在 buildSrc 编译类路径的
 * 旧前提已由 consolidate-binary-conventions 的依赖迁移消解。
 */
public class DependencyConventionsPlugin implements Plugin<Project> {

    /**
     * BOM 导入族清单（坐标 → 事实源版本键），条目与次序自 condense-managed-coordinates 起
     * 维持稳定，增删须经变更册裁决。覆盖次序契约（fix-dependency-version-governance 成文；
     * 托管合并语义经 2026-10-03 配置期实测）：依赖管理插件对多个 BOM 按"后导入者覆盖先导入者"
     * 合并，显式托管条目优先于全部导入；本账具名版本键声明的族，凡导入句位置早于
     * spring-boot-dependencies 即被 Boot 内嵌管理面静默覆盖（log4j 族曾以此形态失守：
     * 声明 2.22.1 实际生效 Boot 的 2.21.1）。因此凡需压过 Boot 管理面的显式大头导入一律排在
     * spring-boot-dependencies 之后；重排本次序会静默改变族级生效版本，由 tny.module-checker
     * 的配置期版本面对账守卫（ManagedVersionsCheck）拦截。
     * grpc 托管层（fix-dependency-version-governance D4）：jetcd-core 传递引入的 io.grpc 六件
     * 此前无任何具名管辖，等值收编为事实源键 grpcVersion，消除多 grpc 来源共存时无统一裁决的裸奔形态。
     */
    private static final List<String[]> BOM_IMPORTS = List.of(
            new String[]{"com.alibaba.cloud:spring-cloud-alibaba-dependencies", "alibabaCloudVersion"},
            new String[]{"org.springframework.boot:spring-boot-dependencies", "springBootVersion"},
            new String[]{"org.apache.logging.log4j:log4j-bom", "log4j2Version"},
            new String[]{"com.google.protobuf:protobuf-bom", "protobufVersion"},
            new String[]{"io.netty:netty-bom", "nettyVersion"},
            new String[]{"org.slf4j:slf4j-bom", "slf4jVersion"},
            new String[]{"com.fasterxml.jackson:jackson-bom", "jacksonVersion"},
            new String[]{"org.testcontainers:testcontainers-bom", "testcontainersVersion"},
            new String[]{"io.grpc:grpc-bom", "grpcVersion"});

    /**
     * 集中托管条目别名表（版本目录 libs 的具名条目，次序与集合自 condense-managed-coordinates
     * 起稳定，原脚本逐条注释随迁）：redisson；Apache Commons 系列——无落点成员已随
     * fix-dependency-version-governance 8.6 对账移除（全配置解析树零出现），commonsCompress 与
     * commonsMath3 两条为"托管而无模块直连消费但有真实传递落点"的保留登记（规格：有落点的条目
     * MUST 在声明处注释登记）：commonsCompress 钉扎 testcontainers 栈传递请求的 commons-compress
     * （上游请求 1.24.0，被压至本目声明 1.22；该压降与已知漏洞线的修正一并归版本升级册处置），
     * commonsMath3 钉扎 jmh-core 1.37 传递引入的 commons-math3:3.6.1；注解校验与未纳入 BOM 管理的
     * 常用库（checkerframework、errorprone、guava、javassist、xstream）；grpc 托管层的 vertx 构件
     * （io.vertx:vertx-grpc 不在 grpc-bom 管辖之内，以单条显式托管钉住 jetcd-grpc 当前传递版本，
     * 等值收编，fix-dependency-version-governance D4）。
     */
    private static final List<String> MANAGED_ALIASES = List.of(
            "redisson",
            "commonsIo", "commonsCodec", "commonsCollections4", "commonsLang3",
            "commonsMath3", "commonsCompress",
            "checkerframework", "errorprone", "guava", "javassist", "xstream",
            "vertxGrpc");

    @Override
    public void apply(Project project) {
        // 区块一：应用插件（次序逐字等于原根脚本行序，见类 javadoc；按 id 引入核心插件与
        // 按类引入等价，取 id 形态以与第三方引入行同谱）
        project.getPluginManager().apply("io.spring.dependency-management");
        project.getPluginManager().apply("idea");
        project.getPluginManager().apply("maven-publish");

        applyIdentity(project);
        // 区块二：托管声明段后置到工程评估收尾（consolidate-assembly-line 组 4.5 实测登记）：
        // VersionCatalogsExtension 于工程构建脚本评估时注册，根 configure(subprojects) 段的急切
        // apply 时刻拿不到——原预编译脚本形态可用 libs 访问器，是因为访问器走 buildSrc 侧对同一份
        // gradle/libs.versions.toml 的只读视图（buildSrc/settings.gradle 注释在册）的编译期注入；
        // 二进制形态无该编译期通道，改按 afterEvaluate 现查。时机等价的论证：托管面首次被消费在
        // tny.module-checker 的 gradle.projectsEvaluated 对账与各配置解析期，均晚于 afterEvaluate；
        // 导入次序与条目次序仍逐字保持（常量清单不变），区块内部相对次序 io.spring→idea→
        // maven-publish→配置体与原行序一致，仅整体后移一个评估周期收尾点。
        project.afterEvaluate(checked -> applyManagedDeclarations(checked));
    }

    /** 托管声明段：BOM 九条导入、具名条目十三条、生成 POM 定制关闭（迁移自原脚本 dependencyManagement 块）。 */
    private void applyManagedDeclarations(Project project) {
        DependencyManagementExtension dependencyManagement =
                project.getExtensions().getByType(DependencyManagementExtension.class);
        dependencyManagement.imports(imports -> BOM_IMPORTS.forEach(entry ->
                imports.mavenBom(entry[0] + ":" + versionOf(project, entry[1]))));
        Optional<VersionCatalog> catalog = project.getExtensions()
                .getByType(VersionCatalogsExtension.class).find("libs");
        if (catalog.isEmpty()) {
            throw new GradleException("集中托管声明失败：工程 '" + project.getPath()
                    + "' 的版本目录 libs 不在位（版本目录由 settings.gradle 单点声明）");
        }
        CatalogNotations notations = new CatalogNotations(catalog.get());
        dependencyManagement.dependencies(dependencies ->
                MANAGED_ALIASES.forEach(alias -> dependencies.dependency(notations.notation(alias))));
        dependencyManagement.generatedPomCustomization(customization -> customization.setEnabled(false));
    }

    /** 组号与版本派生段（迁移自原脚本尾部；34fc8b11 回落语义随迁见 ProjectsExtension 属性 javadoc）。 */
    private void applyIdentity(Project project) {
        ProjectsExtension projects = project.getRootProject().getExtensions().getByType(ProjectsExtension.class);
        project.setGroup(projects.getProjectGroup());
        // release 维护分支未注入 -PreleaseVersion 时 GitFlow.projectVersion 为 null（redesign-devline-integration-model D2），
        // 回落 Gradle 默认 unspecified 已在上游注入点完成（ProjectsPlugin），编译与测试不受影响，
        // 发布任务由门禁按形态与版本双重判定拒绝。此处判空即"tny.release-ops 未先于消费方应用"——
        // 与原脚本 getByType(GitFlow) 扩展不在位即抛的契约同段同形（expose-git-info-extension D3），
        // 报错时机同为子工程配置期。
        String derived = projects.getDerivedProjectVersion();
        if (derived == null) {
            throw new GradleException("版本派生失败：根工程 tny.projects 扩展的派生版本未注入，"
                    + "来由是根构建脚本未先应用 tny.release-ops（gitFlow 扩展不在位，tny.projects 应用时刻无法注入）；"
                    + "原契约见 expose-git-info-extension 设计决策 D3 与 tny.convention.ProjectsExtension javadoc");
        }
        project.setVersion(derived);
    }

    /** 读事实源版本键（providers 配置模型，gradle-build-style"任务注册与配置使用惰性形态"需求的读取纪律）。
     * 包内可见供单测直验缺键报红向；取到值的绿向由根侧配置期对账（ManagedVersionsCheck 既有测试）
     * 与零差异样件（consolidate-assembly-line 组 4.5）共同承担——ProjectBuilder 不加载 gradle.properties，
     * 绿向在本单测族不可构造，如实登记。 */
    static String versionOf(Project project, String versionKey) {
        String value = project.getProviders().gradleProperty(versionKey).getOrNull();
        if (value == null) {
            throw new GradleException("gradle.properties 缺少事实源版本键 '" + versionKey
                    + "'（BOM 导入句取用点，单一事实源见 gradle-build-style 需求三）");
        }
        return value;
    }
}
