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

package com.tny.game.basics.item.behavior;

import com.tny.game.basics.item.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;

import java.util.*;

/**
 * 条件结果
 *
 * @author KGTny
 */
public class DemandResult {

    /**
     * 条件对应Item的Id
     */
    private long id;

    /**
     * 条件相关的model
     */
    private ItemModel itemModel;

    /**
     * 条件类型
     */
    private DemandType demandType;

    /**
     * 当前值
     */
    private Object currentValue;

    /**
     * 期望值
     */
    private Object expectValue;

    private Map<DemandParam, Object> paramMap;

    /**
     * 是否满足条件
     */
    private boolean satisfy;

    public DemandResult(long id, ItemModel itemModel, DemandType demandType, Object currentValue, Object expectValue, boolean satisfy,
            Map<DemandParam, Object> paramMap) {
        this.id = id;
        this.itemModel = itemModel;
        this.demandType = demandType;
        this.currentValue = currentValue;
        this.expectValue = expectValue;
        this.satisfy = satisfy;
        if (paramMap == null) {
            this.paramMap = Collections.emptyMap();
        } else {
            this.paramMap = paramMap;
        }
    }

    /**
     * @return 条件对应Item的Id
     */
    public long getId() {
        return id;
    }

    /**
     * @return 获取条件涉及的modelId
     */
    public int getModelId() {
        return itemModel.getId();
    }

    /**
     * @return* the itemModel
     */
    public ItemModel getItemModel() {
        return itemModel;
    }

    /**
     * @return 条件类型
     */
    public DemandType getDemandType() {
        return demandType;
    }

    /**
     * @return 获取结果码
     */
    public ResultCode getResultCode() {
        if (satisfy) {
            return ResultCode.SUCCESS;
        }
        return this.demandType.getResultCode();
    }

    /**
     * @return 获取当前值
     */
    @SuppressWarnings("unchecked")
    public <V> V getCurrentValue(Class<V> clazz) {
        if (currentValue == null) {
            return null;
        }
        if (clazz.isInstance(currentValue)) {
            return (V) currentValue;
        }
        throw new ClassCastException(currentValue + "is not " + clazz + "instance");
    }

    /**
     * @return 获取期望值
     */
    @SuppressWarnings("unchecked")
    public <V> V getExpectValue(Class<V> clazz) {
        if (expectValue == null) {
            return null;
        }
        return ObjectAide.as(expectValue, clazz);
    }

    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, Class<P> clazz) {
        Object value = this.paramMap.get(param);
        if (param == null) {
            return null;
        }
        return ObjectAide.as(value, clazz);
    }

    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, P defaultValue) {
        Object value = this.getParam(param, defaultValue.getClass());
        return value == null ? defaultValue : (P) value;
    }

    public Map<DemandParam, Object> getParamMap() {
        return Collections.unmodifiableMap(paramMap);
    }

    /**
     * @return 是否满足该条件
     */
    public boolean isSatisfy() {
        return satisfy;
    }

    @Override
    public String toString() {
        return "DemandResult [modelId=" + getModelId() + ", demandType=" + demandType + ", currentValue=" + currentValue
               + ", expectValue=" + expectValue + ", satisfy=" + satisfy + "]";
    }

}
