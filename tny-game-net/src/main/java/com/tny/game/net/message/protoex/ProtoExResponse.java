package com.tny.game.net.message.protoex;

import com.tny.game.net.common.NetMessage;
import com.tny.game.protoex.annotations.ProtoEx;
import com.tny.game.protoex.annotations.ProtoExConf;
import com.tny.game.protoex.annotations.ProtoExField;
import com.tny.game.protoex.annotations.TypeEncode;

/**
 * @author KGTny
 * @ClassName: NomalResponse
 * @Description: 相应对象
 * @date 2011-9-19 上午10:03:54
 * <p>
 * 请求响应对象
 * <p>
 * 请求响应对象,可附加信息<br>
 */
@ProtoEx(ProtoExMessageCoder.RESPONSE_ID)
public class ProtoExResponse extends NetMessage {

    private static final long serialVersionUID = 1L;

    //原来的ID
    @ProtoExField(1)
    protected int ID;

    @ProtoExField(2)
    protected Integer protocol = -1;

    @ProtoExField(3)
    protected int result;

    @ProtoExField(value = 4, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    protected Object body;

    @Deprecated
    @ProtoExField(5)
    protected boolean push;

    //原来的number
    @ProtoExField(6)
    protected int number;

    protected long time;

    @Override
    public int getID() {
        return this.number;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    // @Override
    // public boolean isPush() {
    //     return push;
    // }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        Object msg = this.getBody();
        if (msg == null)
            return null;
        if (!clazz.isInstance(msg))
            return null;
        return (T) msg;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getCheckCode() {
        return null;
    }

    @Override
    protected Object getBody() {
        return this.body;
    }

    @Override
    protected void setBody(Object body) {
        this.body = body;
    }

    // @Override
    // public MessageType getMessage() {
    //     return MessageType.RESPONSE;
    // }

    @Override
    public int getCode() {
        return result;
    }

    @Override
    public int getToMessage() {
        return ID;
    }

    @Override
    public String toString() {
        return "ProtoResponse [ID=" + this.ID + ", number=" + this.number + ", protocol=" + this.protocol + ", result=" + this.result
                + ", message=" + this.body + "]";
    }

}