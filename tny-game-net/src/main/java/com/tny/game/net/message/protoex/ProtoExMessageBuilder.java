package com.tny.game.net.message.protoex;

import com.tny.game.net.message.AbstractNetMessageBuilder;
import com.tny.game.net.message.common.CommonMessage;

/**
 * Created by Kun Yang on 2017/3/21.
 */
public class ProtoExMessageBuilder<UID> extends AbstractNetMessageBuilder<UID, CommonMessage<UID>> {

    protected ProtoExMessageBuilder() {
        super(CommonMessage::new, ProtoExMessageHeader::new);
    }

}
