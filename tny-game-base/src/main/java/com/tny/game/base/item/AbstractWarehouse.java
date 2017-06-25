package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.listener.TradeEvents;
import com.tny.game.base.log.LogName;
import com.tny.game.common.context.AttrEntry;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractWarehouse<O extends Owner> implements Warehouse<O> {

    protected final static Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    protected long playerID;

    protected OwnerExplorer ownerExplorer;

    protected Class<? extends O> ownerClass;

    protected Map<ItemType, WeakReference<? extends O>> ownerMap = new CopyOnWriteMap<>();

    public AbstractWarehouse(Class<O> ownerClass) {
        this.ownerClass = ownerClass;
    }

    @Override
    public long getPlayerID() {
        return this.playerID;
    }

    @Override
    public <SO extends O> SO getOwner(ItemType itemType, Class<SO> clazz) {
        WeakReference<? extends O> reference = this.ownerMap.get(itemType);
        O owner = reference != null ? reference.get() : null;
        if (owner != null) {
            if (clazz.isInstance(owner))
                return (SO) owner;
            else
                throw new ClassCastException(MessageFormat.format("owner的对象类型为{0},而非{1}", owner.getClass(), clazz));
        }
        owner = this.ownerExplorer.getOwner(this.playerID, itemType.getID());
        if (owner == null)
            throw new NullPointerException(MessageFormat.format("{0} 玩家 {1} {2} owner的对象为 null", this.playerID, clazz, itemType));
        reference = new WeakReference<>(owner);
        this.ownerMap.put(itemType, reference);
        if (clazz.isInstance(owner)) {
            return (SO) owner;
        } else {
            throw new ClassCastException(MessageFormat.format("owner的对象类型为{0},而非{1}", owner.getClass(), clazz));
        }
    }

    @Override
    public <I extends Item<?>> I getItemByID(ItemType itemType, long id, Class<I> clazz) {
        Owner<?, Stuff<?>> owner = this.getOwner(itemType, ownerClass);
        if (owner == null)
            return null;
        return (I) owner.getItemByID(id);
    }

    @Override
    public <I extends Item<?>> List<I> getItemByItemID(ItemType itemType, int itemID, Class<I> clazz) {
        Owner<?, Stuff<?>> owner = this.getOwner(itemType, ownerClass);
        if (owner == null)
            return Collections.emptyList();
        return owner.getItemsByItemID(itemID)
                .stream()
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    protected void consume(Trade result, AttrEntry<?>... entries) {
        Attributes attributes = ContextAttributes.create(entries);
        for (TradeItem tradeItem : result.getAllTradeItem()) {
            this.consume0(tradeItem, result.getAction(), attributes);
        }
        TradeEvents.CONSUME_EVENT.notify(this, result.getAction(), result, attributes);
    }

    protected void consume(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        Attributes attributes = ContextAttributes.create(entries);
        this.consume0(tradeItem, action, attributes);
        TradeEvents.CONSUME_EVENT.notify(this, action, new SimpleTrade(action, TradeType.AWARD, tradeItem), attributes);
    }

    protected void receive(Trade result, AttrEntry<?>... entries) {
        Attributes attributes = ContextAttributes.create(entries);
        for (TradeItem tradeItem : result.getAllTradeItem()) {
            this.receive0(tradeItem, result.getAction(), attributes);
        }
        TradeEvents.RECEIVE_EVENT.notify(this, result.getAction(), result, attributes);
    }

    protected void receive(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        Attributes attributes = ContextAttributes.create(entries);
        this.receive0(tradeItem, action, attributes);
        TradeEvents.RECEIVE_EVENT.notify(this, action, new SimpleTrade(action, TradeType.AWARD, tradeItem), attributes);
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    protected void setOwnerExplorer(OwnerExplorer ownerExplorer) {
        this.ownerExplorer = ownerExplorer;
    }

    private void consume0(TradeItem<?> tradeItem, Action action, Attributes attributes) {
        if (!tradeItem.isValid())
            return;
        ItemModel model = tradeItem.getItemModel();
        try {
            O owner = this.getOwner(model.getItemType(), ownerClass);
            if (owner != null) {
                synchronized (owner) {
                     this.doConsume(owner, tradeItem, action, attributes);
                }
            } else {
                LOGGER.warn("{}玩家没有 {} Owner对象", this.playerID, model.getItemType());
            }
        } catch (Throwable e) {
            LOGGER.error("{}玩家 consume {} - {}", this.playerID, model.getItemType(), model.getID(), e);
        }
    }

    private void receive0(TradeItem<?> tradeItem, Action action, Attributes attributes) {
        if (!tradeItem.isValid())
            return;
        ItemModel model = tradeItem.getItemModel();
        try {
            O owner = this.getOwner(model.getItemType(), ownerClass);
            if (owner != null) {
                synchronized (owner) {
                    this.doReceive(owner, tradeItem, action, attributes);
                }
            } else {
                LOGGER.warn("{}玩家没有 {} Owner对象", this.playerID, model.getItemType());
            }
        } catch (Throwable e) {
            LOGGER.error("{}玩家 consume {} - {}", this.playerID, model.getItemType(), model.getID(), e);
        }
    }

    protected abstract void doReceive(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);

    protected abstract void doConsume(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);
}
