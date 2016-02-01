package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.AbstractItemModel;
import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.Probability;
import com.tny.game.base.item.behavior.AbstractAward;
import com.tny.game.base.item.behavior.DemandParam;
import com.tny.game.base.item.xml.AliasCollectUtils;
import com.tny.game.base.log.LogName;
import com.tny.game.common.formula.FormulaHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleAward extends AbstractAward {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    /**
     * 改变方式
     */
    protected AlterType alterType;

    /**
     * 获得概率
     */
    public int probability;

    /**
     * 奖励物品id
     */
    protected String itemAlias;

    /**
     * 涉及Item
     */
    protected FormulaHolder itemAliasFx;

    /**
     * 奖励数量公式
     */
    protected FormulaHolder fx;

    /**
     * 参数
     */
    protected Map<DemandParam, FormulaHolder> paramMap;

    @Override
    public String getItemAlias(Map<String, Object> atrributeMap) {
        return this.itemAliasFx != null ? this.itemAliasFx.createFormula().putAll(atrributeMap).execute(String.class)
                : this.itemAlias;
    }

    @Override
    public int countNumber(ItemModel model, Map<String, Object> attributes) {
        return this.fx.createFormula()
                .putAll(attributes)
                .put(AbstractItemModel.ACTION_AWARD_MODEL_NAME, model)
                .execute(Integer.class);
    }

    @Override
    public Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap) {
        if (this.paramMap == null || this.paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<DemandParam, Object>();
        for (Entry<DemandParam, FormulaHolder> entry : this.paramMap.entrySet()) {
            try {
                paramMap.put(entry.getKey(), entry.getValue().createFormula().putAll(attributeMap).execute(Object.class));
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return paramMap;
    }

    @Override
    public AlterType getAlterType() {
        return this.alterType;
    }

    @Override
    public void init() {
        if (this.itemAlias == null) {
            if (this.itemAliasFx == null) {
                AliasCollectUtils.addAlias(null);
            }
        } else {
            AliasCollectUtils.addAlias(this.itemAlias);
        }
    }

    @Override
    public int getProbability() {
        return this.probability;
    }

    @Override
    public int compareTo(Probability o) {
        return this.getProbability() - o.getProbability();
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
