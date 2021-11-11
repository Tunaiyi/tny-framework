package com.tny.game.basics.item;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.item.behavior.simple.*;
import com.tny.game.basics.item.listener.*;
import com.tny.game.basics.log.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.context.*;
import org.slf4j.*;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractWarehouse<O extends StuffOwner<?, ?>> implements Warehouse<O>, AnyOwned {

	protected final static Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

	protected long playerId;

	private StuffOwnerExplorer stuffOwnerExplorer;

	private final Map<ItemType, WeakReference<? extends O>> stuffOwnerMap = new CopyOnWriteMap<>();

	protected AbstractWarehouse(long playerId, StuffOwnerExplorer stuffOwnerExplorer) {
		this.playerId = playerId;
		this.stuffOwnerExplorer = stuffOwnerExplorer;
	}

	@Override
	public long getId() {
		return this.playerId;
	}

	@Override
	public long getPlayerId() {
		return this.playerId;
	}

	@Override
	public <SO extends O> SO getOwner(ItemType itemType) {
		WeakReference<? extends O> reference = this.stuffOwnerMap.get(itemType);
		O owner = reference != null ? reference.get() : null;
		if (owner != null) {
			return (SO)owner;
		}
		owner = this.stuffOwnerExplorer.getOwner(this.playerId, itemType.getId());
		if (owner == null) {
			throw new NullPointerException(MessageFormat.format("{0} 玩家 {1} {2} owner的对象为 null", this.playerId, itemType));
		}
		reference = new WeakReference<>(owner);
		this.stuffOwnerMap.put(itemType, reference);
		return (SO)owner;
	}

	@Override
	public <I extends Item<?>> I getItemById(ItemType itemType, long id) {
		StuffOwner<?, ?> owner = this.getOwner(itemType);
		if (owner == null) {
			return null;
		}
		return (I)owner.getItemById(id);
	}

	@Override
	public <I extends Item<?>> List<I> getItemByItemId(ItemType itemType, int itemId) {
		StuffOwner<?, ?> owner = this.getOwner(itemType);
		if (owner == null) {
			return Collections.emptyList();
		}
		return owner.getItemsByItemId(itemId)
				.stream()
				.map(item -> (I)item)
				.collect(Collectors.toList());
	}

	protected void consume(Trade result, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		for (TradeItem tradeItem : result.getAllTradeItem()) {
			this.consumeTrade(tradeItem, result.getAction(), attributes);
		}
		TradeEvents.CONSUME_EVENT.notify(this, result.getAction(), result, attributes);
	}

	protected void consume(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		this.consumeTrade(tradeItem, action, attributes);
		TradeEvents.CONSUME_EVENT.notify(this, action, new SimpleTrade(action, TradeType.AWARD, tradeItem), attributes);
	}

	protected void receive(Trade result, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		for (TradeItem tradeItem : result.getAllTradeItem()) {
			this.receiveTrade(tradeItem, result.getAction(), attributes);
		}
		TradeEvents.RECEIVE_EVENT.notify(this, result.getAction(), result, attributes);
	}

	protected void receive(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
		Attributes attributes = ContextAttributes.create(entries);
		this.receiveTrade(tradeItem, action, attributes);
		TradeEvents.RECEIVE_EVENT.notify(this, action, new SimpleTrade(action, TradeType.AWARD, tradeItem), attributes);
	}

	protected void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	protected void setStuffOwnerExplorer(StuffOwnerExplorer stuffOwnerExplorer) {
		this.stuffOwnerExplorer = stuffOwnerExplorer;
	}

	private void consumeTrade(TradeItem<?> tradeItem, Action action, Attributes attributes) {
		if (!tradeItem.isValid()) {
			return;
		}
		ItemModel model = tradeItem.getItemModel();
		try {
			O owner = this.getOwner(model.getItemType());
			if (owner != null) {
				synchronized (owner) {
					this.doConsume(owner, tradeItem, action, attributes);
				}
			} else {
				LOGGER.warn("{}玩家没有 {} Storage对象", this.playerId, model.getItemType());
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", this.playerId, model.getItemType(), model.getId(), e);
		}
	}

	private void receiveTrade(TradeItem<?> tradeItem, Action action, Attributes attributes) {
		if (!tradeItem.isValid()) {
			return;
		}
		ItemModel model = tradeItem.getItemModel();
		try {
			O owner = this.getOwner(model.getItemType());
			if (owner != null) {
				synchronized (owner) {
					this.doReceive(owner, tradeItem, action, attributes);
				}
			} else {
				LOGGER.warn("{}玩家没有 {} Storage对象", this.playerId, model.getItemType());
			}
		} catch (Throwable e) {
			LOGGER.error("{}玩家 consume {} - {}", this.playerId, model.getItemType(), model.getId(), e);
		}
	}

	protected abstract void doReceive(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);

	protected abstract void doConsume(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);

}
