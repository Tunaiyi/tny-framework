package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemModelExplorer;
import com.tny.game.base.item.ItemsImportKey;
import com.tny.game.base.log.LogName;
import com.tny.game.common.formula.FormulaHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 事物执行行为操作的条件
 *
 * @author KGTny
 */
public abstract class AbstractDemand implements Demand, ItemsImportKey {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    /**
     * 条件所涉及的item ID
     */
    protected String itemAlias;

    /**
     * 涉及Item
     */
    protected FormulaHolder itemAliasFx;

    /**
     * 条件涉及的item 别名，用于表达式
     */
    protected String name;

    /**
     * 类型
     */
    protected DemandType demandType;

    /**
     * 当前值表达式
     */
    protected FormulaHolder current;

    /**
     * 希望值表达式
     */
    protected FormulaHolder expect;

    /**
     * 条件判断表达式
     */
    protected FormulaHolder fx;

    /**
     * 参数
     */
    protected Map<DemandParam, FormulaHolder> paramMap;

    protected ItemExplorer itemExplorer;

    protected ItemModelExplorer itemModelExplorer;

    @Override
    public String getItemAlias(Map<String, Object> attributeMap) {
        return this.itemAliasFx != null ? this.itemAliasFx.createFormula().putAll(attributeMap).execute(String.class)
                : this.itemAlias;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DemandType getDemandType() {
        return this.demandType;
    }

    @Override
    public boolean isSatisfy(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return true;
        ItemModel demandModel = this.setAttrMap(playerID, alias, attributeMap);
        Number current = demandModel.currentFormula().createFormula().putAll(attributeMap).execute(Number.class);
        Number expect = this.expect.createFormula().putAll(attributeMap).execute(Number.class);
        return this.isSatisfy0(current, expect, attributeMap);
    }

    protected boolean isSatisfy0(Object current, Object expect, Map<String, Object> attribute) {
        return this.fx.createFormula().putAll(attribute).put(CURRENT_VALUE, current).put(EXPECT_VALUE, expect).execute(Boolean.class);
    }

    @Override
    public Object countExpectValue(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return null;
        this.setAttrMap(playerID, alias, attributeMap);
        return this.expect.createFormula().putAll(attributeMap).execute(Object.class);
    }

    @Override
    public Object countCurrentValue(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return null;
        ItemModel demandModel = this.setAttrMap(playerID, alias, attributeMap);
        return demandModel.currentFormula().createFormula().putAll(attributeMap).execute(Object.class);
    }

    @Override
    public DemandResult checkDemandResult(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return null;
        long id = 0;
        ItemModel demandModel = this.getItemModel(alias);
        this.setAttrMap(playerID, alias, attributeMap);
        Object current = null;
        Object expect = null;
        if (this.demandType.isCost()) {
            current = demandModel.currentFormula().createFormula().putAll(attributeMap).execute(Object.class);
        } else {
            if (this.current != null)
                current = this.current.createFormula().putAll(attributeMap).execute(Object.class);
        }
        if (this.expect != null)
            expect = this.expect.createFormula().putAll(attributeMap).execute(Object.class);
        boolean satisfy = this.isSatisfy0(current, expect, attributeMap);
        Map<DemandParam, Object> paramMap = this.countDemandParam(attributeMap);
        return new DemandResult(id, demandModel, this.demandType, current, expect, satisfy, paramMap);
    }

    private Map<DemandParam, Object> countDemandParam(Map<String, Object> attributeMap) {
        if (this.paramMap == null || this.paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<DemandParam, Object> paramMap = new HashMap<>();
        for (Entry<DemandParam, FormulaHolder> entry : this.paramMap.entrySet()) {
            try {
                paramMap.put(entry.getKey(), entry.getValue().createFormula().putAll(attributeMap).execute(Object.class));
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return paramMap;
    }

    private ItemModel getItemModel(String alias) {
        ItemModel model = this.itemModelExplorer.getItemModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        return model;
    }

    private ItemModel setAttrMap(long playerID, String alias, Map<String, Object> attributeMap) {
        ItemModel model = this.getItemModel(alias);
        Item<?> item;
        attributeMap.put(DEMAND_MODEL, model);
        if (model.getItemType().hasEntity()) {
            item = this.itemExplorer.getItem(playerID, model.getID());
            if (this.name != null)
                attributeMap.put(this.name, item);
            attributeMap.put(alias, item);
            attributeMap.put(DEMAND_ITEM, item);
        }
        return model;
    }

    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ItemModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.itemAlias == null) {
            this.itemAlias = itemModel.getAlias();
        }
    }

}
