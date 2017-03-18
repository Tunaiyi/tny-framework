package com.tny.game.net.base;

import com.tny.game.net.message.Protocol;

/**
 * @author KGTny
 * @ClassName: MessageBuilder
 * @Description: 下发消息构建器
 * @date 2011-10-27 下午1:43:55
 * <p>
 * 下发消息构建器
 * <p>
 * <br>
 */
public class ResponseMessageBuilder {

    /**
     * 返回信息
     */
    private Object message;
    /**
     * 错误码
     */
    private int result = CoreResponseCode.SUCCESS.getCode();
    /**
     * 响应操作
     */
    private int protocol;

    private ResponseMessageBuilder() {
    }

    /**
     * 创建消息构建器 <br>
     *
     * @return 构建器
     */
    public static ResponseMessageBuilder createBuilder() {
        return new ResponseMessageBuilder();
    }

    /**
     * 设置消息 <br>
     *
     * @param message 消息
     * @return 构建器
     */
    public ResponseMessageBuilder setMessage(Object message) {
        this.message = message;
        return this;
    }

    /**
     * 设置状态码 <br>
     *
     * @param result 状态码
     * @return 构建器
     */
    public ResponseMessageBuilder setResult(int result) {
        this.result = result;
        return this;
    }

    /**
     * 设置操作 <br>
     *
     * @param protocol 操作
     * @return 构建器
     */
    public ResponseMessageBuilder setProtocol(int protocol) {
        this.protocol = protocol;
        return this;
    }

    public ResponseMessageBuilder setSendController(Protocol controller) {
        this.protocol = controller.getProtocol();
        return this;
    }

    /**
     * 创建消息 <br>
     *
     * @return
     */
    public ResponseMessage build() {
        if (this.result <= 0)
            throw new IllegalArgumentException("build message result is " + this.result);
        if (this.protocol == 0)
            throw new NullPointerException("build message protocol is 0");
        GameResponseMessage message = new GameResponseMessage();
        message.message = this.message;
        message.protocol = this.protocol;
        message.result = this.result;
        return message;
    }

    private static class GameResponseMessage implements ResponseMessage {

        /**
         * 返回信息
         */
        private Object message;
        /**
         * 错误码
         */
        private int result;
        /**
         * 响应操作
         */
        private Object protocol;

        @Override
        public Object getMessage() {
            return message;
        }

        @Override
        public int getResult() {
            return result;
        }

        @Override
        public Object getProtocol() {
            return protocol;
        }

    }
}
