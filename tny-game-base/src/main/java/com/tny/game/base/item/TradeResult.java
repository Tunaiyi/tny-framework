package com.tny.game.base.item;

import com.tny.game.base.item.behavior.simple.SimpleDealedtem;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;

import java.util.*;
import java.util.Map.Entry;

public class TradeResult {

    private static final TradeResult EMPTY = new TradeResult();

    private List<DealedItem<?>> tradedItemList = new ArrayList<DealedItem<?>>();

    private Set<Stuff<?>> tradeStuffSet = new HashSet<Stuff<?>>();

    private TradeResult() {
    }

    private TradeResult(DealedItem<?> item) {
        super();
        this.tradedItemList.add(item);
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(Collection<DealedItem<?>> items) {
        super();
        this.tradedItemList.addAll(items);
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(ItemModel model, int alert, DemandParamEntry<?>... entrise) {
        super();
        this.tradedItemList.add(new SimpleDealedtem<ItemModel>(model, alert, entrise));
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(Stuff<?> stuff, int alert) {
        super();
        this.tradedItemList.add(new SimpleTradeItem<ItemModel>(stuff.getModel(), alert));
        this.tradeStuffSet.add(stuff);
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(Map<? extends Stuff<?>, Integer> tradeStuffMap) {
        super();
        for (Entry<? extends Stuff<?>, Integer> entry : tradeStuffMap.entrySet()) {
            Stuff<?> stuff = entry.getKey();
            this.tradedItemList.add(new SimpleTradeItem<ItemModel>(stuff.getModel(), entry.getValue()));
            this.tradeStuffSet.add(stuff);
        }
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    public boolean isTrade(ItemModel model) {
        Integer value = 0;
        for (DealedItem<?> item : this.tradedItemList) {
            if (item.getItemModel().equals(model)) {
                value = item.getNumber();
                if (value > 0)
                    break;
            }
        }
        return value != null && value > 0;
    }

    public List<DealedItem<?>> getTradedList() {
        return this.tradedItemList;
    }

    @SuppressWarnings("unchecked")
    public <S extends Stuff<?>> Set<S> getTradeStuffSet() {
        return (Set<S>) Collections.unmodifiableSet(this.tradeStuffSet);
    }

    public static TradeResult create(DealedItem<?> item) {
        return new TradeResult(item);
    }

    public static TradeResult create(Collection<DealedItem<?>> items) {
        return new TradeResult(items);
    }

    public static TradeResult create(Stuff<?> stuff, int alert) {
        return new TradeResult(stuff, alert);
    }

    public static TradeResult create(ItemModel model, int alert) {
        return new TradeResult(model, alert);
    }

    public static TradeResult create(Map<? extends Stuff<?>, Integer> tradeStuffMap) {
        return new TradeResult(tradeStuffMap);
    }

    public static TradeResult empty() {
        return EMPTY;
    }

}
