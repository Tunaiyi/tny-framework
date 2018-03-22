package com.tny.game.net.dispatcher.message.simple;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.MessageType;
import com.tny.game.net.dispatcher.NetResponse;

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
public class SimpleResponse extends NetResponse {

    /**
     * 响应的请求Id
     */
    protected int ID;
    /**
     * 返回信息
     */
    protected Object body;
    /**
     * 错误码
     */
    protected int result;
    /**
     * 响应模块
     */
    protected int protocol;

    /**
     * 序号
     */
    protected int number;

    /**
     * 是否是推送
     */
    protected boolean push = true;

    private static final long serialVersionUID = 1L;

    public SimpleResponse() {
    }

    protected SimpleResponse(final ResultCode resultCode) {
        this.ID = 0;
        this.body = null;
        this.result = resultCode.getCode();
    }

    //	protected SimpleResponse(final Request request, final int result, Object message) {
    //		this.ID = request.getID();
    //		this.result = result;
    //		this.module = request.getModule();
    //		this.operation = request.getOperation();
    //		this.message = message;
    //	}
    //
    //	protected SimpleResponse(final SendController controller, final CommandResult result) {
    //		this.ID = 0;
    //		this.module = controller.getModule();
    //		this.operation = controller.getOperation();
    //		this.putMessage(result);
    //	}
    //
    //	protected SimpleResponse(final int result) {
    //		this.ID = 0;
    //		this.module = null;
    //		this.operation = null;
    //		this.message = null;
    //		this.result = result;
    //	}
    //
    //	public SimpleResponse(final Request request) {
    //		this.ID = request.getID();
    //		this.module = request.getModule();
    //		this.operation = request.getOperation();
    //		this.result = CoreResponseCode.SUCCESS.getCode();
    //		this.message = null;
    //	}
    //
    //	public SimpleResponse(final Response response, final int result) {
    //		this.ID = response.getID();
    //		this.module = response.getModule();
    //		this.operation = response.getOperation();
    //		this.result = result;
    //		this.message = null;
    //	}
    //
    //	public SimpleResponse(final Response response, final ResponseCode result) {
    //		this.ID = response.getID();
    //		this.module = response.getModule();
    //		this.operation = response.getOperation();
    //		this.result = result.getCode();
    //		this.message = null;
    //	}
    //
    //	public SimpleResponse(final Request request, final int result) {
    //		this.ID = request.getID();
    //		this.module = request.getModule();
    //		this.operation = request.getOperation();
    //		this.result = result;
    //		this.message = null;
    //	}
    //
    //	public SimpleResponse(final Request request, final ResponseCode result) {
    //		this.ID = 0;
    //		this.module = request.getModule();
    //		this.operation = request.getOperation();
    //		this.result = result.getCode();
    //		this.message = null;
    //	}

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public int getResult() {
        return this.result;
    }

    @Override
    public boolean isPush() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBody(Class<T> clazz) {
        if (this.body == null)
            return null;
        if (!clazz.isInstance(this.body))
            return null;
        return (T) this.body;
    }

    public void setID(int id) {
        this.ID = id;
    }

    @Override
    protected void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    @Override
    public void setPush(boolean push) {
        this.push = push;
    }

    @Override
    protected Object getBody() {
        return this.body;
    }

    @Override
    public void setBody(Object message) {
        this.body = message;
    }

    @Override
    public void setResult(int result) {
        this.result = result;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    //	@Override
    //	public void putBody(CommandResult result) {
    //		this.result = result.getResultCode();
    //		this.body = result.getBody();
    //	}

    @Override
    public MessageType getMessage() {
        return MessageType.RESPONSE;
    }

    @Override
    public String toString() {
        return "SimpleResponse [id=" + this.ID + ", result=" + this.result + ", protocol=" + this.protocol + ", message=" + this.body + "]";
    }

    @Override
    public int getProtocol() {
        return protocol;
    }

}