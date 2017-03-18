package com.tny.game.net.message.protoex;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.common.NetMessage;
import com.tny.game.net.session.Session;
import com.tny.game.protoex.annotations.Packed;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.ProtoExElement;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.annotations.TypeEncode;

@ProtoEx(ProtoExMessageCoder.MESSAGE_ID)
public class ProtoExNetMessage extends NetMessage {

    @ProtoExField(1)
    protected int ID;

    @ProtoExField(2)
    protected int protocol = -1;

    @ProtoExField(3)
    protected int result;

    @Packed(false)
    @ProtoExField(value = 4, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    protected Object body;

    @ProtoExField(5)
    protected int toMessage;

    @ProtoExField(6)
    protected String checkKey;

    @ProtoExField(7)
    protected long time = -1;

    public ProtoExNetMessage() {
    }

    protected ProtoExNetMessage(Session session) {
        this.session = session;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getCode() {
        return ResultCode.SUCCESS_CODE;
    }

    @Override
    public int getToMessage() {
        return toMessage;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getCheckCode() {
        return this.checkKey;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    protected Object getBody() {
        return this.body;
    }

    @Override
    protected void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ProtoRequest [ID=" + this.ID + ", protocol=" + this.protocol + ", checkKey=" + this.checkKey
                + ", time=" + this.time + ", paramList="
                + this.body + "]";
    }
}

