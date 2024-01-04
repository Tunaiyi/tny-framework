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

import com.tny.game.basics.item.behavior.*;

import java.util.Map;

/**
 * 交易到的Item信息
 *
 * @param <I> ItemModel类型
 */
public interface DealItem<I extends StuffModel> {

    long getId();

    <SI extends I> SI getItemModel();

    default ItemType getItemType() {
        return getItemModel().getItemType();
    }

    default int getModelId() {
        return getItemModel().getId();
    }

    Number getNumber();

    Map<DemandParam, Object> getParamMap();

    <P> P getParam(DemandParam param, P defaultValue);

    <P> P getParam(DemandParam param);

}
