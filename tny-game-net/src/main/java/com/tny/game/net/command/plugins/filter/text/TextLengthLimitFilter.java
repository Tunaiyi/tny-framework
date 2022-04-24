package com.tny.game.net.command.plugins.filter.text;

import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.plugins.filter.text.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public class TextLengthLimitFilter extends AbstractParamFilter<Object, TextLength, String> {

    private final static TextLengthLimitFilter INSTANCE = new TextLengthLimitFilter();

    public static TextLengthLimitFilter getInstance() {
        return INSTANCE;
    }

    private TextLengthLimitFilter() {
        super(TextLength.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message message, int index, TextLength annotation,
            String param) {
        if (param == null) {
            return code(NetResultCode.SERVER_ILLEGAL_PARAMETERS, annotation.illegalCode());
        }
        int length = param.length();
        if (length < annotation.low() || annotation.high() < length) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串长度超过 {} - {} 范围",
                    tunnel.getUserId(), head.getId(),
                    index, param, annotation.low(), annotation.high());
            return code(NetResultCode.SERVER_ILLEGAL_PARAMETERS, annotation.illegalCode());
        }
        return ResultCode.SUCCESS;
    }

}
