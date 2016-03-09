package com.tny.game.base.item.behavior;

import com.tny.game.base.item.ItemModel;
import com.tny.game.common.reflect.ObjectUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 条件结果
 *
 * @author KGTny
 */
public class DemandResult {

    /**
     * 条件对应Item的ID
     */
    private long id;

    /**
     * 条件相关的itemID
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

    public DemandResult(long id, ItemModel itemModel, DemandType demandType, Object currentValue, Object expectValue, boolean satisfy, Map<DemandParam, Object> paramMap) {
        this.id = id;
        this.itemModel = itemModel;
        this.demandType = demandType;
        this.currentValue = currentValue;
        this.expectValue = expectValue;
        this.satisfy = satisfy;
        if (paramMap == null)
            this.paramMap = Collections.emptyMap();
        else
            this.paramMap = paramMap;
    }

    /**
     * 条件对应Item的ID
     *
     * @return
     */
    public long getID() {
        return id;
    }

    /**
     * 获取条件涉及的itemID
     *
     * @return
     */
    public int getItemID() {
        return itemModel.getID();
    }

    /**
     * @return the itemModel
     */
    public ItemModel getItemModel() {
        return itemModel;
    }

    /**
     * 条件类型
     *
     * @return
     */
    public DemandType getDemandType() {
        return demandType;
    }

    /**
     * 获取当前值
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V> V getCurrentValue(Class<V> clazz) {
        if (currentValue == null)
            return null;
        if (clazz.isInstance(currentValue))
            return (V) currentValue;
        throw new ClassCastException(currentValue + "is not " + clazz + "instance");
    }

    /**
     * 获取期望值
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public <V> V getExpectValue(Class<V> clazz) {
        if (expectValue == null)
            return null;
        return ObjectUtils.as(expectValue, clazz);
    }

    @SuppressWarnings("unchecked")
    public <P> P getParam(DemandParam param, Class<P> clazz) {
        Object value = this.paramMap.get(param);
        if (param == null)
            return null;
        return ObjectUtils.as(value, clazz);
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
     * 是否满足该条件
     *
     * @return
     */
    public boolean isSatisfy() {
        return satisfy;
    }

    @Override
    public String toString() {
        return "DemandResult [itemID=" + getItemID() + ", demandType=" + demandType + ", currentValue=" + currentValue
                + ", expectValue=" + expectValue + ", satisfy=" + satisfy + "]";
    }

}
