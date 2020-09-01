package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;
import com.tny.game.base.item.behavior.simple.*;
import com.tny.game.base.item.listener.*;
import com.tny.game.base.log.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.context.*;
import org.slf4j.*;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractWarehouse<O extends Storage> implements Warehouse<O>, Owned {

    protected final static Logger LOGGER = LoggerFactory.getLogger(LogName.WAREHOUSE);

    protected long playerId;

    protected StorageExplorer storageExplorer;

    protected Class<? extends O> storageClass;

    protected Map<ItemType, WeakReference<? extends O>> storageMap = new CopyOnWriteMap<>();

    public AbstractWarehouse(Class<O> storageClass) {
        this.storageClass = storageClass;
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public long getOwnerId() {
        return this.playerId;
    }

    @Override
    public <SO extends O> SO getStorage(ItemType itemType, Class<SO> clazz) {
        WeakReference<? extends O> reference = this.storageMap.get(itemType);
        O storage = reference != null ? reference.get() : null;
        if (storage != null) {
            if (clazz.isInstance(storage)) {
                return (SO)storage;
            } else {
                throw new ClassCastException(MessageFormat.format("storage的对象类型为{0},而非{1}", storage.getClass(), clazz));
            }
        }
        storage = this.storageExplorer.getStorage(this.playerId, itemType.getId());
        if (storage == null) {
            throw new NullPointerException(MessageFormat.format("{0} 玩家 {1} {2} storage的对象为 null", this.playerId, clazz, itemType));
        }
        reference = new WeakReference<>(storage);
        this.storageMap.put(itemType, reference);
        if (clazz.isInstance(storage)) {
            return (SO)storage;
        } else {
            throw new ClassCastException(MessageFormat.format("storage的对象类型为{0},而非{1}", storage.getClass(), clazz));
        }
    }

    @Override
    public <I extends Item<?>> I getItemById(ItemType itemType, long id, Class<I> clazz) {
        Storage<?, Stuff<?>> storage = this.getStorage(itemType, this.storageClass);
        if (storage == null) {
            return null;
        }
        return (I)storage.getItemById(id);
    }

    @Override
    public <I extends Item<?>> List<I> getItemByItemId(ItemType itemType, int itemId, Class<I> clazz) {
        Storage<?, Stuff<?>> storage = this.getStorage(itemType, this.storageClass);
        if (storage == null) {
            return Collections.emptyList();
        }
        return storage.getItemsByItemId(itemId)
                .stream()
                .map(item -> (I)item)
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

    protected void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    protected void setStorageExplorer(StorageExplorer storageExplorer) {
        this.storageExplorer = storageExplorer;
    }

    private void consume0(TradeItem<?> tradeItem, Action action, Attributes attributes) {
        if (!tradeItem.isValid()) {
            return;
        }
        ItemModel model = tradeItem.getItemModel();
        try {
            O storage = this.getStorage(model.getItemType(), this.storageClass);
            if (storage != null) {
                synchronized (storage) {
                    this.doConsume(storage, tradeItem, action, attributes);
                }
            } else {
                LOGGER.warn("{}玩家没有 {} Storage对象", this.playerId, model.getItemType());
            }
        } catch (Throwable e) {
            LOGGER.error("{}玩家 consume {} - {}", this.playerId, model.getItemType(), model.getId(), e);
        }
    }

    private void receive0(TradeItem<?> tradeItem, Action action, Attributes attributes) {
        if (!tradeItem.isValid()) {
            return;
        }
        ItemModel model = tradeItem.getItemModel();
        try {
            O storage = this.getStorage(model.getItemType(), this.storageClass);
            if (storage != null) {
                synchronized (storage) {
                    this.doReceive(storage, tradeItem, action, attributes);
                }
            } else {
                LOGGER.warn("{}玩家没有 {} Storage对象", this.playerId, model.getItemType());
            }
        } catch (Throwable e) {
            LOGGER.error("{}玩家 consume {} - {}", this.playerId, model.getItemType(), model.getId(), e);
        }
    }

    protected abstract void doReceive(O storage, TradeItem<?> tradeItem, Action action, Attributes attributes);

    protected abstract void doConsume(O storage, TradeItem<?> tradeItem, Action action, Attributes attributes);

}
