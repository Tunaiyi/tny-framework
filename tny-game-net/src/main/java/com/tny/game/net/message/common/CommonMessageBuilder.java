package com.tny.game.net.message.common;

import com.tny.game.net.message.*;

import java.util.function.Supplier;

/**
 * Created by Kun Yang on 2017/3/21.
 */
public class CommonMessageBuilder<UID> extends AbstractNetMessageBuilder<UID, CommonMessage<UID>> {

    protected CommonMessageBuilder() {
        super(CommonMessage::new, CommonMessageHeader::new);
    }

    protected CommonMessageBuilder(Supplier<? extends AbstractNetMessageHeader> headCreator) {
        super(CommonMessage::new, headCreator);
    }

}
