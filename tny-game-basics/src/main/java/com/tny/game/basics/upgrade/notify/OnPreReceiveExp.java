package com.tny.game.basics.upgrade.notify;

import com.tny.game.basics.item.behavior.*;
import com.tny.game.basics.upgrade.*;

/**
 * Created by Kun Yang on 2018/3/23.
 */
public interface OnPreReceiveExp<I, EM extends ExpModel> {

    void preReceiveExp(I item, Action action, EM expModel, long recvExp);

}