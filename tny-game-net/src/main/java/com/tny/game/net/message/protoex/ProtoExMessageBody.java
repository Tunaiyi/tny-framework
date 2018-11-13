package com.tny.game.net.message.protoex;

import com.tny.game.protoex.annotations.*;

@ProtoEx(ProtoExCodec.MESSAGE_BODY_ID)
public class ProtoExMessageBody {

    @Packed(false)
    @ProtoExField(value = 1, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    private Object body;

    public ProtoExMessageBody() {
    }

    public ProtoExMessageBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return body;
    }
}

