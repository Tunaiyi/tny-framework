package com.tny.game.net.transport;

import com.tny.game.net.transport.message.Message;


/**
 * Created by Kun Yang on 2017/9/9.
 */
public interface WriteCallback<UID> {

    void onWrite(Message<UID> message, boolean success, Throwable cause);

}
