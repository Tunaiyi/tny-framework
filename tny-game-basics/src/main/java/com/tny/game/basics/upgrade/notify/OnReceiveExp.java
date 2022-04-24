package com.tny.game.basics.upgrade.notify;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.upgrade.*;

/**
 * Created by Kun Yang on 2018/3/23.
 */
public interface OnReceiveExp<I, EM extends ExpModel> {

    void receiveExp(I item, Action action, EM expModel, long oldExp, long recvExp);

}

