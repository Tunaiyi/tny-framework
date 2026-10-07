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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ReleaseGateCheck 门禁判定的单元测试（convert-orchestration-to-java 任务 4.1）：
 * 覆盖通过向与违例向，违例断言逐条指名判红对象与理由、文案主体承 tny.release.gradle 原句。
 */
class ReleaseGateCheckTest {

    @Test
    void releaseVersionAcceptsBareTriple() {
        assertEquals("5.7.8", ReleaseGateCheck.requireReleaseVersion("releaseCut", "5.7.8"));
    }

    @Test
    void releaseVersionRejectsMissingAndMalformed() {
        GradleException missing = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireReleaseVersion("releaseTag", null));
        assertTrue(missing.getMessage().contains("releaseTag 需要 -PreleaseVersion"), "须指名任务与参数");
        GradleException malformed = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireReleaseVersion("releaseTag", "5.7"));
        assertTrue(malformed.getMessage().contains("版本号 '5.7' 不符合裸三段号形态"));
    }

    @Test
    void legacyOccupiedNumberRefused() {
        List<String> legacy = List.of("5.5.0", "5.4.2");
        assertDoesNotThrow(() -> ReleaseGateCheck.assertNotLegacyOccupied("5.7.8", legacy));
        GradleException ex = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.assertNotLegacyOccupied("5.5.0", legacy));
        assertTrue(ex.getMessage().contains("'5.5.0'") && ex.getMessage().contains("禁止裸号复用同号"));
    }

    @Test
    void cutBranchWhitelist() {
        assertDoesNotThrow(() -> ReleaseGateCheck.requireCutBranch(true, false, "main"));
        assertDoesNotThrow(() -> ReleaseGateCheck.requireCutBranch(false, true, "5.7.x"));
        GradleException ex = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireCutBranch(false, false, "feat/x"));
        assertTrue(ex.getMessage().contains("当前分支 'feat/x'"));
    }

    @Test
    void mainCutOnlySeriesStartAndUniqueness() {
        assertDoesNotThrow(() -> ReleaseGateCheck.requireSeriesStart("5.8.0", "5.8"));
        GradleException notStart = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireSeriesStart("5.8.1", "5.8"));
        assertTrue(notStart.getMessage().contains("系列首发 <N.M>.0") && notStart.getMessage().contains("收到 '5.8.1'"));
        assertTrue(assertThrows(GradleException.class,
                () -> ReleaseGateCheck.assertLocalBranchFree("release/5.8.x", true)).getMessage()
                .contains("每系列维护分支唯一"));
        assertTrue(assertThrows(GradleException.class,
                () -> ReleaseGateCheck.assertRemoteMaintenanceFree("release/5.8.x", true)).getMessage()
                .contains("每系列唯一"));
    }

    @Test
    void tagBranchWhitelistForBothTracks() {
        assertDoesNotThrow(() -> ReleaseGateCheck.requireTagBranch("5.7.8", "5.7", "release/5.7.x"));
        assertDoesNotThrow(() -> ReleaseGateCheck.requireTagBranch("5.7.8", "5.7", "5.7.8.release"));
        GradleException ex = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireTagBranch("5.7.8", "5.7", "main"));
        assertTrue(ex.getMessage().contains("release/5.7.x") && ex.getMessage().contains("'5.7.8.release'"));
    }

    @Test
    void tagIdempotenceThreeStates() {
        assertDoesNotThrow(() -> ReleaseGateCheck.assertTagIdempotent("v5.7.8", null, "c0ffee1"));
        assertDoesNotThrow(() -> ReleaseGateCheck.assertTagIdempotent("v5.7.8",
                Map.of("annotated", true, "commit", "c0ffee1"), "c0ffee1"), "附注且指当前提交仅补推送");
        assertTrue(assertThrows(GradleException.class, () -> ReleaseGateCheck.assertTagIdempotent("v5.7.8",
                Map.of("annotated", false, "commit", "c0ffee1"), "c0ffee1")).getMessage().contains("轻量标签"));
        GradleException wrongCommit = assertThrows(GradleException.class, () -> ReleaseGateCheck.assertTagIdempotent(
                "v5.7.8", Map.of("annotated", true, "commit", "deadbee"), "c0ffee1"));
        assertTrue(wrongCommit.getMessage().contains("指向非当前构建提交（deadbee）"));
    }

    @Test
    void nextPatchNumberCheckGreenAndRed() {
        assertDoesNotThrow(() -> ReleaseGateCheck.assertNextPatchNumber("5.7", "5.7.12", List.of(0, 11)));
        assertDoesNotThrow(() -> ReleaseGateCheck.assertNextPatchNumber("5.8", "5.8.0", List.of()), "无标签首发 .0");
        GradleException ahead = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.assertNextPatchNumber("5.7", "5.7.13", List.of(0, 11)));
        assertTrue(ahead.getMessage().contains("最大补丁号 11") && ahead.getMessage().contains("应为 '5.7.12'"));
        GradleException onEmpty = assertThrows(GradleException.class,
                () -> ReleaseGateCheck.assertNextPatchNumber("5.9", "5.9.1", List.of()));
        assertTrue(onEmpty.getMessage().contains("（无）") && onEmpty.getMessage().contains("应为 '5.9.0'"));
    }

    @Test
    void remoteResolutionFailureNamesBranch() {
        assertTrue(assertThrows(GradleException.class,
                () -> ReleaseGateCheck.requireRemote(null, "dev/5.7.x")).getMessage()
                .contains("当前分支 'dev/5.7.x' 没有上游"));
    }
}
