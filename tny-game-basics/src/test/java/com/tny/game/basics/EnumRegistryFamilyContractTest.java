/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.mould.*;
import com.tny.game.common.enums.*;
import com.tny.game.common.result.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注册表族契约矩阵 + 现状差异钉桩（reduce-code-duplication 组6 tasks 6.1，重构前必须全绿）。
 * <p>
 * 覆盖 8 个 basics 注册表门面（item/Behaviors、Actions、Abilities、DemandParams、ItemTypes、DemandTypes，
 * mould/Features、Moulds）的转发六件套 of/check(String)/check(int)/option/all/enumerator：
 * 命中/宽松未命中（of 返 null、option 空包装）/严格未命中（check 抛 NPE 且消息=现状模板"获取 {} X 不存在"
 * /"获取 ID为 {} 的 X 不存在"）；另钉 ItemTypes 别名扩展（ofAlias/ofModelId/ofItemId）、
 * DemandTypes.check 委托宽松通道的现状差异、Actions.getAll() 异名、Moulds/Features.register public 可见性
 * 与 item 6 类 register 包私有可见性。scheduler 侧 TaskReceiverTypes 用例并入
 * common-scheduler 的 TaskDeliveryContractTest（跨模块不可复用本类）。
 * <p>
 * 本类期望值即重构前行为账，收敛到泛型中间层后期望值一字不改仍须全绿（保行为铁律）。
 */
class EnumRegistryFamilyContractTest {

    // ---- 探针枚举夹具（id/身份名远离其他测试可能装载的值；嵌套于测试类，不发布） ----

    enum ProbeFeature implements Feature {
        PROBE_FEATURE_A(901001);
        private final int id;
        ProbeFeature(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public Collection<Mould> dependMoulds() {
            return Collections.emptyList();
        }
        @Override
        public String getDesc() {
            return name();
        }
        @Override
        public boolean isValid() {
            return true;
        }
        @Override
        public boolean isHasHandler() {
            return false;
        }
    }

    enum ProbeMould implements Mould {
        PROBE_MOULD_A(902001);
        private final int id;
        ProbeMould(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public boolean isValid() {
            return true;
        }
        @Override
        public boolean isHasHandler() {
            return false;
        }
    }

    enum ProbeBehavior implements Behavior {
        PROBE_BEHAVIOR_A(903001);
        private final int id;
        ProbeBehavior(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public Feature getFeature() {
            return ProbeFeature.PROBE_FEATURE_A;
        }
        @Override
        public String getDesc() {
            return name();
        }
        @Override
        public Action forAction(Object value) {
            return ProbeAction.PROBE_ACTION_A;
        }
    }

    enum ProbeAction implements Action {
        PROBE_ACTION_A(904001);
        private final int id;
        ProbeAction(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public Behavior getBehavior() {
            return ProbeBehavior.PROBE_BEHAVIOR_A;
        }
        @Override
        public Feature getFeature() {
            return ProbeFeature.PROBE_FEATURE_A;
        }
        @Override
        public String getDesc() {
            return name();
        }
    }

    enum ProbeAbility implements Ability {
        PROBE_ABILITY_A(905001);
        private final int id;
        ProbeAbility(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
    }

    enum ProbeDemandParam implements DemandParam {
        PROBE_DEMAND_PARAM_A(906001);
        private final int id;
        ProbeDemandParam(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
    }

    enum ProbeDemandType implements DemandType {
        PROBE_DEMAND_TYPE_A(907001);
        private final int id;
        ProbeDemandType(int id) {
            this.id = id;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public boolean isCost() {
            return false;
        }
        @Override
        public ResultCode getResultCode() {
            return ResultCode.SUCCESS;
        }
    }

    enum ProbeItemType implements ItemType {
        PROBE_ITEM_A(908001, "PROBE_HEAD"),
        PROBE_ITEM_TRUNK(9000000, "PROBE_TRUNK_HEAD"),
        PROBE_ITEM_ZERO(0, "PROBE_ZERO_HEAD");
        private final int id;
        private final String aliasHead;
        ProbeItemType(int id, String aliasHead) {
            this.id = id;
            this.aliasHead = aliasHead;
        }
        @Override
        public int id() {
            return id;
        }
        @Override
        public String getAliasHead() {
            return aliasHead;
        }
        @Override
        public String getDesc() {
            return name();
        }
    }

    @BeforeAll
    static void registerProbes() throws Exception {
        // item 6 类 register 为包私有静态（装载入口 createSelector(Xxx::register) 的消费形态）——
        // 本测试落 basics 根包（tasks 6.1 指定路径），经反射按现状可见性注册，不改任何生产签名
        registerViaReflection(Behaviors.class, Behavior.class, ProbeBehavior.PROBE_BEHAVIOR_A);
        registerViaReflection(Actions.class, Action.class, ProbeAction.PROBE_ACTION_A);
        registerViaReflection(Abilities.class, Ability.class, ProbeAbility.PROBE_ABILITY_A);
        registerViaReflection(DemandParams.class, DemandParam.class, ProbeDemandParam.PROBE_DEMAND_PARAM_A);
        registerViaReflection(DemandTypes.class, DemandType.class, ProbeDemandType.PROBE_DEMAND_TYPE_A);
        registerViaReflection(ItemTypes.class, ItemType.class, ProbeItemType.PROBE_ITEM_A);
        registerViaReflection(ItemTypes.class, ItemType.class, ProbeItemType.PROBE_ITEM_TRUNK);
        registerViaReflection(ItemTypes.class, ItemType.class, ProbeItemType.PROBE_ITEM_ZERO);
        // mould 2 类 register public（可见性差异本体）——直调
        Features.register(ProbeFeature.PROBE_FEATURE_A);
        Moulds.register(ProbeMould.PROBE_MOULD_A);
    }

    private static void registerViaReflection(Class<?> facade, Class<?> valueType, Object value) throws Exception {
        Method register = facade.getDeclaredMethod("register", valueType);
        register.setAccessible(true);
        register.invoke(null, value);
    }

    // ---- 严格族契约矩阵：命中 / 宽松未命中 / 严格未命中消息模板现状 ----

    @Test
    void behaviorsForwardingSixPieceContract() {
        assertSame(ProbeBehavior.PROBE_BEHAVIOR_A, Behaviors.of("PROBE_BEHAVIOR_A"));
        assertSame(ProbeBehavior.PROBE_BEHAVIOR_A, Behaviors.of(903001));
        assertSame(ProbeBehavior.PROBE_BEHAVIOR_A, Behaviors.check("PROBE_BEHAVIOR_A"));
        assertSame(ProbeBehavior.PROBE_BEHAVIOR_A, Behaviors.check(903001));
        assertEquals(Optional.of(ProbeBehavior.PROBE_BEHAVIOR_A), Behaviors.option("PROBE_BEHAVIOR_A"));
        assertEquals(Optional.of(ProbeBehavior.PROBE_BEHAVIOR_A), Behaviors.option(903001));
        assertNull(Behaviors.of("MISS_PROBE_BEHAVIOR_A"), "宽松 of 未命中须返 null");
        assertNull(Behaviors.of(990001), "宽松 of(int) 未命中须返 null");
        assertEquals(Optional.empty(), Behaviors.option("MISS_PROBE_BEHAVIOR_A"));
        assertEquals(Optional.empty(), Behaviors.option(990001));
        assertEquals("获取 MISS_PROBE_BEHAVIOR_A Behavior 不存在",
                assertThrows(NullPointerException.class, () -> Behaviors.check("MISS_PROBE_BEHAVIOR_A")).getMessage(),
                "check(String) 现状消息模板");
        assertEquals("获取 ID为 990001 的 Behavior 不存在",
                assertThrows(NullPointerException.class, () -> Behaviors.check(990001)).getMessage(),
                "check(int) 现状消息模板");
        assertTrue(Behaviors.all().contains(ProbeBehavior.PROBE_BEHAVIOR_A));
        assertThrows(UnsupportedOperationException.class,
                () -> Behaviors.<Behavior>all().add(ProbeBehavior.PROBE_BEHAVIOR_A), "all() 现状为不可修改视图");
        assertSame(Behaviors.enumerator(), Behaviors.enumerator(), "enumerator() 应返回同一 holder");
    }

    @Test
    void abilitiesForwardingSixPieceContract() {
        assertSame(ProbeAbility.PROBE_ABILITY_A, Abilities.of("PROBE_ABILITY_A"));
        assertSame(ProbeAbility.PROBE_ABILITY_A, Abilities.of(905001));
        assertSame(ProbeAbility.PROBE_ABILITY_A, Abilities.check("PROBE_ABILITY_A"));
        assertSame(ProbeAbility.PROBE_ABILITY_A, Abilities.check(905001));
        assertEquals(Optional.of(ProbeAbility.PROBE_ABILITY_A), Abilities.option("PROBE_ABILITY_A"));
        assertEquals(Optional.of(ProbeAbility.PROBE_ABILITY_A), Abilities.option(905001));
        assertNull(Abilities.of("MISS"));
        assertNull(Abilities.of(990002));
        assertEquals(Optional.empty(), Abilities.option("MISS"));
        assertEquals(Optional.empty(), Abilities.option(990002));
        assertEquals("获取 MISS_ABILITY Ability 不存在",
                assertThrows(NullPointerException.class, () -> Abilities.check("MISS_ABILITY")).getMessage());
        assertEquals("获取 ID为 990002 的 Ability 不存在",
                assertThrows(NullPointerException.class, () -> Abilities.check(990002)).getMessage());
        assertTrue(Abilities.all().contains(ProbeAbility.PROBE_ABILITY_A));
        assertSame(Abilities.enumerator(), Abilities.enumerator());
    }

    @Test
    void demandParamsForwardingSixPieceContract() {
        assertSame(ProbeDemandParam.PROBE_DEMAND_PARAM_A, DemandParams.of("PROBE_DEMAND_PARAM_A"));
        assertSame(ProbeDemandParam.PROBE_DEMAND_PARAM_A, DemandParams.of(906001));
        assertSame(ProbeDemandParam.PROBE_DEMAND_PARAM_A, DemandParams.check("PROBE_DEMAND_PARAM_A"));
        assertSame(ProbeDemandParam.PROBE_DEMAND_PARAM_A, DemandParams.check(906001));
        assertEquals(Optional.of(ProbeDemandParam.PROBE_DEMAND_PARAM_A), DemandParams.option("PROBE_DEMAND_PARAM_A"));
        assertEquals(Optional.of(ProbeDemandParam.PROBE_DEMAND_PARAM_A), DemandParams.option(906001));
        assertNull(DemandParams.of("MISS"));
        assertNull(DemandParams.of(990003));
        assertEquals(Optional.empty(), DemandParams.option("MISS"));
        assertEquals(Optional.empty(), DemandParams.option(990003));
        assertEquals("获取 MISS_PARAM DemandParam 不存在",
                assertThrows(NullPointerException.class, () -> DemandParams.check("MISS_PARAM")).getMessage());
        assertEquals("获取 ID为 990003 的 DemandParam 不存在",
                assertThrows(NullPointerException.class, () -> DemandParams.check(990003)).getMessage());
        assertTrue(DemandParams.all().contains(ProbeDemandParam.PROBE_DEMAND_PARAM_A));
        assertSame(DemandParams.enumerator(), DemandParams.enumerator());
    }

    @Test
    void itemTypesForwardingSixPieceContract() {
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.of("PROBE_ITEM_A"));
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.of(908001));
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.check("PROBE_ITEM_A"));
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.check(908001));
        assertEquals(Optional.of(ProbeItemType.PROBE_ITEM_A), ItemTypes.option("PROBE_ITEM_A"));
        assertEquals(Optional.of(ProbeItemType.PROBE_ITEM_A), ItemTypes.option(908001));
        assertNull(ItemTypes.of("MISS"));
        assertNull(ItemTypes.of(990004));
        assertEquals(Optional.empty(), ItemTypes.option("MISS"));
        assertEquals(Optional.empty(), ItemTypes.option(990004));
        assertEquals("获取 MISS_ITEM ItemType 不存在",
                assertThrows(NullPointerException.class, () -> ItemTypes.check("MISS_ITEM")).getMessage());
        assertEquals("获取 ID为 990004 的 ItemType 不存在",
                assertThrows(NullPointerException.class, () -> ItemTypes.check(990004)).getMessage());
        assertTrue(ItemTypes.all().contains(ProbeItemType.PROBE_ITEM_A));
        assertSame(ItemTypes.enumerator(), ItemTypes.enumerator());
    }

    @Test
    void featuresForwardingSixPieceContract() {
        assertSame(ProbeFeature.PROBE_FEATURE_A, Features.of("PROBE_FEATURE_A"));
        assertSame(ProbeFeature.PROBE_FEATURE_A, Features.of(901001));
        assertSame(ProbeFeature.PROBE_FEATURE_A, Features.check("PROBE_FEATURE_A"));
        assertSame(ProbeFeature.PROBE_FEATURE_A, Features.check(901001));
        assertEquals(Optional.of(ProbeFeature.PROBE_FEATURE_A), Features.option("PROBE_FEATURE_A"));
        assertEquals(Optional.of(ProbeFeature.PROBE_FEATURE_A), Features.option(901001));
        assertNull(Features.of("MISS"));
        assertNull(Features.of(990005));
        assertEquals(Optional.empty(), Features.option("MISS"));
        assertEquals(Optional.empty(), Features.option(990005));
        assertEquals("获取 MISS_FEATURE Feature 不存在",
                assertThrows(NullPointerException.class, () -> Features.check("MISS_FEATURE")).getMessage());
        assertEquals("获取 ID为 990005 的 Feature 不存在",
                assertThrows(NullPointerException.class, () -> Features.check(990005)).getMessage());
        assertTrue(Features.all().contains(ProbeFeature.PROBE_FEATURE_A));
        assertSame(Features.enumerator(), Features.enumerator());
    }

    @Test
    void mouldsForwardingSixPieceContract() {
        assertSame(ProbeMould.PROBE_MOULD_A, Moulds.of("PROBE_MOULD_A"));
        assertSame(ProbeMould.PROBE_MOULD_A, Moulds.of(902001));
        assertSame(ProbeMould.PROBE_MOULD_A, Moulds.check("PROBE_MOULD_A"));
        assertSame(ProbeMould.PROBE_MOULD_A, Moulds.check(902001));
        assertEquals(Optional.of(ProbeMould.PROBE_MOULD_A), Moulds.option("PROBE_MOULD_A"));
        assertEquals(Optional.of(ProbeMould.PROBE_MOULD_A), Moulds.option(902001));
        assertNull(Moulds.of("MISS"));
        assertNull(Moulds.of(990006));
        assertEquals(Optional.empty(), Moulds.option("MISS"));
        assertEquals(Optional.empty(), Moulds.option(990006));
        assertEquals("获取 MISS_MOULD Mould 不存在",
                assertThrows(NullPointerException.class, () -> Moulds.check("MISS_MOULD")).getMessage());
        assertEquals("获取 ID为 990006 的 Mould 不存在",
                assertThrows(NullPointerException.class, () -> Moulds.check(990006)).getMessage());
        assertTrue(Moulds.all().contains(ProbeMould.PROBE_MOULD_A));
        assertSame(Moulds.enumerator(), Moulds.enumerator());
    }

    // ---- 现状差异钉桩 ----

    /**
     * DemandTypes.check 严格通道契约（fix-registered-defects tasks 6.1，design D6——原状差异钉桩翻转：
     * 未注册身份由委托宽松 of 返 null 改显式失败，对齐兄弟门面既有严格通道与差量
     * enumeration-facade-semantics「严格校验通道不得降级为宽松空返」）。
     */
    @Test
    void demandTypesCheckIsLenientLikeOf_DivergencePinnedNotFixed() {
        assertSame(ProbeDemandType.PROBE_DEMAND_TYPE_A, DemandTypes.check("PROBE_DEMAND_TYPE_A"));
        assertSame(ProbeDemandType.PROBE_DEMAND_TYPE_A, DemandTypes.check(907001));
        assertEquals("获取 MISS_DEMAND_TYPE DemandType 不存在",
                assertThrows(NullPointerException.class, () -> DemandTypes.check("MISS_DEMAND_TYPE")).getMessage(),
                "翻转（6.1）：check(String) 未注册身份显式失败且携带身份信息，对齐兄弟门面严格模板");
        assertEquals("获取 ID为 990007 的 DemandType 不存在",
                assertThrows(NullPointerException.class, () -> DemandTypes.check(990007)).getMessage(),
                "翻转（6.1）：check(int) 未注册身份显式失败且携带身份信息，对齐兄弟门面严格模板");
        // 其余六件套与严格族一致
        assertSame(ProbeDemandType.PROBE_DEMAND_TYPE_A, DemandTypes.of("PROBE_DEMAND_TYPE_A"));
        assertSame(ProbeDemandType.PROBE_DEMAND_TYPE_A, DemandTypes.of(907001));
        assertEquals(Optional.of(ProbeDemandType.PROBE_DEMAND_TYPE_A), DemandTypes.option("PROBE_DEMAND_TYPE_A"));
        assertEquals(Optional.of(ProbeDemandType.PROBE_DEMAND_TYPE_A), DemandTypes.option(907001));
        assertNull(DemandTypes.of("MISS"));
        assertEquals(Optional.empty(), DemandTypes.option("MISS"));
        assertTrue(DemandTypes.all().contains(ProbeDemandType.PROBE_DEMAND_TYPE_A));
        assertSame(DemandTypes.enumerator(), DemandTypes.enumerator());
    }

    /** ItemTypes 别名扩展：ofAlias 命中/无分隔符/未命中消息现状＋空串/纯分隔符受控失败（6.2 翻转正解）；
     *  ofModelId 段取整现状＋ofItemId 单一商式归位（6.3 翻转，差量 enumeration-facade-semantics） */
    @Test
    void itemTypesAliasAndIdTruncationExtensions() {
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.ofAlias("PROBE_HEAD$777"), "别名前缀+后缀应取前缀");
        assertSame(ProbeItemType.PROBE_ITEM_A, ItemTypes.ofAlias("PROBE_HEAD"), "无分隔符时整串即前缀");
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofAlias("PROBE_TRUNK_HEAD$x"));
        assertEquals("获取 别名前缀 NO_HEAD 的 ItemType 不存在",
                assertThrows(NullPointerException.class, () -> ItemTypes.ofAlias("NO_HEAD$x")).getMessage(),
                "ofAlias 现状严格消息模板");
        // 翻转（6.2，差量「空标识符查找显式失败不越界」）：空串/纯分隔符拆前判非法，受控失败且信息含输入描述
        IllegalArgumentException emptyAlias = assertThrows(IllegalArgumentException.class, () -> ItemTypes.ofAlias(""),
                "翻转（6.2）：空串别名改受控失败，越界栈不得再出现");
        assertTrue(emptyAlias.getMessage().contains("\"\""),
                "受控失败消息须含输入描述: " + emptyAlias.getMessage());
        IllegalArgumentException sepOnlyAlias = assertThrows(IllegalArgumentException.class, () -> ItemTypes.ofAlias("$"),
                "翻转（6.2）：纯分隔符输入拆后无段，同改受控失败（现状亦 heads[0] 越界）");
        assertTrue(sepOnlyAlias.getMessage().contains("$"),
                "受控失败消息须含输入描述: " + sepOnlyAlias.getMessage());
        // ofModelId：modelId/ID_TAIL_SIZE*ID_TAIL_SIZE 向下取整到百万段
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofModelId(9000123), "9000123→9000000 段命中");
        assertSame(ProbeItemType.PROBE_ITEM_ZERO, ItemTypes.ofModelId(9999), "亚百万段取整为 0");
        assertNull(ItemTypes.ofModelId(10000000), "10000000→10000000 段未注册返回 null");
        // 翻转（6.3，差量「道具位号解析按模型段商式单一规则收敛」）：单一商式段基址还原——全局号取商至首位、
        // 首位×ID_TAIL_SIZE 还原段基址后交 ofModelId；同模型序列号恒归位模型条目，未注册段按宽松 of 通道返 null。
        // 原状「每级 /10^k 商恒 < ID_TAIL_SIZE → 任意输入恒落 0 段」的五分支钉桩全部按承诺方向翻正
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofItemId(9999L),
                "翻转（6.3）：9999 首位 9 → 段基址 9000000 归位 TRUNK（TRUNK.itemIdOf(999) 实产样本）");
        assertNull(ItemTypes.ofItemId(10000L),
                "翻转（6.3）：10000 首位 1 → 段基址 1000000 未注册 → 宽松 of 通道语义返 null");
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofItemId(900000000000L),
                "翻转（6.3）：9e11 首位 9 → 段基址 9000000 归位 TRUNK");
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofItemId(9000000000000000L),
                "翻转（6.3）：9e15 首位 9 → 段基址 9000000 归位 TRUNK");
        assertSame(ProbeItemType.PROBE_ITEM_TRUNK, ItemTypes.ofItemId(9000000000000000000L),
                "翻转（6.3）：9e18 首位 9 → 段基址 9000000 归位 TRUNK");
    }

    /** Actions.getAll() 异名现状：本类只有 getAll 无 all（兄弟均为 all），且返回集合含已注册值 */
    @Test
    void actionsGetAllAliasShapePinned() throws Exception {
        Method getAll = Actions.class.getDeclaredMethod("getAll");
        assertTrue(Modifier.isStatic(getAll.getModifiers()));
        assertTrue(Modifier.isPublic(getAll.getModifiers()));
        assertThrows(NoSuchMethodException.class, () -> Actions.class.getDeclaredMethod("all"),
                "Actions 现状无 all()——异名薄委托不得借收敛改名");
        Collection<Action> all = Actions.getAll();
        assertTrue(all.contains(ProbeAction.PROBE_ACTION_A));
        assertEquals("获取 MISS_ACTION Action 不存在",
                assertThrows(NullPointerException.class, () -> Actions.check("MISS_ACTION")).getMessage());
        assertEquals("获取 ID为 990008 的 Action 不存在",
                assertThrows(NullPointerException.class, () -> Actions.check(990008)).getMessage());
        assertSame(ProbeAction.PROBE_ACTION_A, Actions.check("PROBE_ACTION_A"));
        assertSame(ProbeAction.PROBE_ACTION_A, Actions.of(904001));
        assertEquals(Optional.of(ProbeAction.PROBE_ACTION_A), Actions.option(904001));
        assertEquals(Optional.of(ProbeAction.PROBE_ACTION_A), Actions.option("PROBE_ACTION_A"));
        assertSame(Actions.enumerator(), Actions.enumerator());
    }

    /** register 入口可见性现状矩阵：item 6 类包私有、mould 2 类 public（D5 逐一保留依据） */
    @Test
    void registerEntryVisibilityMatrixPinned() throws Exception {
        assertPackagePrivateRegister(Behaviors.class, Behavior.class);
        assertPackagePrivateRegister(Actions.class, Action.class);
        assertPackagePrivateRegister(Abilities.class, Ability.class);
        assertPackagePrivateRegister(DemandParams.class, DemandParam.class);
        assertPackagePrivateRegister(DemandTypes.class, DemandType.class);
        assertPackagePrivateRegister(ItemTypes.class, ItemType.class);
        assertPublicRegister(Features.class, Feature.class);
        assertPublicRegister(Moulds.class, Mould.class);
    }

    private static void assertPackagePrivateRegister(Class<?> facade, Class<?> valueType) throws Exception {
        Method register = facade.getDeclaredMethod("register", valueType);
        assertTrue(Modifier.isStatic(register.getModifiers()), facade.getSimpleName() + ".register 须为静态");
        assertFalse(Modifier.isPublic(register.getModifiers()) || Modifier.isProtected(register.getModifiers()),
                facade.getSimpleName() + ".register 现状包私有，不得借收敛提升可见性");
    }

    private static void assertPublicRegister(Class<?> facade, Class<?> valueType) throws Exception {
        Method register = facade.getDeclaredMethod("register", valueType);
        assertTrue(Modifier.isStatic(register.getModifiers()));
        assertTrue(Modifier.isPublic(register.getModifiers()),
                facade.getSimpleName() + ".register 现状 public（Moulds/Features 对外装载面），不得借收敛收窄");
    }

}
