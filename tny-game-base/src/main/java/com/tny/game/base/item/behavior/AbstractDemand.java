package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.GameRuningException;
import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ItemsImportKey;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.xml.XMLDemand.TradeDemandType;
import com.tny.game.base.log.LogName;
import com.tny.game.common.formula.Formula;
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
     * 改变类型
     */
    protected AlterType alertType;

    /**
     * 参数
     */
    protected Map<DemandParam, FormulaHolder> paramMap;

    protected ItemExplorer itemExplorer;

    protected ModelExplorer itemModelExplorer;

    public AlterType getAlertType() {
        return alertType;
    }

    @Override
    public String getItemAlias(Map<String, Object> attributeMap) {
        return this.itemAliasFx != null ? this.itemAliasFx.createFormula().putAll(attributeMap).execute(String.class) : this.itemAlias;
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
        return demandModel.currentFormula().putAll(attributeMap).execute(Object.class);
    }

    @Override
    public DemandResult checkDemandResult(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return null;
        long id = 0;
        ItemModel demandModel = this.getItemModel(alias);
        this.setAttrMap(playerID, alias, attributeMap);
        Formula currentFormula = getCurrentFormula(demandModel);
        Object current = currentFormula != null ? currentFormula.putAll(attributeMap).execute(Object.class) : null;
        Object expect = this.expect != null ? this.expect.createFormula().putAll(attributeMap).execute(Object.class) : null;
        boolean satisfy = this.checkSatisfy(current, expect, demandModel, attributeMap);
        Map<DemandParam, Object> paramMap = this.countDemandParam(attributeMap);
        if (this.getDemandType() == TradeDemandType.COST_DEMAND_GE)
            return new CostDemandResult(id, demandModel, this.demandType, current, expect, satisfy, this.alertType, paramMap);
        else
            return new DemandResult(id, demandModel, this.demandType, current, expect, satisfy, paramMap);
    }

    private Formula getCurrentFormula(ItemModel demandModel) {
        if (this.current != null) {
            return this.current.createFormula();
        } else {
            if (this.demandType.isCost())
                return demandModel.currentFormula();
        }
        return null;
    }

    private Formula getDemandFormula(ItemModel demandModel) {
        if (this.fx != null) {
            return this.fx.createFormula();
        } else {
            if (this.demandType.isCost())
                return demandModel.demandFormula();
        }
        return null;
    }

    @Override
    public boolean isSatisfy(long playerID, Map<String, Object> attributeMap) {
        return this.checkDemandResult(playerID, attributeMap).isSatisfy();
    }

    protected boolean checkSatisfy(Object current, Object expect, ItemModel demandModel, Map<String, Object> attribute) {
        Formula fxFormula = getDemandFormula(demandModel);
        return fxFormula.putAll(attribute).put(CURRENT_VALUE, current).put(EXPECT_VALUE, expect).execute(Boolean.class);
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
        ItemModel model = this.itemModelExplorer.getModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        return model;
    }

    private ItemModel setAttrMap(long playerID, String alias, Map<String, Object> attributeMap) {
        ItemModel model = this.getItemModel(alias);
        Item<?> item;
        attributeMap.put(DEMAND_MODEL, model);
        if (itemExplorer.hasItemMannager(model.getItemType())) {
            item = this.itemExplorer.getItem(playerID, model.getID());
            if (this.name != null)
                attributeMap.put(this.name, item);
            attributeMap.put(alias, item);
            attributeMap.put(DEMAND_ITEM, item);
        }
        return model;
    }

    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.alertType == null)
            this.alertType = AlterType.CHECK;
        if (this.itemAlias == null) {
            this.itemAlias = itemModel.getAlias();
        }
    }

}
