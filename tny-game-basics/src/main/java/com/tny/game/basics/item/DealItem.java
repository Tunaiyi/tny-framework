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
