package com.tny.game.suite.base;

import com.tny.game.base.item.*;
import com.tny.game.base.item.behavior.Action;
import com.tny.game.common.context.AttrEntry;
import com.tny.game.common.context.Attributes;

public class GameWarehouse extends AbstractWarehouse<GameItemOwner> implements Identifiable {

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
    protected DealedResult consume(Trade result, AttrEntry<?>... entries) {
        return super.consume(result, entries);
    }

    @Override
    protected DealedResult consume(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        return super.consume(tradeItem, action, entries);
    }

    @Override
    protected DealedResult receive(Trade result, AttrEntry<?>... entries) {
        return super.receive(result, entries);
    }

    @Override
    protected DealedResult receive(TradeItem<?> tradeItem, Action action, AttrEntry<?>... entries) {
        return super.receive(tradeItem, action, entries);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TradeResult doReceive(GameItemOwner owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        return owner.receive(tradeItem, action, attributes);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TradeResult doConsume(GameItemOwner owner, TradeItem<?> tradeItem, Action action, Attributes attributes) {
        return owner.consume(tradeItem, action, attributes);
    }


}
