package com.tny.game.net.message.protoex;

import com.tny.game.net.message.AbstractNetMessage;
import com.tny.game.protoex.annotations.Packed;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.ProtoExElement;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.annotations.TypeEncode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ProtoEx(ProtoExMessageCoder.MESSAGE_ID)
public class ProtoExMessage<UID> extends AbstractNetMessage<UID> {

    @ProtoExField(1)
    private int ID;

    @ProtoExField(2)
    private int protocol = -1;

    @ProtoExField(3)
    private int code;

    @ProtoExField(value = 4, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    private Object body;

    @ProtoExField(5)
    private int toMessage;

    @ProtoExField(6)
    private String sign;

    @ProtoExField(7)
    private long time = -1;

    @ProtoExField(8)
    @Packed(false)
    @ProtoExElement(@ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    private List<Object> bodies;

    public ProtoExMessage() {
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getCode() {
        return this.code;
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
    public String getSign() {
        return this.sign;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }


    @Override
    public String toString() {
        return "ProtoRequest [ID=" + this.ID + ", protocol=" + this.protocol + ", sign=" + this.sign
                + ", time=" + this.time + ", body="
                + getBody() + "]";
    }

    @Override
    protected Object getBody() {
        if (bodies != null)
            return bodies;
        return body;
    }

    @Override
    public ProtoExMessage<UID> setID(int ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public ProtoExMessage<UID> setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public ProtoExMessage<UID> setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProtoExMessage<UID> setBody(Object body) {
        if (body instanceof List) {
            this.bodies = (List<Object>) body;
        } else if (body instanceof Collection) {
            this.bodies = new ArrayList<>((Collection<?>) body);
        } else {
            this.body = body;
        }
        return this;
    }

    @Override
    public ProtoExMessage<UID> setToMessage(int toMessage) {
        this.toMessage = toMessage;
        return this;
    }

    @Override
    public ProtoExMessage<UID> setSign(String sign) {
        this.sign = sign;
        return this;
    }

    @Override
    public ProtoExMessage<UID> setTime(long time) {
        this.time = time;
        return this;
    }
}

