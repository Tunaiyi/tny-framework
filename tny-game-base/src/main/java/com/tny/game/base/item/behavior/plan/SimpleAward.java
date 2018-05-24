package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.base.item.xml.AliasCollectUtils;
import com.tny.game.base.log.LogName;
import com.tny.game.expr.FormulaHolder;
import org.slf4j.*;

import java.util.*;
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
    public FormulaHolder probability;

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
    public String getItemAlias(Map<String, Object> attributeMap) {
        return this.itemAliasFx != null ? this.itemAliasFx.createFormula().putAll(attributeMap).execute(String.class)
                : this.itemAlias;
    }

    @Override
    public Number countNumber(ItemModel model, Map<String, Object> attributes) {
        return this.fx.createFormula()
                .putAll(attributes)
                .put(AbstractItemModel.ACTION_AWARD_MODEL_NAME, model)
                .execute(Number.class);
    }

    @Override
    public Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap) {
        if (this.paramMap == null || this.paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<>();
        for (Entry<DemandParam, FormulaHolder> entry : this.paramMap.entrySet()) {
            try {
                Object value = entry.getValue().createFormula().putAll(attributeMap).execute(Object.class);
                paramMap.put(entry.getKey(), value);
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
    public int getProbability(Map<String, Object> attributeMap) {
        return this.probability.createFormula()
                .putAll(attributeMap)
                .execute(Integer.class);
    }

//    @Override
//    public int compareTo(Probability o) {
//        return this.getProbability() - o.getProbability();
//    }

//    @Override
//    public int getPriority() {
//        return 0;
//    }
}
