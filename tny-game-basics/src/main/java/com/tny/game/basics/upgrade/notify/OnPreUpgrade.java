package com.tny.game.basics.upgrade.notify;

import com.tny.game.basics.item.behavior.*;

/**
 * Created by Kun Yang on 2018/3/23.
 */
public interface OnPreUpgrade<I> {

	void preUpgrade(I item, Action action, long upLevel);

}
