package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.exception.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.listener.*;
import com.tny.game.common.context.*;
import com.tny.game.common.result.*;

import java.util.*;

import static com.tny.game.basics.item.behavior.TradeType.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/17 2:34 下午
 */
public class GameTradeService implements TradeService {

	private final PrimaryStuffService<?> primaryService;

	private final WarehouseManager warehouseManager;

	private final Map<ItemType, StuffService<?>> serviceMap = ImmutableMap.of();

	public GameTradeService(WarehouseManager warehouseManager, PrimaryStuffService<?> primaryService, List<StuffService<?>> services) {
		this.warehouseManager = warehouseManager;
		this.primaryService = primaryService;
		Map<ItemType, StuffService<?>> serviceMap = new HashMap<>();
		for (StuffService<?> service : services) {
			for (ItemType type : service.getDealStuffTypes()) {
				StuffService<?> old = serviceMap.put(type, service);
				if (old != null) {
					throw new IllegalArgumentException(
							format("{} 与 {} 都可以处理 Stuff ItemType {}", old, service, type));
				}
			}
		}
	}

	@Override
	public void deal(long playerId, Trade trade, AttrEntry<?>... entries) {
		Warehouse warehouse = warehouseManager.load(playerId);
		if (warehouse == null) {
			return;
		}
		switch (trade.getTradeType()) {
			case AWARD:
				this.reward(warehouse, trade, entries);
				break;
			case COST:
				this.deduct(warehouse, trade, entries);
				break;
		}
	}

	@Override
	public void deal(long playerId, TryToDoResult result, AttrEntry<?>... entries) {
		Trade costTrade = result.getCostTrade();
		Warehouse warehouse = warehouseManager.load(playerId);
		if (warehouse == null) {
			return;
		}
		if (costTrade != null && !costTrade.isEmpty()) {
			this.deduct(warehouse, costTrade, entries);
		}
		Trade awardTrade = result.getCostTrade();
		if (costTrade != null && !costTrade.isEmpty()) {
			this.reward(warehouse, awardTrade, entries);
		}
	}

	@Override
	public ResultCode checkBound(long playerId, Trade trade) {
		boolean award = trade.getTradeType() == AWARD;
		Warehouse warehouse = warehouseManager.load(playerId);
		if (warehouse == null) {
			return ItemResultCode.ROLE_NO_EXIST;
		}
		for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
			if (!(item.getItemModel() instanceof StuffModel)) {
				continue;
			}
			StuffModel<?> stuffModel = item.getItemModel();
			if (stuffModel.isNumberLimit()) {
				StuffService<StuffModel<?>> service = stuffService(stuffModel.getItemType());
				if (award) {
					if (service.isOverflow(warehouse, stuffModel, AlterType.CHECK, item.getNumber())) {
						return ItemResultCode.FULL_NUMBER;
					}
				} else {
					if (service.isNotEnough(warehouse, stuffModel, AlterType.CHECK, item.getNumber())) {
						return ItemResultCode.LACK_NUMBER;
					}
				}
			}
		}
		return ResultCode.SUCCESS;
	}

	private <T extends ItemModel> StuffService<T> stuffService(ItemType itemType) {
		if (serviceMap.isEmpty()) {
			return as(primaryService);
		}
		StuffService<T> service = as(serviceMap.get(itemType));
		if (service != null) {
			return service;
		}
		return as(primaryService);
	}

	private void deduct(Warehouse warehouse, Trade trade, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
			this.doDeduct(warehouse, item, trade.getAction(), attributes);
		}
		TradeEvents.CONSUME_EVENT.notify(warehouse, trade, attributes);
	}

	private void doDeduct(Warehouse warehouse, TradeItem<ItemModel> item, Action action, Attributes attributes) {
		if (!(item.getItemModel() instanceof StuffModel)) {
			return;
		}
		ItemModel stuffModel = item.getItemModel();
		StuffService<ItemModel> service = stuffService(stuffModel.getItemType());
		service.deduct(warehouse, item, action, attributes);
	}

	private void reward(Warehouse warehouse, Trade trade, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		for (TradeItem<ItemModel> item : trade.getAllTradeItem()) {
			this.doReward(warehouse, item, trade.getAction(), attributes);
		}
		TradeEvents.REWARD_EVENT.notify(warehouse, trade, attributes);
	}

	private void doReward(Warehouse warehouse, TradeItem<ItemModel> item, Action action, Attributes attributes) {
		if (!(item.getItemModel() instanceof StuffModel)) {
			return;
		}
		ItemModel stuffModel = item.getItemModel();
		StuffService<ItemModel> service = stuffService(stuffModel.getItemType());
		service.reward(warehouse, item, action, attributes);
	}

}