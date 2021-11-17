package com.tny.game.basics.upgrade.notify;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.upgrade.*;

/**
 * Created by xiaoqing on 2016/2/29.
 */
public interface ExpUpdaterListener {

	default void onUpgrade(ExpUpdater<?> source, Action action, int oldLevel, int alterLevel) {
	}

	default void onReceiveExp(ExpUpdater<?> source, ExpModel expModel, Action action, long oldNum, long receiveNum) {
	}

}
