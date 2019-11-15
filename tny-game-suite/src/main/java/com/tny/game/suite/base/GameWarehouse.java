package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.*;
import com.tny.game.common.context.*;

public class GameWarehouse extends AbstractWarehouse<GameItemOwner> implements Identifier {

    //	protected Queue<Trade> trades;
    //
    //	public Collection<Trade> getTrades() {
    //		Queue<Trade> trades = this.trades;
    //		if (trades == null)
    //			return Collections.emptyList();
    //		return Collections.unmodifiableCollection(trades);
    //	}
    //
    //	private Queue<Trade> getQueue() {
    //		if (this.trades != null)
    //			return this.trades;
    //		synchronized (this) {
    //			this.trades = new ConcurrentLinkedQueue<>();
    //		}
    //		return this.trades;
    //	}
    //	public Trade popTrade() {
    //		Queue<Trade> trades = this.trades;
    //		if (trades == null)
    //			return null;
    //		return trades.poll();
    //	}
    //
    //	public boolean isTradesEmpty() {
    //		Queue<Trade> trades = this.trades;
    //		return this.trades == null || trades.isEmpty();
    //	}
    //
    //	public boolean pushTrade(Trade trade) {
    //		return this.getQueue().offer(trade);
    //	}
    //
    //	public boolean pushTrade(Collection<Trade> trades) {
    //		return this.getQueue().addAll(trades);
    //	}

    public GameWarehouse() {
        super(GameItemOwner.class);
    }

    @Override
    protected void consume(Trade result, AttrEntry<?>... entries) {
        super.consume(result, entries);
    }

    @Override
    protected void consume(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        super.consume(tradeItem, action, entries);
    }

    @Override
    protected void receive(Trade result, AttrEntry<?>... entries) {
        super.receive(result, entries);
    }

    @Override
    protected void receive(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        super.receive(tradeItem, action, entries);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doReceive(GameItemOwner owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        owner.receive(tradeItem, action, attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doConsume(GameItemOwner owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        owner.consume(tradeItem, action, attributes);
    }


}
