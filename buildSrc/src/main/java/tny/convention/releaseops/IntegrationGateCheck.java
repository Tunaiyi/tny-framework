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

package tny.convention.releaseops;

import org.gradle.api.GradleException;
import tny.convention.GitFacts;

import java.util.List;

/**
 * 分支流转门禁判定（convert-orchestration-to-java 任务 4.2，自 tny.integrate.gradle 的校验闭包
 * 拆出；capability branch-integration-gates 语义契约不变，报错文案逐字承脚本原句）。
 * 红绿用例见 IntegrationGateCheckTest；num 闭包的 find 语义回归在 GitFactsTest 钉住。
 */
public final class IntegrationGateCheck {

    private IntegrationGateCheck() {
    }

    /** integrateMain 分支形态守卫。 */
    public static void requireDevBranch(String current) {
        if (!isDevSeriesBranch(current)) {
            throw new GradleException("integrateMain 必须在开发版本分支（dev/<主>.<次>.x）上执行，当前分支 '"
                    + current + "'");
        }
    }

    /** 集成顺序检查：任一低编号开发版本分支仍在途即拒绝（本地与远端并集为输入）。 */
    public static void assertIntegrationOrder(List<String> allDevLines, String current, int myKey) {
        List<String> lowerLines = allDevLines.stream()
                .filter(line -> isDevSeriesBranch(line) && !line.equals(current)
                        && GitFacts.seriesKey(line) >= 0 && GitFacts.seriesKey(line) < myKey)
                .toList();
        if (!lowerLines.isEmpty()) {
            throw new GradleException("开发版本分支集成顺序检查失败：低编号开发分支 "
                    + String.join("、", lowerLines) + " 仍存在且未完成集成到主干"
                    + "（本模型集成完成后即删除该分支）——先完成低编号系列集成，或按改号跳号程序注销该线");
        }
    }

    /** mergeUpward 分支形态守卫（源必须是 release 维护分支）。 */
    public static void requireMaintenanceSource(String current) {
        if (!isReleaseMaintenanceBranch(current)) {
            throw new GradleException("mergeUpward 必须在 release 维护分支上执行（源分支），当前分支 '" + current
                    + "'；开发版本分支的修复随其集成到主干进入 main，不走本任务");
        }
    }

    /** 合并结果完整性检查：目标合并结果中检索不到标记即缺失（检索命中求和为输入的判定）。
     * 保守语义（与接线侧原行为同判据）：命中行清单截断或该行非数字时按缺失处理——
     * 防"检索失败被当作无缺失"（JMH 空列表不报错教训同型，e2e 第十一轮叙述路径排除由接线保证）。 */
    public static List<String> missingMarkers(List<String> markers, List<List<String>> hitsPerMarkerLines) {
        java.util.List<String> missing = new java.util.ArrayList<>();
        for (int i = 0; i < markers.size(); i++) {
            if (i >= hitsPerMarkerLines.size() || GitFacts.sumGrepHits(hitsPerMarkerLines.get(i)) == 0) {
                missing.add(markers.get(i));
            }
        }
        return missing;
    }

    public static void assertNoMissingMarkers(String source, String target, List<String> missing) {
        if (!missing.isEmpty()) {
            throw new GradleException("合并结果完整性检查拦截 " + source + " -> " + target
                    + "：合并结果中检索不到标记 " + String.join("、", missing)
                    + "（修复内容在冲突解决中丢失）。目标已回退原头、保持未提交状态；重新解决冲突后重跑，"
                    + "或按规格例外通道登记该标记在目标结构中不存在并在新结构重做");
        }
    }

    /** retireGuard 分支形态守卫。 */
    public static void requireRetireCandidateBranch(String current) {
        if (!isReleaseMaintenanceBranch(current)) {
            throw new GradleException("retireGuard 必须在待退役的 release 维护分支上执行，当前分支 '" + current + "'");
        }
    }

    /** 退役检查一：未集成提交清单必须为空。 */
    public static void assertNoUncollected(String current, List<String> uncollected) {
        if (!uncollected.isEmpty()) {
            throw new GradleException("退役检查未通过：" + current + " 上有 " + uncollected.size()
                    + " 笔未集成提交——\n" + String.join("\n", uncollected)
                    + "\n确需保留的先执行 mergeUpward 完成向上合并；判定废弃的在 docs/release-process.md"
                    + " 线谱系登记表处置列逐笔写明理由后重跑本任务。");
        }
    }

    /** 退役检查二：线谱系登记表须含该线行（登记文本行为输入）。 */
    public static void assertLineageDispositioned(String current, List<String> lineageLines) {
        boolean dispositioned = lineageLines.stream().anyMatch(line -> line.contains(current));
        if (!dispositioned) {
            throw new GradleException("退役检查未通过：线谱系登记表中没有 '" + current
                    + "' 的行——先按退役条款把该线状态改为已退役并登记处置，再执行删除");
        }
    }

    private static boolean isDevSeriesBranch(String name) {
        return name != null && name.matches("dev/\\d+\\.\\d+\\.x");
    }

    private static boolean isReleaseMaintenanceBranch(String name) {
        return name != null && name.matches("release/\\d+\\.\\d+\\.x");
    }
}
