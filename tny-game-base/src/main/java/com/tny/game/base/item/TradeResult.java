package com.tny.game.base.item;

import com.tny.game.base.item.behavior.simple.SimpleDealedItem;
import com.tny.game.base.item.behavior.simple.SimpleTradeItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.tny.game.number.NumberUtils.*;

public class TradeResult {

    private static final TradeResult EMPTY = new TradeResult();

    private List<DealedItem<?>> tradedItemList = new ArrayList<>();

    private Set<Stuff<?>> tradeStuffSet = new HashSet<>();

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

    private TradeResult(ItemModel model, Number alert, DemandParamEntry<?>... entries) {
        super();
        this.tradedItemList.add(new SimpleDealedItem<>(model, alert, entries));
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(Stuff<?> stuff, Number alert) {
        super();
        this.tradedItemList.add(new SimpleTradeItem<ItemModel>(stuff.getModel(), alert));
        this.tradeStuffSet.add(stuff);
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    private TradeResult(Map<? extends Stuff<?>, Number> tradeStuffMap) {
        super();
        for (Entry<? extends Stuff<?>, Number> entry : tradeStuffMap.entrySet()) {
            Stuff<?> stuff = entry.getKey();
            this.tradedItemList.add(new SimpleTradeItem<ItemModel>(stuff.getModel(), entry.getValue()));
            this.tradeStuffSet.add(stuff);
        }
        this.tradedItemList = Collections.unmodifiableList(this.tradedItemList);
        this.tradeStuffSet = Collections.unmodifiableSet(this.tradeStuffSet);
    }

    public boolean isTrade(ItemModel model) {
        Number value = null;
        for (DealedItem<?> item : this.tradedItemList) {
            if (item.getItemModel().equals(model)) {
                value = item.getNumber();
                if (greater(value, 0))
                    break;
            }
        }
        return value != null && greater(value, 0);
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

    public static TradeResult create(Stuff<?> stuff, Number alert) {
        return new TradeResult(stuff, alert);
    }

    public static TradeResult create(ItemModel model, Number alert) {
        return new TradeResult(model, alert);
    }

    public static TradeResult create(Map<? extends Stuff<?>, Number> tradeStuffMap) {
        return new TradeResult(tradeStuffMap);
    }

    public static TradeResult empty() {
        return EMPTY;
    }

}
