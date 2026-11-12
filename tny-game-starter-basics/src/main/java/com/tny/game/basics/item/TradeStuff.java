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
