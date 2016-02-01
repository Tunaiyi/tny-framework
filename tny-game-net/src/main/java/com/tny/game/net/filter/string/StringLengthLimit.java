package com.tny.game.net.filter.string;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.filter.AbstractParamFilter;
import com.tny.game.net.filter.string.annotation.StrLength;
import org.springframework.stereotype.Component;

@Component
public class StringLengthLimit extends AbstractParamFilter<StrLength, String> {

    protected StringLengthLimit() {
        super(StrLength.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodHolder holder, Request request, int index, StrLength annotation, String param) {
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
