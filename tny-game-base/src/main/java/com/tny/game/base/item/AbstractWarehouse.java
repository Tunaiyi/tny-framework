package com.tny.game.base.item;

import com.tny.game.base.item.behavior.Action;
import com.tny.game.base.item.behavior.TradeType;
import com.tny.game.base.item.behavior.simple.SimpleTrade;
import com.tny.game.base.item.listener.TradeEvents;
import com.tny.game.base.log.LogName;
import com.tny.game.common.context.AttributeEntry;
import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractWarehouse<O extends Owner>
        implements Warehouse<O> {

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
        Owner<?> owner = this.getOwner(itemType, ownerClass);
        if (owner == null)
            return null;
        return (I) owner.getItemByID(id);
    }

    @Override
    public <I extends Item<?>> List<I> getItemByItemID(ItemType itemType, int itemID, Class<I> clazz) {
        Owner<Stuff<?>> owner = this.getOwner(itemType, ownerClass);
        if (owner == null)
            return Collections.emptyList();
        return owner.getItemByItemID(itemID)
                .stream()
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    protected DealedResult consume(Trade result, AttributeEntry<?>... entries) {
        WarehouseDealedResult dealedResult = new WarehouseDealedResult(result.getAction());
        Attributes attributes = ContextAttributes.create(entries);
        for (TradeItem tradeItem : result.getAllTradeItem()) {
            this.consume0(dealedResult, tradeItem, result.getAction(), attributes);
        }
        TradeEvents.CONSUME_EVENT.notify(this, result.getAction(), result, dealedResult, attributes);
        return dealedResult;
    }

    protected DealedResult consume(TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries) {
        WarehouseDealedResult dealedResult = new WarehouseDealedResult(action);
        Attributes attributes = ContextAttributes.create(entries);
        this.consume0(dealedResult, tradeItem, action, attributes);
        TradeEvents.CONSUME_EVENT.notify(this, action,  new SimpleTrade(action, TradeType.AWARD, tradeItem), dealedResult, attributes);
        return dealedResult;
    }

    protected DealedResult receive(Trade result, AttributeEntry<?>... entries) {
        WarehouseDealedResult dealedResult = new WarehouseDealedResult(result.getAction());
        Attributes attributes = ContextAttributes.create(entries);
        for (TradeItem tradeItem : result.getAllTradeItem()) {
            this.receive0(dealedResult, tradeItem, result.getAction(), attributes);
        }
        TradeEvents.RECEIVE_EVENT.notify(this, result.getAction(), result, dealedResult, attributes);
        return dealedResult;
    }

    protected DealedResult receive(TradeItem<?> tradeItem, Action action, AttributeEntry<?>... entries) {
        WarehouseDealedResult dealedResult = new WarehouseDealedResult(action);
        Attributes attributes = ContextAttributes.create(entries);
        this.receive0(dealedResult, tradeItem, action, attributes);
        TradeEvents.RECEIVE_EVENT.notify(this, action, new SimpleTrade(action, TradeType.AWARD, tradeItem), dealedResult, attributes);
        return dealedResult;
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    protected void setOwnerExplorer(OwnerExplorer ownerExplorer) {
        this.ownerExplorer = ownerExplorer;
    }

    private void consume0(WarehouseDealedResult dealedResult, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        ItemModel model = tradeItem.getItemModel();
        O owner = this.getOwner(model.getItemType(), ownerClass);
        if (owner != null) {
            synchronized (owner) {
                dealedResult.changeOwnerSet.add(owner);
                TradeResult tradeResult = this.doConsume(owner, tradeItem, action, attributes);
                dealedResult.changeStuffSet.addAll(tradeResult.getTradeStuffSet());
                dealedResult.dealedItemList.addAll(tradeResult.getTradedList());
            }
        } else {
            throw new NullPointerException(MessageFormat.format("{0}玩家没有 {1} Owner对象", this.playerID, model.getItemType()));
        }
    }

    private void receive0(WarehouseDealedResult dealedResult, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        ItemModel model = tradeItem.getItemModel();
        O owner = this.getOwner(model.getItemType(), ownerClass);
        if (owner != null) {
            synchronized (owner) {
                dealedResult.changeOwnerSet.add(owner);
                TradeResult tradeResult = this.doReceive(owner, tradeItem, action, attributes);
                dealedResult.changeStuffSet.addAll(tradeResult.getTradeStuffSet());
                dealedResult.dealedItemList.addAll(tradeResult.getTradedList());
            }
        } else {
            throw new NullPointerException(MessageFormat.format("{0}玩家没有 {1} Owner对象", this.playerID, model.getItemType()));
        }
    }

    protected abstract TradeResult doReceive(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);

    protected abstract TradeResult doConsume(O owner, TradeItem<?> tradeItem, Action action, Attributes attributes);

    private static class WarehouseDealedResult implements DealedResult {

        private Action action;

        private List<DealedItem<?>> dealedItemList = new ArrayList<>();

        private Set<Owner<?>> changeOwnerSet = new HashSet<>();

        private Set<Stuff<?>> changeStuffSet = new HashSet<>();

        private WarehouseDealedResult(Action action) {
            this.action = action;
        }

        @Override
        public Action getAction() {
            return this.action;
        }

        @Override
        public Set<Owner<?>> getChangeOwnerSet() {
            return Collections.unmodifiableSet(this.changeOwnerSet);
        }

        @Override
        public Set<Stuff<?>> getChangeStuffSet() {
            return Collections.unmodifiableSet(this.changeStuffSet);
        }

        @Override
        public List<DealedItem<?>> getDealedItemList() {
            return Collections.unmodifiableList(this.dealedItemList);
        }

    }
}
