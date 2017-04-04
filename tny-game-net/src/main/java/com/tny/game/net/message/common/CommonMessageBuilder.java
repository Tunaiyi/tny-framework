package com.tny.game.net.message.common;

import com.tny.game.net.message.AbstractNetMessageBuilder;

/**
 * Created by Kun Yang on 2017/3/21.
 */
public class CommonMessageBuilder<UID> extends AbstractNetMessageBuilder<UID, CommonMessage<UID>> {

    protected CommonMessageBuilder() {
        super(CommonMessage::new);
    }

}
