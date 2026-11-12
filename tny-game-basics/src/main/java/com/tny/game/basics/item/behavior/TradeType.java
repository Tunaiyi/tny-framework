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
