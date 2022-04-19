package com.tny.game.basics.item.behavior.plan;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;

import java.util.*;

/**
 * 抽象消费方案
 *
 * @author KGTny
 */
public class DefaultCostPlan extends BaseCostPlan {

    /**
     * 消费条件
     */
    protected List<BaseDemand> costList;

    @Override
    public Trade createTrade(long playerId, Action action, Map<String, Object> attributeMap) {
        return new SimpleTrade(action, TradeType.COST, this.createTradeItem(playerId, attributeMap));
    }

    private List<TradeItem<StuffModel>> createTradeItem(long playerId, Map<String, Object> attributeMap) {
        List<TradeItem<StuffModel>> itemList = new ArrayList<>();
        List<DemandResult> demandResultList = this
                .countAllDemandResults(playerId, this.costList, ItemsImportKey.$COST_PLAN_DEMAND_PARAMS, attributeMap);
        for (DemandResult demandResult : demandResultList) {
            if (!(demandResult instanceof CostDemandResult)) {
                continue;
            }
            CostDemandResult result = (CostDemandResult)demandResult;
            itemList.add(new SimpleTradeItem<>(result.getId(), result.getStuffModel(),
                    result.getExpectValue(Number.class).intValue(), result.getAlterType(), demandResult.getParamMap()));
        }
        return itemList;
    }

    @Override
    public CostList getCostList(long playerId, Action action, Map<String, Object> attributeMap) {
        return new SimpleCostList(action, this.createTradeItem(playerId, attributeMap));
    }

    @Override
    public List<DemandResult> countDemandResultList(long playerId, Map<String, Object> attributeMap) {
        return this.countAllDemandResults(playerId, this.costList, ItemsImportKey.$COST_PLAN_DEMAND_PARAMS, attributeMap);
    }

    @Override
    public DemandResultCollector tryToDo(long playerId, boolean tryAll, DemandResultCollector collector, Map<String, Object> attributeMap) {
        return this.checkResult(playerId, this.demandList, tryAll, collector, ItemsImportKey.$COST_PLAN_DEMAND_PARAMS, attributeMap);
    }

    //	@Override
    //	public AlertType getAlertType() {
    //		return alertType;
    //	}

    @Override
    public void init(ItemModel itemModel, ItemModelContext context) {
        super.init(itemModel, context);
        if (this.costList == null) {
            this.costList = Collections.emptyList();
        }
        this.demandList = this.costList = Collections.unmodifiableList(this.costList);
        for (BaseDemand demand : this.costList) {
            demand.init(itemModel, context);
        }
        this.costList = Collections.unmodifiableList(this.costList);
    }

}
