package com.tny.game.net.tunnel;

import com.tny.game.net.message.Message;


/**
 * Created by Kun Yang on 2017/9/9.
 */
public interface WriteCallback<UID> {

    void onWrite(Message<UID> message, boolean success, Throwable cause);

}
