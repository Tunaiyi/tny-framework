package com.tny.game.net.command.filter.string;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.filter.AbstractParamFilter;
import com.tny.game.net.command.filter.string.annotation.StrLength;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.suite.app.CoreResponseCode;

public class StringLengthLimitFilter extends AbstractParamFilter<Object, StrLength, String> {

    private final static StringLengthLimitFilter INSTANCE = new StringLengthLimitFilter();

    public static StringLengthLimitFilter getInstance() {
        return INSTANCE;
    }

    private StringLengthLimitFilter() {
        super(StrLength.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> request, int index, StrLength annotation, String param) {
        if (param == null)
            return CoreResponseCode.ILLEGAL_PARAMETERS;
        int length = param.length();
        if (length < annotation.low() || annotation.high() < length) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串长度超过 {} - {} 范围",
                    request.getUserID(), request.getProtocol(),
                    index, param, annotation.low(), annotation.high());
            return CoreResponseCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }
}
