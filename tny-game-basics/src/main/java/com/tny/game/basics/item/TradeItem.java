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
 * 将要交易的Item信息
 *
 * @param <I> ItemModel类型
 */
public interface TradeItem<I extends StuffModel> extends DealItem<I> {

    AlterType getAlertType();

    boolean isValid();

    @SuppressWarnings("unchecked")
    default <IM extends I> TradeItem<IM> as() {
        return (TradeItem<IM>)this;
    }

}
