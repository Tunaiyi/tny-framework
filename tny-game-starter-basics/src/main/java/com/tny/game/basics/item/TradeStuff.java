/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

/**
 * Created by Kun Yang on 2017/4/11.
 */
public class TradeStuff {

    /**
     * 物品ID
     */
    private int modelId;

    /**
     * 物品数量
     */
    private long number;

    /**
     * 更改方式 1: 检测上下限 2: 不检测上下限(可超出) 3: 忽略多出
     */
    private int alterType;

    public TradeStuff() {

    }

    public TradeStuff(int modelId, long number) {
        this(modelId, number, AlterType.UNCHECK.getId());
    }

    public TradeStuff(int modelId, long number, int alterType) {
        this.modelId = modelId;
        this.number = number;
        this.alterType = alterType;
    }

    public int getModelId() {
        return modelId;
    }

    public long getNumber() {
        return number;
    }

    public int getAlterType() {
        return alterType;
    }

    public TradeStuff setModelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    public TradeStuff setNumber(long number) {
        this.number = number;
        return this;
    }

    public TradeStuff setAlterType(int alterType) {
        this.alterType = alterType;
        return this;
    }

}
