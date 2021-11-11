package com.tny.game.basics.item;

import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import com.tny.game.common.result.*;
import org.slf4j.*;

import javax.annotation.Resource;
import java.util.function.BiConsumer;

import static com.tny.game.basics.common.BasicResultCode.*;
import static com.tny.game.basics.item.behavior.TradeType.*;

public class GameWarehouseService implements WarehouseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameWarehouseService.class);

	@Resource
	private GameWarehouseManager gameWarehouseManager;

	@Override
	public void consume(long playerId, Trade trade, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			this.doHandleTrade(playerId, trade, warehouse::consume, entries);
		}
	}

	@Override
	public void consume(long playerId, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			doHandleTrade(playerId, tradeItem, action, COST, warehouse::consume, entries);
		}
	}

	@Override
	public void receive(long playerId, Trade trade, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			this.doHandleTrade(playerId, trade, warehouse::receive, entries);
		}
	}

	@Override
	public void receive(long playerId, TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			doHandleTrade(playerId, tradeItem, action, AWARD, warehouse::receive, entries);
		}
	}

	@Override
	public void deal(long playerId, Trade trade, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			switch (trade.getTradeType()) {
				case AWARD:
					this.doHandleTrade(playerId, trade, warehouse::receive, entries);
					break;
				case COST:
					this.doHandleTrade(playerId, trade, warehouse::consume, entries);
					break;
			}
		}
	}

	@Override
	public void deal(long playerId, TryToDoResult result, AttrEntry<?>... entries) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			this.doHandleTrade(playerId, result.getCostTrade(), warehouse::consume, entries);
			this.doHandleTrade(playerId, result.getAwardTrade(), warehouse::receive, entries);
		}
	}

	private void doHandleTrade(long playerId, TradeItem<?> tradeItem, Action action, TradeType tradeType, TradeWithTradeItem fn,
			AttrEntry<?>... entries) {
		try {
			fn.trade(tradeItem, action, entries);
		} catch (Exception e) {
			LOGGER.error("玩家 {} {} {}物品 {} 异常", playerId, action, tradeType, tradeItem, e);
		}
	}

	private void doHandleTrade(long playerId, Trade trade, BiConsumer<Trade, AttrEntry<?>[]> fn, AttrEntry<?>... entries) {
		try {
			fn.accept(trade, entries);
		} catch (Exception e) {
			LOGGER.error("玩家 {} {} {}物品 {} 异常", playerId, trade.getAction(), trade.getTradeType(), trade, e);
		}
	}

	public DoneResult<Boolean> checkTradeBound(long playerId, Trade trade) {
		GameWarehouse warehouse = this.gameWarehouseManager.getWarehouse(playerId);
		if (warehouse != null) {
			boolean award = trade.getTradeType() == AWARD;
			for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
				if (!(item.getItemModel() instanceof CountableStuffModel)) {
					continue;
				}
				CountableStuffModel<?> stuffModel = item.getItemModel();
				if (stuffModel.isNumberLimit()) {
					GameCountableStuffOwner<CountableStuffModel<?>, ?, ?> owner = warehouse.getOwner(stuffModel.getItemType());
					if (award) {
						if (owner.isOverUpperLimit(stuffModel, AlterType.CHECK, item.getNumber())) {
							return DoneResults.failure(ItemResultCode.FULL_NUMBER);
						}
					} else {
						if (owner.isOverLowerLimit(stuffModel, AlterType.CHECK, item.getNumber())) {
							return DoneResults.failure(ItemResultCode.LACK_NUMBER);
						}
					}
				}
			}
			return DoneResults.success(true);
		}
		return DoneResults.failure(ITEM_WAREHOUSE_NO_EXIST);
	}

	interface TradeWithTradeItem {

		void trade(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries);

	}

}
