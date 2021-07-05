package com.tny.game.net.endpoint;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/12 8:27 下午
 */
public interface MessageMaker<UID> {

    Message make(MessageContext<UID> context);

}
