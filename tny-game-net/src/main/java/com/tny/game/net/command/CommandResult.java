package com.tny.game.net.command;

import com.tny.game.common.result.*;

/**
 * @author KGTny
 */
public interface CommandResult {

    CommandResult VOID_SUCCESS = new CommandResult() {

        @Override
        public ResultCode getResultCode() {
            return ResultCode.SUCCESS;
        }

        @Override
        public String getDescriptions() {
            return "success";
        }

        @Override
        public Object getBody() {
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
    default int getCode() {
        return this.getResultCode().getCode();
    }

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
     * @return 消息描述(开发用, 请勿作为提示)
     */
    String getDescriptions();

    /**
     * 获取响应消息体
     * <p>
     * <p>
     * 获取响应消息体<br>
     *
     * @return 返回响应消息体
     */
    Object getBody();

}
