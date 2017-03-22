package com.tny.game.net.message;

import com.tny.game.net.session.Session;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public interface MessageSentHandler<UID> {

    void onSent(Session<UID> session, Message<UID> message);

}
