/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.application;

import com.tny.game.common.result.*;

public enum NetResultCode implements ResultCode {

    EMPTY(0, "无处理", ResultLevel.GENERAL),

    /**
     * 服务端返回响应编码响应异常(不断开连接)
     */
    DECODE_FAILED(151, "编码响应异常", ResultLevel.WARN),

    /**
     * 服务端返回响应编码响应异常
     */
    ENCODE_FAILED(152, "编码响应异常", ResultLevel.WARN),

    /**
     * 服务端接受请求解码格式错误(断开连接)
     */
    DECODE_ERROR(153, "解码格式错误", ResultLevel.ERROR),

    /**
     * 服务端返回响应编码响应异常
     */
    ENCODE_ERROR(154, "编码响应错误", ResultLevel.ERROR),
    /**
     * 收到的网络包超时失效
     */
    PACKET_TIMEOUT(155, "网络包超时失效", ResultLevel.WARN),
    /**
     * 网络包校验失败
     */
    PACKET_VERIFY_FAILED(156, "网络包校验失败", ResultLevel.WARN),

    /**
     * 拒绝接受消息
     */
    REJECT_TO_RECEIVE_MESSAGE(180, "拒绝接受消息", ResultLevel.WARN),

    /**
     * 拒绝发送消息
     */
    REJECT_TO_SEND_MESSAGE(181, "拒绝发送消息", ResultLevel.WARN),

    /**
     * 消息发送失败
     */
    MESSAGE_SEND_FAILED(182, "消息发送失败", ResultLevel.WARN),

    /**
     * 连接断开
     */
    DISCONNECTION(183, "连接断开", ResultLevel.WARN),

    /**
     * 连接失败
     */
    CONNECT_FAILED(184, "连接失败", ResultLevel.WARN),

    /**
     * 会话关闭
     */
    SESSION_CLOSE(185, "会话关闭", ResultLevel.WARN),

    /**
     * 服务端接受请求异常
     */
    SERVER_ERROR(200, "服务端异常", ResultLevel.WARN),

    /**
     * 服务端接受请求异常
     */
    SERVER_RECEIVE_EXCEPTION(201, "服务端接受请求异常", ResultLevel.WARN),
    /**
     * 服务端执行业务异常
     */
    SERVER_EXECUTE_EXCEPTION(202, "服务端执行业务异常", ResultLevel.GENERAL),

    /**
     * 请求操作不存在
     */
    SERVER_NO_SUCH_PROTOCOL(205, "请求操作不存在", ResultLevel.WARN),
    /**
     * 分发消息异常
     */
    SERVER_DISPATCH_EXCEPTION(206, "分发消息异常", ResultLevel.WARN),
    /**
     * 非法请求参数
     */
    SERVER_ILLEGAL_PARAMETERS(207, "非法请求参数", ResultLevel.WARN),

    /**
     * 无法接收该类型消息
     */
    SERVER_NO_RECEIVE_MODE(208, "无法接收该类型消息", ResultLevel.WARN),

    /**
     * 无法发送该类型消息
     */
    SERVER_NO_SEND_MODE(209, "无法发送该类型消息", ResultLevel.WARN),

    /**
     * 验证失败
     */
    AUTH_FAIL_ERROR(210, "验证失败", ResultLevel.ERROR),
    /**
     * 用户未登录
     */
    NO_LOGIN(211, "用户未登录", ResultLevel.WARN),
    /**
     * 没有权限调用
     */
    NO_PERMISSIONS(212, "用户没有权限调用", ResultLevel.WARN),
    /**
     * 请求消息被篡改
     */
    MESSAGE_FALSIFY(213, "请求消息被篡改", ResultLevel.WARN),
    /**
     * 请求过期
     */
    REQUEST_TIMEOUT(214, "请求过期", ResultLevel.WARN),
    /**
     * 响应过期
     */
    RESPONSE_TIMEOUT(215, "响应过期", ResultLevel.WARN),
    /**
     * 会话丢失
     */
    SESSION_LOSS_ERROR(216, "会话丢失", ResultLevel.ERROR),
    /**
     * 会话超时
     */
    SESSION_TIMEOUT_ERROR(217, "会话超时", ResultLevel.ERROR),
    /**
     * 会话丢失
     */
    SESSION_CREATE_FAILED(218, "会话创建失败", ResultLevel.GENERAL),

    /**
     * 消息已处理过
     */
    MESSAGE_HANDLED(219, "消息已处理过", ResultLevel.WARN),
    /**
     * 证书无效
     */
    INVALID_CERTIFICATE_ERROR(220, "验证无效", ResultLevel.ERROR),
    /**
     * 验证失败
     */
    SERVER_OFFLINE_ERROR(221, "服务器未上线", ResultLevel.ERROR),

    /**
     * 服务端执行业务超时
     */
    EXECUTE_TIMEOUT(222, "服务端执行业超时", ResultLevel.GENERAL),

    /**
     * 用户已登录
     */
    LOGGED_IN(223, "用户已登录", ResultLevel.GENERAL),
    /**
     * 集群网络繁忙
     */
    CLUSTER_NETWORK_BUSY(224, "集群网络未接通", ResultLevel.ERROR),
    /**
     * 集群网络未接通
     */
    CLUSTER_NETWORK_UNCONNECTED_ERROR(225, "集群网络未接通错误", ResultLevel.ERROR),
    /**
     * 集群不存在
     */
    CLUSTER_NOT_EXIST_ERROR(226, "集群不存在", ResultLevel.ERROR),

    /**
     * 客户端IO异常
     */
    IO_EXCEPTION(300, "客户端IO异常", ResultLevel.WARN),
    /**
     * 客户端请求中断
     */
    INTERRUPTED(301, "客户端请求中断", ResultLevel.WARN),
    /**
     * 客户端请求超时
     */
    WAIT_TIMEOUT(302, "客户端请求超时", ResultLevel.WARN),
    /**
     * 远程调用异常
     */
    REMOTE_EXCEPTION(303, "远程调用异常", ResultLevel.WARN),
    /**
     * 客户端请求服务端无响应
     */
    REMOTE_NO_RESPONSE(305, "服务端无响应", ResultLevel.WARN),
    /**
     * 客户端请求失败
     */
    REQUEST_FAILED(306, "客户端请求失败", ResultLevel.WARN),

    /**
     * 服务不可用
     */
    RPC_SERVICE_NOT_AVAILABLE(400, "服务不可用", ResultLevel.WARN),

    /**
     * RPC调用异常
     */
    RPC_INVOKE_FAILED(401, "RPC调用异常", ResultLevel.WARN),

    //
    ;

    private int code;

    private String message;

    private ResultLevel type;

    NetResultCode(int code, String message, ResultLevel type) {
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
    public ResultLevel getLevel() {
        return type;
    }

}
