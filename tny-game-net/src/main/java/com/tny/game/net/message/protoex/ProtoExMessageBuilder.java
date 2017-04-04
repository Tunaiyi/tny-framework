package com.tny.game.net.message.protoex;

import com.tny.game.net.message.AbstractNetMessageBuilder;

/**
 * Created by Kun Yang on 2017/3/21.
 */
public class ProtoExMessageBuilder<UID> extends AbstractNetMessageBuilder<UID, ProtoExMessage<UID>> {

    protected ProtoExMessageBuilder() {
        super(ProtoExMessage::new);
    }


}
