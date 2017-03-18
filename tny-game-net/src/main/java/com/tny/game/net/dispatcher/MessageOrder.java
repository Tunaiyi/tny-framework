package com.tny.game.net.dispatcher;

import com.tny.game.net.session.event.MessageOutputMode;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public interface MessageOrder {

    MessageOutputMode getOrderType();

}
