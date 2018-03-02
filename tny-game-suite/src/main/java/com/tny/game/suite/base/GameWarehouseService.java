package com.tny.game.suite.base;

import com.tny.game.base.exception.ItemResultCode;
import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.context.AttrEntry;
import com.tny.game.common.utils.*;
import com.tny.game.suite.utils.SuiteResultCode;
import org.slf4j.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.BiConsumer;

import static com.tny.game.base.item.behavior.TradeType.*;
import static com.tny.game.suite.SuiteProfiles.GAME;

@Component
@Profile(GAME)
public class GameWarehouseService implements WarehouseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameWarehouseService.class);

    @Resource
    private GameWarehouseManager gameWarehouseManager;

    /**
     * 异步发奖励
     * 慎用~不一定发成功
     */
//    public void asynTrade(long playerID, Trade trade) {
//        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
//        if (warehouse != null) {
//            try {
//                warehouse.pushTrade(trade);
//                this.gameWarehouseManager.saveWarehouse(warehouse);
//            } catch (Exception e) {
//                LOGGER.error("玩家 {} 添加异步Trade {} 异常", playerID, trade, e);
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
    public void consume(long playerID, Trade trade, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            this.doHandleTrade(playerID, trade, warehouse::consume, entries);
    }

    @Override
    public void consume(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            doHandleTrade(playerID, tradeItem, action, COST, warehouse::consume, entries);
    }

    @Override
    public void receive(long playerID, Trade trade, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            this.doHandleTrade(playerID, trade, warehouse::receive, entries);
    }

    @Override
    public void receive(long playerID, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null)
            doHandleTrade(playerID, tradeItem, action, AWARD, warehouse::receive, entries);
    }

    @Override
    public void deal(long playerID, Trade trade, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null) {
            switch (trade.getTradeType()) {
                case AWARD:
                    this.doHandleTrade(playerID, trade, warehouse::receive, entries);
                    break;
                case COST:
                    this.doHandleTrade(playerID, trade, warehouse::consume, entries);
                    break;
            }
        }
    }

    @Override
    public void deal(long playerID, TryToDoResult result, AttrEntry<?>... entries) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null) {
            this.doHandleTrade(playerID, result.getCostTrade(), warehouse::consume, entries);
            this.doHandleTrade(playerID, result.getAwardTrade(), warehouse::receive, entries);
        }
    }

    private void doHandleTrade(long playerID, TradeItem<?> tradeItem, Action action, TradeType tradeType, TradeWithTradeItem fn, AttrEntry<?>... entries) {
        try {
            fn.trade(tradeItem, action, entries);
        } catch (Exception e) {
            LOGGER.error("玩家 {} {} {}物品 {} 异常", playerID, action, tradeType, tradeItem, e);
        }
    }


    private void doHandleTrade(long playerID, Trade trade, BiConsumer<Trade, AttrEntry<?>[]> fn, AttrEntry<?>... entries) {
        try {
            fn.accept(trade, entries);
        } catch (Exception e) {
            LOGGER.error("玩家 {} {} {}物品 {} 异常", playerID, trade.getAction(), trade.getTradeType(), trade, e);
        }
    }


    @SuppressWarnings("unchecked")
    public DoneResult<Boolean> checkTradeBound(long playerID, Trade trade) {
        GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerID);
        if (warehouse != null) {
            boolean award = trade.getTradeType() == AWARD;
            for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
                if (!(item.getItemModel() instanceof CountableStuffModel))
                    continue;
                CountableStuffModel stuffModel = item.getItemModel();
                if (stuffModel.isNumberLimit()) {
                    CountableStuffOwner owner = warehouse.getOwner(stuffModel.getItemType(), GameCountableStuffOwner.class);
                    if (award) {
                        if (owner.isOverUpperLimit(stuffModel, AlterType.CHECK, item.getNumber()))
                            return DoneResults.fail(ItemResultCode.FULL_NUMBER);
                    } else {
                        if (owner.isOverLowerLimit(stuffModel, AlterType.CHECK, item.getNumber()))
                            return DoneResults.fail(ItemResultCode.LACK_NUMBER);
                    }
                }
            }
            return DoneResults.succ(true);
        }
        return DoneResults.fail(SuiteResultCode.ITEM_WAREHOUSE_NO_EXIST);
    }

    interface TradeWithTradeItem {

        void trade(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

    }

}
