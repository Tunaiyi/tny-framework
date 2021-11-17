package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.behavior.*;

/**
 * 满经验手动扣除经验并升级Updater
 * <p>
 * Created by xiaoqing on 2018/3/10.
 */
public abstract class DeductExpManualUpdater<I extends Item<?>, EM extends ExpModel> extends BaseExpUpdater<I, EM> {

	protected DeductExpManualUpdater(I item, int level, ExpType expType, long exp) {
		super(item, level, expType, exp, false);
	}

	@Override
	protected int upgrade(Action action) {
		return upgrade(action, 1);
	}

	@Override
	protected int upgrade(Action action, int upgradeLevel) {
		int alterLevel = 0;
		int oldLevel = this.level;
		for (int index = 0; upgradeLevel < 0 || index < upgradeLevel; index++) {
			long maxExp = getMaxExp();
			int maxLevel = this.getMaxLevel();
			if ((maxLevel < 0 || this.level < maxLevel) && this.exp >= maxExp) {
				prepareUpgrade(action, 1);
				this.level++;
				alterLevel++;
				this.exp = Math.max(this.exp - maxExp, 0);
			} else {
				break;
			}
		}
		if (alterLevel > 0) {
			postUpgrade(action, oldLevel, alterLevel);
		}
		return alterLevel;
	}

}
