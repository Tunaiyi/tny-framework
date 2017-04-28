package com.tny.game.base.item.behavior.plan;

import com.tny.game.base.item.AlterType;
import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ItemModel;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.Trade;
import com.tny.game.base.item.TradeItem;
import com.tny.game.base.item.behavior.AbstractCostPlan;
import com.tny.game.base.item.behavior.AbstractDemand;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.CostList;
import com.tny.game.base.item.behavior.DemandResult;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleCostList;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 抽象消费方案
 *
 * @author KGTny
 */
public class SimpleCostPlan extends AbstractCostPlan {

    /**
     * 消费条件
     */
    protected List<AbstractDemand> costList;

    /**
     * 交易方式
     */
    protected AlterType alertType;

    @Override
    public Trade createTrade(long playerID, Action action, Map<String, Object> attributeMap) {
        return new SimpleTrade(action, TradeType.COST, this.createTradeItem(playerID, action, attributeMap));
    }

    private List<TradeItem<ItemModel>> createTradeItem(long playerID, Action action, Map<String, Object> attributeMap) {
        List<TradeItem<ItemModel>> itemList = new ArrayList<TradeItem<ItemModel>>();
        List<DemandResult> demandResultList = this.countDemandResultList(playerID, this.costList, attributeMap);
        for (DemandResult result : demandResultList) {
            itemList.add(new SimpleTradeItem<ItemModel>(result.getItemModel(), result.getExpectValue(Number.class).intValue(), this.alertType, result.getParamMap()));
        }
        return itemList;
    }

    @Override
    public CostList getCostList(long playerID, Action action, Map<String, Object> attributeMap) {
        return new SimpleCostList(action, this.createTradeItem(playerID, action, attributeMap));
    }

    @Override
    public List<DemandResult> countDemandResultList(long playerID, Map<String, Object> attributeMap) {
        return this.countDemandResultList(playerID, this.costList, attributeMap);
    }

    @Override
    public List<DemandResult> tryToDo(long playerID, boolean tryAll, Map<String, Object> attributeMap) {
        return this.checkResult(playerID, this.demandList, tryAll, attributeMap);
    }

//	@Override
//	public AlertType getAlertType() {
//		return alertType;
//	}

    @Override
    public void init(ItemModel itemModel, ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        super.init(itemModel, itemExplorer, itemModelExplorer);
        if (this.costList == null)
            this.costList = new ArrayList<AbstractDemand>(0);
        this.demandList = this.costList = Collections.unmodifiableList(this.costList);
        this.itemExplorer = itemExplorer;
        this.itemModelExplorer = itemModelExplorer;
        if (this.alertType == null)
            this.alertType = AlterType.CHECK;
        for (AbstractDemand demand : this.costList) {
            demand.init(itemModel, itemExplorer, this.itemModelExplorer);
        }
        this.costList = Collections.unmodifiableList(this.costList);
    }

}
