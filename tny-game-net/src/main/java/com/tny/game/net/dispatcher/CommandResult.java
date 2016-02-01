package com.tny.game.net.dispatcher;

import com.tny.game.common.result.ResultCode;

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

    /**
     * 获取结果状态码
     * <p>
     * <p>
     * 获取结果状态码<br>
     *
     * @return 返回结果状态码
     */
    public ResultCode getResultCode();

    /**
     * 获取响应消息体
     * <p>
     * <p>
     * 获取响应消息体<br>
     *
     * @return 返回响应消息体
     */
    public Object getBody();

}
