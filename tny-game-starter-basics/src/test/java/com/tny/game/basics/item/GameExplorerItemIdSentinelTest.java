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

package com.tny.game.basics.item;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameExplorer 消费面解析哨兵（fix-registered-defects tasks 1.1，design D5「验证（先手）」；
 * enumeration-facade-semantics 差量第三 Requirement 的回归 Scenario）。
 * <p>
 * 号段证据取自 ItemTypes/ItemType 注册实际来源：以注册的模型条目调用其自身的
 * {@link ItemType#itemIdOf(long)}（即 GameWarehouse 生成链 {@code BaseSingleStuffOwner} 同款产出方式）
 * 构造全局序列号，断言 {@link ItemTypes#ofItemId(long)} 恒归位其模型条目。
 * 现状位宽阶梯先行缩位、再叠段内截断，商恒小于段基址——任意输入恒解析到段 0
 * （本 JVM 段 0 未注册 → null，即"恒 0 命中"红基线）；修复（单一商式，D5）后转绿。
 * <p>
 * GameExplorer 两处 ofItemId 调用点（getItem(AnyId) 经 anyId.getId()、getItemManager(long)）
 * 的解析段即本哨兵钉住的语义；manager 表寻址面属 Spring 装配域，本哨兵不涉。
 * register 为 ItemTypes 包私有静态装载入口（createSelector(ItemTypes::register) 消费形态），
 * 本测试按现状可见性同包直调，不改任何生产签名。
 */
class GameExplorerItemIdSentinelTest {

    /**
     * 哨兵模型条目：号段照策划形态取百万段基址（getIdHead()=id/ID_TAIL_SIZE 首位单档），
     * 身份名与别名头远离其他测试夹具，防串扰。
     */
    enum SentinelItemType implements ItemType {

        SENTINEL_MODEL_2E6(2000000, "SENTINEL_HEAD_2E6"),
        SENTINEL_MODEL_9E6(9000000, "SENTINEL_HEAD_9E6");

        private final int id;
        private final String aliasHead;

        SentinelItemType(int id, String aliasHead) {
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
    static void registerSentinelModels() {
        ItemTypes.register(SentinelItemType.SENTINEL_MODEL_2E6);
        ItemTypes.register(SentinelItemType.SENTINEL_MODEL_9E6);
    }

    /** 差量 Scenario「同模型序列号全部归位其模型」：模型 2000000 下不同位宽序列号（含 2000001、2500000999） */
    @Test
    void serialsOfSameModelAllResolveBackToModelEntry() {
        // itemIdOf 实产样本：head 2 拼接短/长序列号（21 与 2500000999——后者超 int 位宽，(int) 直转即回绕）
        assertSame(SentinelItemType.SENTINEL_MODEL_2E6,
                ItemTypes.ofItemId(SentinelItemType.SENTINEL_MODEL_2E6.itemIdOf(1)),
                "itemIdOf 产出的 2 位序列号须归位模型 2000000（现状恒解析段 0=红基线）");
        assertSame(SentinelItemType.SENTINEL_MODEL_2E6, ItemTypes.ofItemId(2000001L),
                "差量样本 2000001 须解析回 2000000 条目（现状恒解析段 0=红基线）");
        assertSame(SentinelItemType.SENTINEL_MODEL_2E6,
                ItemTypes.ofItemId(SentinelItemType.SENTINEL_MODEL_2E6.itemIdOf(500000999L)),
                "差量样本 2500000999（itemIdOf 十位宽实产）须解析回 2000000 条目（现状恒解析段 0=红基线）");
        // 模型 9000000 下不同位宽样本（9999="9"++"999"、9e18 十八位宽）
        assertSame(SentinelItemType.SENTINEL_MODEL_9E6,
                ItemTypes.ofItemId(SentinelItemType.SENTINEL_MODEL_9E6.itemIdOf(999)),
                "9999 系模型 9000000 的实产序列号，须归位 9000000（现状恒解析段 0=红基线）");
        assertSame(SentinelItemType.SENTINEL_MODEL_9E6, ItemTypes.ofItemId(9000000000000000000L),
                "9e18 宽位序列号首位 9，须归位模型 9000000（现状恒解析段 0=红基线）");
    }

    /** 差量 Scenario「未注册模型段则按既有宽松入口语义返回」：of 通道未命中返 null，不抛 */
    @Test
    void unregisteredHeadSegmentFollowsLenientLookupSemantics() {
        assertNull(ItemTypes.ofItemId(50000000000000L),
                "首位 5 → 段基址 5000000 未注册，宽松 of 通道语义须返 null（不抛不越界）");
        assertNull(ItemTypes.ofItemId(-7000000000L),
                "负全局号非注册号段形态，宽松 of 通道语义须返 null（不抛不越界）");
    }

}
