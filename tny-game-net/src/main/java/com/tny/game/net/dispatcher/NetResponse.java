package com.tny.game.net.dispatcher;

import com.tny.game.net.base.MessageType;

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
public abstract class NetResponse extends Response {

    protected static final long serialVersionUID = 1L;

    public NetResponse() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        Object body = this.getBody();
        if (body == null)
            return null;
        if (!clazz.isInstance(body))
            return null;
        return (T) body;
    }

    protected abstract Object getBody();

    protected abstract void setBody(Object message);

    protected abstract void setResult(int result);

    protected abstract void setID(int id);

    protected abstract void setProtocol(int protocol);

    protected abstract void setPush(boolean push);

    protected abstract void setNumber(int number);

    @Override
    public MessageType getMessage() {
        return MessageType.RESPONSE;
    }

    @Override
    public String toString() {
        return "SimpleResponse [id=" + this.getID() + ", result=" + this.getResult() + ", protocol=" + this.getProtocol() + ", message=" + this.getBody() + "]";
    }

}