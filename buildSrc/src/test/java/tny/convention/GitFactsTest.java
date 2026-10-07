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

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GitFacts 解析与派生判定的单元测试（convert-orchestration-to-java 任务 4.1，
 * gradle-build-style"二进制实现的检查逻辑必须携带单元测试"需求）：每函数覆盖命中与不命中两向；
 * seriesKey 的 find 语义回归钉住"dev/5.7.x 带前缀名必得 5007"（误用 matches 即静默 -1 的
 * e2e 第五轮事故判据随迁）。
 */
class GitFactsTest {

    @Test
    void projectVersionDerivationMatrix() {
        assertEquals("5.7.x-SNAPSHOT", GitFacts.parseProjectVersion("dev/5.7.x", null));
        assertEquals("5.7.8", GitFacts.parseProjectVersion("release/5.7.x", "5.7.8"));
        assertNull(GitFacts.parseProjectVersion("release/5.7.x", "5.8.0"), "前两段须与分支编号一致");
        assertNull(GitFacts.parseProjectVersion("release/5.7.x", "5.7"), "注入值须裸三段号");
        assertNull(GitFacts.parseProjectVersion("release/5.7.x", null), "维护分支缺注入值派生 null");
        assertEquals("5.7.x-SNAPSHOT", GitFacts.parseProjectVersion("5.7.x", null));
        assertEquals("5.7.9", GitFacts.parseProjectVersion("5.7.9.release", null));
        assertEquals("5.7.x", GitFacts.parseBranchVersion("dev/5.7.x"));
        assertEquals("main", GitFacts.parseBranchVersion("main"));
    }

    @Test
    void seriesKeyUsesFindSemanticsNotMatches() {
        assertEquals(5007, GitFacts.seriesKey("dev/5.7.x"), "带前缀名 find 必命中（事故回归钉）");
        assertEquals(5007, GitFacts.seriesKey("release/5.7.x"));
        assertEquals(5010, GitFacts.seriesKey("dev/5.10.x"));
        assertEquals(-1, GitFacts.seriesKey("main"));
        assertEquals(-1, GitFacts.seriesKey("dev/5.7"));
    }

    @Test
    void describeTagStrictSecondPassFilter() {
        assertEquals("v5.7.8", GitFacts.describeTagOrNull(" v5.7.8\n"));
        assertNull(GitFacts.describeTagOrNull("v3.4.1-RELEASE"), "旧代 -RELEASE 形态拒绝");
        assertNull(GitFacts.describeTagOrNull("v9.9.9-rc1"), "rc 形态拒绝");
        assertNull(GitFacts.describeTagOrNull("v5.7.8-3-gdeadbee"), "describe 距离后缀拒绝");
    }

    @Test
    void porcelainTrackedPathMapping() {
        assertEquals(List.of("a.java", "old -> new"),
                GitFacts.porcelainTrackedPaths(List.of("M  a.java", "?? untracked", "R  old -> new", "X")));
    }

    @Test
    void lsRemoteGuardedParseDropsShortLines() {
        Map<String, String> refs = GitFacts.parseLsRemoteLines(
                List.of("sha1 refs/heads/main", "only-one-token", "", "sha2 refs/tags/v5.7.0^{}"));
        assertEquals(Map.of("refs/heads/main", "sha1", "refs/tags/v5.7.0^{}", "sha2"), refs);
    }

    @Test
    void releasedPatchesOnlyCountPeeledTagLines() {
        List<Integer> patches = GitFacts.releasedPatchesFromRefs(List.of(
                "refs/tags/v5.7.0", "refs/tags/v5.7.0^{}", "refs/tags/v5.7.11^{}",
                "refs/tags/v5.8.0^{}", "refs/heads/release/5.7.x"), "5.7");
        assertEquals(List.of(0, 11), patches, "轻量标签行与它系不参与计数");
        assertEquals(0, GitFacts.nextPatchExpected(List.of()));
        assertEquals(12, GitFacts.nextPatchExpected(List.of(0, 11)));
    }

    @Test
    void markerExtractionDedupesInDeclarationOrder() {
        assertEquals(List.of("BUG-101", "CVE-2026-1234"),
                GitFacts.extractMarkers(List.of("fix: BUG-101 and CVE-2026-1234 tail",
                        "no marker here", "BUG-101 again")));
        assertEquals(List.of(), GitFacts.extractMarkers(List.of("nothing")));
    }

    @Test
    void grepHitSumIgnoresNonNumericTails() {
        assertEquals(5, GitFacts.sumGrepHits(List.of("abc:3", "def:notnum", "x:2")));
        assertEquals(0, GitFacts.sumGrepHits(List.of()));
        // 空匹配集恒 0 即"缺失"（JMH 教训同型：空列表不报错处显式判红的输入面）
    }

    @Test
    void legacyRegistrySkipsCommentsAndTrims() {
        Map<String, String> registry = GitFacts.parseLegacyRegistry(
                List.of("# 注释", "", "5.7.x | maintenance | 至 5.8.0 首发", "x|y|z"));
        assertEquals(Map.of("5.7.x", "maintenance", "x", "y"), registry);
    }

    @Test
    void versionShapePredicates() {
        assertTrue(GitFacts.isBareTripleVersion("5.7.8"));
        assertFalse(GitFacts.isBareTripleVersion("5.7"));
        assertFalse(GitFacts.isBareTripleVersion("5.7.8.1"));
        assertTrue(GitFacts.isSeriesStartVersion("5.8.0"));
        assertFalse(GitFacts.isSeriesStartVersion("5.8.1"));
    }

    @Test
    void commitTimeFormattingKnownValuesPinZoneAndPattern() {
        // 旧式 new Date(0).format("yyyyMMdd_HHmm") 即默认时区换算；java.time 等价用固定时区断言已知值
        assertEquals("19700101_0000", GitFacts.formatCommitTime(0L, ZoneId.of("UTC")));
        assertEquals("19700101_0800", GitFacts.formatCommitTime(0L, ZoneId.of("Asia/Shanghai")));
    }

    @Test
    void branchTypeFourShapes() {
        assertEquals("version", GitFacts.branchType("dev/5.7.x"));
        assertEquals("version", GitFacts.branchType("release/5.7.x"));
        assertEquals("release", GitFacts.branchType("5.7.8.RELEASE".toLowerCase()));
        assertEquals("dev", GitFacts.branchType("5.7.x"));
        assertEquals("test", GitFacts.branchType("feat/x"));
    }
}
