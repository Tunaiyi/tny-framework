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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tny.convention.checker;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 组号对账判定的单元测试（pilot-binary-build-conventions 任务 5.1，gradle-build-style
 * "二进制实现的检查逻辑必须携带单元测试"需求：每个检查类至少一个通过用例与一个违例用例）：
 * 覆盖一致方向零问题、组号失配（普通成员与插件例外成员）与构建文件悬空三类违例的
 * 判红对象与理由文案。
 */
class GroupAlignmentCheckTest {

    private static final String GROUP = "com.tnydev.game";

    private static final String LEGACY = "com.tny.game";

    @Test
    void alignedFactsProduceNoViolations() {
        List<GroupAlignmentCheck.ProjectFact> facts = List.of(
                new GroupAlignmentCheck.ProjectFact(":tny-game-net", "tny-game-net", GROUP, true, false),
                new GroupAlignmentCheck.ProjectFact(":tny-game-doc-gradle", "tny-game-doc-gradle", LEGACY, true, true));
        assertTrue(GroupAlignmentCheck.violations(facts, GROUP, LEGACY).isEmpty());
    }

    @Test
    void wrongGroupIsRedWithExpectedAndActualShown() {
        List<GroupAlignmentCheck.ProjectFact> facts = List.of(
                new GroupAlignmentCheck.ProjectFact(":tny-game-net", "tny-game-net", "com.wrong", true, false));
        List<String> violations = GroupAlignmentCheck.violations(facts, GROUP, LEGACY);
        assertEquals(1, violations.size());
        assertTrue(violations.get(0).contains(":tny-game-net"), "须指名判红对象");
        assertTrue(violations.get(0).contains(GROUP) && violations.get(0).contains("com.wrong"), "须列出期望值与实际值");
    }

    @Test
    void pluginMemberCheckedAgainstLegacyGroupOnly() {
        List<GroupAlignmentCheck.ProjectFact> facts = List.of(
                new GroupAlignmentCheck.ProjectFact(":tny-game-doc-gradle", "tny-game-doc-gradle", GROUP, true, true));
        List<String> violations = GroupAlignmentCheck.violations(facts, GROUP, LEGACY);
        assertEquals(1, violations.size());
        assertTrue(violations.get(0).contains("插件模块例外"), "例外成员的报错须说明其按例外组号判定");
    }

    @Test
    void danglingBuildFileIsRed() {
        List<GroupAlignmentCheck.ProjectFact> facts = List.of(
                new GroupAlignmentCheck.ProjectFact(":tny-game-ghost", "tny-game-ghost", GROUP, false, false));
        List<String> violations = GroupAlignmentCheck.violations(facts, GROUP, LEGACY);
        assertTrue(violations.stream().anyMatch(v -> v.contains("构建文件不存在") && v.contains(":tny-game-ghost")));
    }

    @Test
    void aggregatePrefixesListedViolations() {
        String message = GroupAlignmentCheck.aggregate(List.of("违例甲", "违例乙"));
        assertTrue(message.startsWith("发布构件配置期对账失败（组号单一事实源与构建文件存在性）：\n- 违例甲\n- 违例乙"));
    }
}
