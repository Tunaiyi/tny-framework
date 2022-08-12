/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.behavior;

/**
 * 交易类型
 *
 * @author KGTny
 */
public enum TradeType {

    /**
     * 奖励
     */
    AWARD(1),

    /**
     * 扣除
     */
    DEDUCT(2);

    private final int id;

    private TradeType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static TradeType get(int id) {
        if (AWARD.id == id) {
            return AWARD;
        } else if (DEDUCT.id == id) {
            return DEDUCT;
        }
        throw new NullPointerException("不存在TradeType id 为" + id);
    }
}
