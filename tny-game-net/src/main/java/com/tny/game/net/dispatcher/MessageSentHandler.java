package com.tny.game.net.dispatcher;

import com.tny.game.net.base.Message;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public interface MessageSentHandler {

    void onSent(Message message);

}
