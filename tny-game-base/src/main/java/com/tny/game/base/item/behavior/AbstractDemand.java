package com.tny.game.base.item.behavior;

import com.tny.game.base.exception.*;
import com.tny.game.base.item.*;
import com.tny.game.base.item.xml.AliasCollectUtils;
import com.tny.game.base.item.xml.XMLDemand.TradeDemandType;
import com.tny.game.expr.*;

import java.util.Map;

/**
 * 事物执行行为操作的条件
 *
 * @author KGTny
 */
public abstract class AbstractDemand extends DemandParamsObject implements Demand, ItemsImportKey {

    /**
     * 条件所涉及的item ID
     */
    protected String itemAlias;

    /**
     * 涉及Item
     */
    protected ExprHolder itemAliasFx;

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
    protected ExprHolder current;

    /**
     * 希望值表达式
     */
    protected ExprHolder expect;

    /**
     * 条件判断表达式
     */
    protected ExprHolder fx;

    /**
     * 改变类型
     */
    protected AlterType alertType;

    public AlterType getAlertType() {
        return alertType;
    }

    @Override
    public String getItemAlias(Map<String, Object> attributeMap) {
        return this.itemAliasFx != null ? this.itemAliasFx.createExpr().putAll(attributeMap).execute(String.class) : this.itemAlias;
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
        this.countAndSetDemandParams($PARAMS, attributeMap);
        return this.expect.createExpr().putAll(attributeMap).execute(Object.class);
    }

    @Override
    public Object countCurrentValue(long playerID, Map<String, Object> attributeMap) {
        String alias = this.getItemAlias(attributeMap);
        if (alias == null)
            return null;
        ItemModel demandModel = this.setAttrMap(playerID, alias, attributeMap);
        this.countAndSetDemandParams($PARAMS, attributeMap);
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
        Map<DemandParam, Object> paramMap = this.countAndSetDemandParams($PARAMS, attributeMap);
        Expr currentFormula = getCurrentFormula(demandModel);
        Object current = currentFormula != null ? currentFormula.putAll(attributeMap).execute(Object.class) : null;
        Object expect = this.expect != null ? this.expect.createExpr().putAll(attributeMap).execute(Object.class) : null;
        boolean satisfy = this.checkSatisfy(current, expect, demandModel, attributeMap);
        if (this.getDemandType() == TradeDemandType.COST_DEMAND_GE)
            return new CostDemandResult(id, demandModel, this.demandType, current, expect, satisfy, this.alertType, paramMap);
        else
            return new DemandResult(id, demandModel, this.demandType, current, expect, satisfy, paramMap);
    }

    private Expr getCurrentFormula(ItemModel demandModel) {
        if (this.current != null) {
            return this.current.createExpr();
        } else {
            if (this.demandType.isCost())
                return demandModel.currentFormula();
        }
        return null;
    }

    private Expr getDemandFormula(ItemModel demandModel) {
        if (this.fx != null) {
            return this.fx.createExpr();
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
        Expr fxFormula = getDemandFormula(demandModel);
        return fxFormula.putAll(attribute).put(CURRENT_VALUE, current).put(EXPECT_VALUE, expect).execute(Boolean.class);
    }

    private ItemModel getItemModel(String alias) {
        ModelExplorer itemModelExplorer = context.getItemModelExplorer();
        ItemModel model = itemModelExplorer.getModelByAlias(alias);
        if (model == null)
            throw new GameRuningException(ItemResultCode.MODEL_NO_EXIST, alias);
        return model;
    }

    private ItemModel setAttrMap(long playerID, String alias, Map<String, Object> attributeMap) {
        ItemModel model = this.getItemModel(alias);
        Item<?> item;
        attributeMap.put(DEMAND_MODEL, model);
        ItemExplorer itemExplorer = context.getItemExplorer();
        if (itemExplorer.hasItemManager(model.getItemType())) {
            item = itemExplorer.getItem(playerID, model.getID());
            if (this.name != null)
                attributeMap.put(this.name, item);
            attributeMap.put(alias, item);
            attributeMap.put(DEMAND_ITEM, item);
        }
        attributeMap.putIfAbsent(DEMAND_ITEM, null);
        return model;
    }

    public void init(ItemModel itemModel, ItemModelContext context) {
        this.init(context);
        if (this.alertType == null)
            this.alertType = AlterType.CHECK;
        if (this.itemAlias == null) {
            this.itemAlias = itemModel.getAlias();
        }
        if (this.demandType == null)
            this.demandType = TradeDemandType.COST_DEMAND_GE;
        if (this.itemAlias == null) {
            if (this.itemAliasFx == null)
                AliasCollectUtils.addAlias(null);
        } else {
            AliasCollectUtils.addAlias(this.itemAlias);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [demandType=" + this.demandType + ", expect=" + this.expect + "]";
    }

}
