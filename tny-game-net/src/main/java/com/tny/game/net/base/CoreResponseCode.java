package com.tny.game.net.base;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodeType;

public enum CoreResponseCode implements ResultCode {

    EMPTY(0, "无处理", ResultCodeType.GENERAL),

//	/** 请求处理成功 100*/
//	SUCCESS(ResultCode.SUCCESS_CODE, "请求处理成功", ResultCodeType.GENERAL),
    /**
     * 服务端接受请求异常
     */
    RECEIVE_ERROR(200, "服务端接受请求异常", ResultCodeType.WARN),
    /**
     * 服务端执行业务异常
     */
    EXECUTE_EXCEPTION(201, "服务端执行业务异常", ResultCodeType.GENERAL),
    /**
     * 服务端接受请求解码格式错误
     */
    DECODE_ERROR(202, "服务端接受请求解码格式错误", ResultCodeType.WARN),
    /**
     * 服务端返回响应编码响应异常
     */
    ENCODE_ERROR(203, "服务端返回响应编码响应异常", ResultCodeType.WARN),
    /**
     * 请求模块不存在
     */
    @Deprecated
    NO_SUCH_MODULE(204, "请求模块不存在", ResultCodeType.WARN),
    /**
     * 请求操作不存在
     */
    NO_SUCH_PROTOCOL(205, "请求操作不存在", ResultCodeType.WARN),
    /**
     * 分发消息异常
     */
    DISPATCH_EXCEPTION(206, "分发消息异常", ResultCodeType.WARN),
    /**
     * 非法请求参数
     */
    ILLEGAL_PARAMETERS(207, "非法请求参数", ResultCodeType.WARN),

    /**
     * 验证失败
     */
    VALIDATOR_FAIL(210, "验证失败", ResultCodeType.ERROR),
    /**
     * 用户未登录
     */
    UNLOGIN(211, "用户未登录", ResultCodeType.WARN),
    /**
     * 没有权限调用
     */
    NO_PERMISSIONS(212, "用户没有权限调用", ResultCodeType.WARN),
    /**
     * 请求消息被篡改
     */
    FALSIFY(213, "请求消息被篡改", ResultCodeType.WARN),
    /**
     * 请求过期
     */
    REQUEST_TIMEOUT(214, "请求过期", ResultCodeType.WARN),
    /**
     * 响应过期
     */
    RESPONSE_TIMEOUT(215, "响应过期", ResultCodeType.WARN),

    /**
     * 验证失败
     */
    SERVER_OFFLINE(220, "服务器未上线", ResultCodeType.ERROR),

    /**
     * 客户端IO异常
     */
    IO_EXCEPTION(300, "客户端IO异常", ResultCodeType.WARN),
    /**
     * 客户端请求中断
     */
    INTERRUPTED(301, "客户端请求中断", ResultCodeType.WARN),
    /**
     * 客户端请求超时
     */
    WAIT_TIMEOUT(302, "客户端请求超时", ResultCodeType.WARN),
    /**
     * 远程调用异常
     */
    REMOTE_EXCEPTION(303, "远程调用异常", ResultCodeType.WARN),
    /**
     * 客户端连接中断
     */
    CONNECT_INTERRUPTED(304, "客户端请求中断", ResultCodeType.WARN),
    /**
     * 客户端请求服务端无响应
     */
    REMOTE_NO_RESPONSE(305, "服务端无响应", ResultCodeType.WARN),
    /**
     * 客户端请求失败
     */
    REQUEST_FAILED(306, "客户端请求失败", ResultCodeType.WARN),

    //
    ;


    private int code;

    private String message;

    private ResultCodeType type;

    CoreResponseCode(int code, String message, ResultCodeType type) {
        this.code = code;
        this.message = message;
        this.type = type;
        this.registerSelf();
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public ResultCodeType getType() {
        return type;
    }

}
