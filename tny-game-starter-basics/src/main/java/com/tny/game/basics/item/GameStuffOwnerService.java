package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.common.context.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/17 4:16 下午
 */
public class GameStuffOwnerService<SM extends ItemModel, S extends Stuff<SM>, O
		extends BaseStuffOwner<?, SM, S>>
		implements PrimaryStuffService<SM> {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameStuffOwnerService.class);

	private final StuffOwnerExplorer stuffOwnerExplorer;

	public GameStuffOwnerService(StuffOwnerExplorer stuffOwnerExplorer) {
		this.stuffOwnerExplorer = stuffOwnerExplorer;
	}

	private O getOwner(Warehouse warehouse, ItemType stuffType) {
		return warehouse.loadOwner(stuffType, (w, type) -> stuffOwnerExplorer.getOwner(w.getPlayerId(), type.getId()));
	}

	@Override
	public boolean isOverflow(Warehouse warehouse, SM model, AlterType check, Number number) {
		try {
			O owner = this.getOwner(warehouse, model.getItemType());
			if (owner == null) {
				LOGGER.warn("{}玩家没有 {} Owner对象", warehouse.getPlayerId(), model.getItemType());
				return true;
			}
			if (owner instanceof CountableStuffOwner) {
				CountableStuffOwner<?, SM, S> countableStuffOwner = as(owner);
				return countableStuffOwner.isOverflow(model, check, number);
			} else {
				LOGGER.warn("{} 玩家 {} Owner对象非 {}", warehouse.getPlayerId(), model.getItemType(), CountableStuffOwner.class);
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", warehouse.getPlayerId(), model.getItemType(), model.getId(), e);
		}
		return true;
	}

	@Override
	public boolean isNotEnough(Warehouse warehouse, SM model, AlterType check, Number number) {
		try {
			O owner = this.getOwner(warehouse, model.getItemType());
			if (owner == null) {
				LOGGER.warn("{}玩家没有 {} Owner对象", warehouse.getPlayerId(), model.getItemType());
				return true;
			}
			if (owner instanceof CountableStuffOwner) {
				CountableStuffOwner<?, SM, S> countableStuffOwner = as(owner);
				return countableStuffOwner.isNotEnough(model, check, number);
			} else {
				LOGGER.warn("{} 玩家 {} Owner对象非 {}", warehouse.getPlayerId(), model.getItemType(), CountableStuffOwner.class);
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", warehouse.getPlayerId(), model.getItemType(), model.getId(), e);
		}
		return true;
	}

	@Override
	public void deduct(Warehouse warehouse, TradeItem<SM> tradeItem, Action action, Attributes attributes) {
		if (!tradeItem.isValid()) {
			return;
		}
		ItemModel model = tradeItem.getItemModel();
		try {
			O owner = this.getOwner(warehouse, model.getItemType());
			if (owner != null) {
				synchronized (owner) {
					owner.deduct(tradeItem, action, attributes);
				}
			} else {
				LOGGER.warn("{}玩家没有 {} Owner对象", warehouse.getPlayerId(), model.getItemType());
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", warehouse.getPlayerId(), model.getItemType(), model.getId(), e);
		}
	}

	@Override
	public void reward(Warehouse warehouse, TradeItem<SM> tradeItem, Action action, Attributes attributes) {
		if (!tradeItem.isValid()) {
			return;
		}
		ItemModel model = tradeItem.getItemModel();
		try {
			O owner = this.getOwner(warehouse, model.getItemType());
			if (owner != null) {
				synchronized (owner) {
					owner.reward(tradeItem, action, attributes);
				}
			} else {
				LOGGER.warn("{}玩家没有 {} Owner对象", warehouse.getPlayerId(), model.getItemType());
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", warehouse.getPlayerId(), model.getItemType(), model.getId(), e);
		}
	}

}
