package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.base.item.xml.AliasCollectUtils;
import com.tny.game.base.log.LogName;
import com.tny.game.expr.FormulaHolder;
import org.slf4j.*;

import java.util.*;

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
        this.initParamMap();
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
