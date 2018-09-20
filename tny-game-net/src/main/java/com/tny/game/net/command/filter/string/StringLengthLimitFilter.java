package com.tny.game.net.command.filter.string;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.filter.AbstractParamFilter;
import com.tny.game.net.command.filter.string.annotation.StrLength;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.message.*;

public class StringLengthLimitFilter extends AbstractParamFilter<Object, StrLength, String> {

    private final static StringLengthLimitFilter INSTANCE = new StringLengthLimitFilter();

    public static StringLengthLimitFilter getInstance() {
        return INSTANCE;
    }

    private StringLengthLimitFilter() {
        super(StrLength.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> message, int index, StrLength annotation, String param) {
        if (param == null)
            return NetResultCode.ILLEGAL_PARAMETERS;
        int length = param.length();
        if (length < annotation.low() || annotation.high() < length) {
            MessageHeader header = message.getHeader();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串长度超过 {} - {} 范围",
                    message.getUserID(), header.getProtocol(),
                    index, param, annotation.low(), annotation.high());
            return NetResultCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }
}
