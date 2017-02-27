package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.NetMessage;
import com.tny.game.net.dispatcher.Session;
import com.tny.game.protoex.annotations.Packed;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.ProtoExElement;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.annotations.TypeEncode;

@ProtoEx(ProtoExMessageCoder.REQUEST_ID)
public class ProtoExRequest extends NetMessage {

    private static final long serialVersionUID = 1L;

    @ProtoExField(1)
    protected int ID;

    @ProtoExField(2)
    protected int protocol = -1;

    @ProtoExField(3)
    protected String checkKey;

    @ProtoExField(4)
    protected long time = -1;

    @ProtoExField(5)
    @Packed(false)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    protected Object body;

    public ProtoExRequest() {
    }

    protected ProtoExRequest(Session session) {
        this.session = session;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    // @Override
    // public MessageType getMessage() {
    //     return MessageType.REQUEST;
    // }

    @Override
    public int getCode() {
        return ResultCode.SUCCESS_CODE;
    }

    @Override
    public int getToMessage() {
        return -1;
    }

    @Override
    public long getTime() {
        return this.time;
    }


    @Override
    public String getCheckKey() {
        return this.checkKey;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }


    @Override
    public String toString() {
        return "ProtoRequest [ID=" + this.ID + ", protocol=" + this.protocol + ", checkKey=" + this.checkKey
                + ", time=" + this.time + ", paramList="
                + this.body + "]";
    }

    @Override
    protected Object getBody() {
        return this.body;
    }

    @Override
    protected void setBody(Object body) {
        this.body = body;
    }
}
