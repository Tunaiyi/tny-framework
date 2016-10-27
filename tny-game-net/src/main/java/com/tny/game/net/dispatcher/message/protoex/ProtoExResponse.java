package com.tny.game.net.dispatcher.message.protoex;

import com.tny.game.net.base.MessageType;
import com.tny.game.net.dispatcher.NetResponse;
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
public class ProtoExResponse extends NetResponse {

    private static final long serialVersionUID = 1L;

    @ProtoExField(1)
    protected int ID;

    @ProtoExField(2)
    protected Integer protocol = -1;

    @ProtoExField(3)
    protected int result;

    @ProtoExField(value = 4, conf = @ProtoExConf(typeEncode = TypeEncode.EXPLICIT))
    protected Object body;

    @ProtoExField(5)
    protected boolean push;

    @ProtoExField(6)
    protected int number;

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getResult() {
        return this.result;
    }

    @Override
    public int getProtocol() {
        return this.protocol;
    }

    @Override
    public boolean isPush() {
        return push;
    }

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
    protected Object getBody() {
        return this.body;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    protected void setBody(Object body) {
        this.body = body;
    }

    @Override
    protected void setResult(int result) {
        this.result = result;
    }

    @Override
    protected void setID(int id) {
        this.ID = id;
    }

    @Override
    protected void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void setPush(boolean push) {
        this.push = push;
    }

    @Override
    protected void setNumber(int number) {
        this.number = number;
    }

    @Override
    public MessageType getMessage() {
        return MessageType.RESPONSE;
    }

    @Override
    public String toString() {
        return "ProtoResponse [ID=" + this.ID + ", number=" + this.number + ", protocol=" + this.protocol + ", result=" + this.result
                + ", message=" + this.body + "]";
    }

}