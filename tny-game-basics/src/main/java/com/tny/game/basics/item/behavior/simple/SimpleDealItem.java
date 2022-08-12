/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.behavior.simple;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

import java.util.*;

public class SimpleDealItem<I extends StuffModel> implements DealItem<I> {

    private long id;

    private I itemModel;

    private Number number;

    private Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();

    public SimpleDealItem() {
        super();
    }

    @SuppressWarnings("unchecked")
    public SimpleDealItem(DemandResult result, DemandParamEntry<?>... entries) {
        super();
        this.id = result.getId();
        this.itemModel = (I)result.getItemModel();
        this.number = result.getExpectValue(Integer.class);
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleDealItem(I itemModel, Number number, DemandParamEntry<?>... entries) {
        super();
        this.itemModel = itemModel;
        this.number = number;
        for (DemandParamEntry<?> entry : entries) {
            this.paramMap.put(entry.getParam(), entry.getValue());
        }
    }

    public SimpleDealItem(DealItem<I> item, Number number) {
        super();
        this.itemModel = item.getItemModel();
        this.number = number;
        if (paramMap != null) {
            this.paramMap.putAll(item.getParamMap());
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SI extends I> SI getItemModel() {
        return (SI)itemModel;
    }

    @Override
    public Number getNumber() {
        return number;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param) {
        Object value = this.paramMap.get(param);
        if (value == null) {
            return null;
        }
        return (P)value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, P defaultValue) {
        Object value = this.getParam(param, defaultValue.getClass());
        return value == null ? defaultValue : (P)value;
    }

    @Override
    public Map<DemandParam, Object> getParamMap() {
        return Collections.unmodifiableMap(paramMap);
    }

    @Override
    public String toString() {
        return "SimpleTradeItem [itemModel=" + itemModel + ", number=" + number + "]";
    }

}
