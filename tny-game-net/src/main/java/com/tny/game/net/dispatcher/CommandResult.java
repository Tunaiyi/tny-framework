package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;

/**
 * @author KGTny
 * @ClassName: CommandResult
 * @Description: 命令结果
 * @date 2011-9-19 上午10:50:30
 * <p>
 * 命令结果
 * <p>
 * 包含一个结果状态码,消息体<br>
 */
public interface CommandResult {

    CommandResult NO_RESULT = new CommandResult() {

        @Override
        public ResultCode getResultCode() {
            return ResultCode.SUCCESS;
        }

        @Override
        public Object getBody() {
            return null;
        }

        @Override
        public Protocol getProtocol() {
            return null;
        }

    };


    /**
     * 获取结果状态码
     * <p>
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    ResultCode getResultCode();

    /**
     * 获取响应消息体
     * <p>
     * <p>
     * 获取响应消息体<br>
     *
     * @return 返回响应消息体
     */
    Object getBody();

    /**
     * @return 返回结果协议, null 原协议返回
     */
    Protocol getProtocol();

}
