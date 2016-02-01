package com.tny.game.suite.base;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.common.context.AttributeEntry;
import com.tny.game.suite.utils.SuiteResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@Profile({"suite.base", "suite.all"})
public class GameWarehouseService implements WarehouseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameWarehouseService.class);

    @Autowired
    private GameWarehouseManager gameWarehouseManager;

    @Autowired
    protected GameExplorer gameExplorer;

    /**
     * 异步发奖励
     * 慎用~不一定发成功
     */
//    public void asynTrade(long playerID, Trade result) {
//        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
//        if (warehouse != null) {
//            try {
//                warehouse.pushTrade(result);
//                this.gameWarehouseManager.saveWarehouse(warehouse);
//            } catch (Exception e) {
//                LOGGER.error("玩家 {} 添加异步Trade {} 异常", playerID, result, e);
//            }
//        }
//    }

    /**
     * 异步发奖励
     * 慎用~不一定发成功
     */
//    public void checkAsynTrade(long playerID) {
//        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
//        Trade trade = null;
//        if (warehouse != null) {
//            if (warehouse.isTradesEmpty())
//                return;
//            try {
//                while ((trade = warehouse.popTrade()) != null) {
//                    if (trade.getTradeType() == TradeType.AWARD) {
//                        this.doHandleTrade(playerID, trade, warehouse::receive);
//                    } else {
//                        this.doHandleTrade(playerID, trade, warehouse::consume);
//                    }
//                }
//                this.gameWarehouseManager.saveWarehouse(warehouse);
//            } catch (Exception e) {
//                LOGGER.error("玩家 {} 异步处理Trade {} 异常", playerID, trade, e);
//            }
//        }
//    }
    @Override
    public DoneResult<DealedResult> consume(long playerID, Trade result, AttributeEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            return this.doHandleTrade(playerID, result, warehouse::consume, entries);
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    @Override
    public DoneResult<DealedResult> consume(long playerID, TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            doHandleTrade(playerID, tradeItem, action, TradeType.COST, warehouse::consume, entries);
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    @Override
    public DoneResult<DealedResult> receive(long playerID, Trade result, AttributeEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            return this.doHandleTrade(playerID, result, warehouse::receive, entries);
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    @Override
    public DoneResult<DealedResult> receive(long playerID, TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            doHandleTrade(playerID, tradeItem, action, TradeType.AWARD, warehouse::receive, entries);
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    private DoneResult<DealedResult> doHandleTrade(long playerID, TradeItem<?> tradeItem, Action action, TradeType tradeType, TradeWithTradeItem fn, AttributeEntry<?>... entries) {
        try {
            DealedResult dealedResult = fn.trade(tradeItem, action, entries);
            return DoneResult.succ(dealedResult);
        } catch (Exception e) {
            LOGGER.error("玩家 {} {} {}物品 {} 异常", playerID, action, tradeType, tradeItem, e);
        }
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_TRADE_FAILED);
    }


    private DoneResult<DealedResult> doHandleTrade(long playerID, Trade result, BiFunction<Trade, AttributeEntry<?>[], DealedResult> fn, AttributeEntry<?>... entries) {
        try {
            DealedResult dealedResult = fn.apply(result, entries);
            return DoneResult.succ(dealedResult);
        } catch (Exception e) {
            LOGGER.error("玩家 {} {} {}物品 {} 异常", playerID, result.getAction(), result.getTradeType(), result, e);
        }
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_TRADE_FAILED);
    }


    @Override
    @SuppressWarnings("unchecked")
    public DoneResult<Boolean> checkTradeBound(long playerID, Trade trade) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null) {
            boolean award = trade.getTradeType() == TradeType.AWARD;
            for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
                if (!(item.getItemModel() instanceof CountableStuffModel))
                    continue;
                CountableStuffModel stuffModel = (CountableStuffModel) item.getItemModel();
                if (stuffModel.isNumberLimit()) {
                    CountableStuffOwner owner = warehouse.getOwner(stuffModel.getItemType(), GameCountableStuffOwner.class);
                    if (award) {
                        if (owner.isOverUpperLimit(stuffModel, AlterType.CHECK, item.getNumber()))
                            return DoneResult.fail(ItemResultCode.FULL_NUMBER);
                    } else {
                        if (owner.isOverLowerLimit(stuffModel, AlterType.CHECK, item.getNumber()))
                            return DoneResult.fail(ItemResultCode.LACK_NUMBER);
                    }
                } else {
                    continue;
                }
            }
            return DoneResult.succ(true);
        }
        return DoneResult.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    static interface TradeWithTradeItem {

        DealedResult trade(TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries);

    }

}
