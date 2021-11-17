package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.upgrade.notify.*;
import com.tny.game.common.context.*;
import com.tny.game.common.event.bus.*;

/**
 * 抽象升级器
 * Created by Kun Yang on 2017/4/5.
 */
public abstract class BaseExpUpdater<I extends Item<?>, EM extends ExpModel> extends BaseUpdater<I> implements ExpUpdater<I> {

	private static final BindP3EventBus<ExpUpdaterListener, ExpUpdater<?>, Action, Integer, Integer> UPGRADE_EVENT =
			EventBuses.of(ExpUpdaterListener.class, ExpUpdaterListener::onUpgrade, true);

	private static final BindP4EventBus<ExpUpdaterListener, ExpUpdater<?>, ExpModel, Action, Long, Long> RECEIVE_EXP_EVENT =
			EventBuses.of(ExpUpdaterListener.class, ExpUpdaterListener::onReceiveExp, true);

	protected long exp;

	private final ExpType expType;

	private boolean upgradeOnMax;

	private OnReceiveExp<I, EM> onReceiveExp;

	private OnPreReceiveExp<I, EM> onPreReceiveExp;

	public BaseExpUpdater(I item, int level, ExpType expType, long exp, boolean upgradeOnMax) {
		super(item, level);
		this.expType = expType;
		this.exp = exp;
		this.upgradeOnMax = upgradeOnMax;
	}

	@Override
	public I item() {
		return item;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public long getExp() {
		return exp;
	}

	@Override
	public ExpType getExpType() {
		return expType;
	}

	@Override
	protected void reset(Action action) {
		int oldLevel = this.level;
		this.level = this.getInitLevel();
		this.exp = 0;
		if (onReset != null) {
			onReset.reset(item, action, oldLevel);
		}
	}

	protected void receive(EM expModel, long recvExp, Action action, Attributes attributes) {
		if (recvExp <= 0 || expModel.getExpType() != this.getExpType()) {
			return;
		}
		long oldExp = this.exp;
		onPreReceiveExp(action, expModel, recvExp);
		this.exp = this.exp + recvExp;
		postReceiveExp(action, expModel, oldExp, recvExp);
		if (this.upgradeOnMax) {
			this.upgrade(action);
		}
	}

	protected void onPreReceiveExp(Action action, EM expModel, long recvExp) {
		if (this.onPreReceiveExp != null) {
			this.onPreReceiveExp.preReceiveExp(this.item, action, expModel, recvExp);
		}
	}

	protected void postReceiveExp(Action action, EM expModel, long oldExp, long recvExp) {
		if (this.onReceiveExp != null) {
			this.onReceiveExp.receiveExp(this.item, action, expModel, oldExp, recvExp);
			RECEIVE_EXP_EVENT.notify(this, expModel, action, oldExp, recvExp);
		}
	}

	protected void prepareUpgrade(Action action, int upLevel) {
		if (this.onPreUpgrade != null) {
			this.onPreUpgrade.preUpgrade(this.item, action, upLevel);
		}
	}

	/**
	 * 升级后通知
	 *
	 * @param action   行为
	 * @param oldLevel 旧等级
	 * @param upLevel  级级数
	 */
	protected void postUpgrade(Action action, int oldLevel, int upLevel) {
		if (this.onUpgrade != null) {
			this.onUpgrade.upgrade(this.item, action, oldLevel, upLevel);
		}
		UPGRADE_EVENT.notify(this, action, oldLevel, upLevel);
	}

	protected abstract int upgrade(Action action);

	protected BaseExpUpdater<I, EM> setExp(long exp) {
		this.exp = exp;
		return this;
	}

	protected BaseExpUpdater<I, EM> setUpgradeOnMax(boolean upgradeOnMax) {
		this.upgradeOnMax = upgradeOnMax;
		return this;
	}

	protected BaseExpUpdater<I, EM> setOnReceiveExp(OnReceiveExp<I, EM> onReceiveExp) {
		this.onReceiveExp = onReceiveExp;
		return this;
	}

	protected BaseExpUpdater<I, EM> setOnPreReceiveExp(OnPreReceiveExp<I, EM> onPreReceiveExp) {
		this.onPreReceiveExp = onPreReceiveExp;
		return this;
	}

}