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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import tny.convention.releaseops.IntegrationChannelTasks;
import tny.convention.releaseops.ReleaseChannelTasks;

/**
 * 发布工程编排线总入口（id {@code tny.release-ops}，convert-orchestration-to-java 任务 6 立口——
 * 吸收 tny.git、tny.release、tny.integrate 三枚预编译脚本，三 id 随吸收注销，脚本删除、注册行新增
 * 与根脚本三行并一在同一提交；内部次序逐字等于原根脚本三行行序）。
 *
 * <p>git 派生与版本形态段（前身 gradle/git.gradle；expose-git-info-extension：导出面由根工程 ext
 * 派生字典改为根工程类型化扩展；gitinfo-self-init-and-getters：扩展名 gitFlow、派生值初始化收进
 * 类构造器，本入口只负责准备通道，经带门面的构造调用一步完成扩展的创建与注册，唯一写入方仍是
 * 这一处构造调用；retire-grgit-channel：通道从 grgit 句柄换为 GitCli 门面，同时删除
 * linked-worktree 构建禁令）。
 * 布局边界（retire-grgit-channel D4 解除禁令）：初版经 grgit/JGit 读 .git，linked worktree 的
 * commondir 重定向不被跟随（5.13 与 6.10.1 实测证伪，证据见归档 upgrade-grgit-for-worktrees），
 * 曾以 gitdir 位置判据 fail-fast 禁建。通道全 CLI 化后该缺陷库退场，禁令存在理由消失——
 * gradle 构建对完整克隆、linked worktree、子模块布局一律开放；发布快速通道仍建议独立完整
 * 克隆执行（与并行会话争用 HEAD 与 index 属运营纪律，不再由构建拦截强制）。
 *
 * <p>目录页（内部装配次序即原三行行序，每项行为指名所在类）：
 * <ol>
 * <li>本类通道段——读祖父登记表（redesign-devline-integration-model D7，"分支名|身份|依据"
 *     三列、注释与空行丢弃，解析在 {@link GitFacts#parseLegacyRegistry}）并经带门面的构造调用
 *     创建根扩展 gitFlow（{@link GitFlow}，实现 {@link GitVersionSource} 供 tny.projects 注入）；</li>
 * <li>{@link ReleaseChannelTasks}——发布快速通道 releaseCut/releaseTag（语义契约 capability
 *     release-versioning 与 docs/release-process.md；新形态切常驻维护分支与附注标签双语义，
 *     祖父轨旧一次性流程保留至 5.7 系列退役；任务 releaseMergeBack 已由 redesign 删除、其语义
 *     由 mergeUpward 取代）；</li>
 * <li>{@link IntegrationChannelTasks}——分支流转门禁 integrateMain/mergeUpward/retireGuard
 *     （capability branch-integration-gates，redesign 设计 D3/D4/D5）。</li>
 * </ol>
 *
 * <p>边界：不做任务注册之外的装配；发布断言门禁在 tny.publish.gate（发布族册处置）；全仓 git
 * 读写统一经 GitCli 门面单通道（GitFlow 的派生与查询同经此门面）。-PdryRun 预览通道覆盖四个
 * 变更型任务（不可逆编排纪律），预览只读派生值与登记表、不派生写子进程。
 */
public class ReleaseOpsPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        GitCli git = GitCli.forProject(project);
        // 祖父登记表（redesign-devline-integration-model D7）：读入并交 GitFlow 持有。
        File legacyRegistryFile = new File(project.getRootDir(), "gradle/branch-legacy-registry.txt");
        Map<String, String> legacyRegistry = Map.of();
        if (legacyRegistryFile.exists()) {
            try {
                legacyRegistry = GitFacts.parseLegacyRegistry(
                        Files.readAllLines(legacyRegistryFile.toPath(), StandardCharsets.UTF_8));
            } catch (IOException failure) {
                throw new UncheckedIOException(failure);
            }
        }
        Object releaseVersion = project.findProperty("releaseVersion");
        GitFlow gitFlow = project.getExtensions().create("gitFlow", GitFlow.class, git, legacyRegistry,
                releaseVersion == null ? null : releaseVersion.toString());
        ReleaseChannelTasks.register(project, gitFlow, git);
        IntegrationChannelTasks.register(project, gitFlow, git);
    }
}
