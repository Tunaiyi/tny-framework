package com.tny.game.basics.upgrade.notify;

import com.tny.game.basics.item.behavior.*;

/**
 * Created by Kun Yang on 2018/3/23.
 */
public interface OnReset<I> {

    void reset(I item, Action action, int oldLevel);

}
