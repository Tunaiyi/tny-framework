package com.tny.game.oplog.simple;

import com.tny.game.basics.item.*;
import com.tny.game.oplog.*;

public class SimpleTradeLog implements StuffTradeLog {

	private long id;

	private int itemId;

	private long oldNum;

	private long alter;

	private long newNum;

	public SimpleTradeLog(Item<?> item, OpTradeType tradeType, long oldNumber, long alter, long newNumber) {
		super();
		this.id = item.getId();
		this.itemId = item.getModelId();
		this.oldNum = oldNumber;
		this.newNum = newNumber;
		this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
	}

	public SimpleTradeLog(long id, int itemId, OpTradeType tradeType, long oldNum, long alter, long newNum) {
		super();
		this.id = id;
		this.itemId = itemId;
		this.oldNum = oldNum;
		this.newNum = newNum;
		this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
	}

	public void receive(long alter, long newNum) {
		this.alter += alter;
		this.newNum = newNum;
	}

	public void consume(long alter, long newNum) {
		this.alter -= alter;
		this.newNum = newNum;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public int getItemId() {
		return this.itemId;
	}

	@Override
	public long getOldNum() {
		return this.oldNum;
	}

	@Override
	public long getNewNum() {
		return this.newNum;
	}

	@Override
	public long getAlterNum() {
		return this.alter;
	}

}
