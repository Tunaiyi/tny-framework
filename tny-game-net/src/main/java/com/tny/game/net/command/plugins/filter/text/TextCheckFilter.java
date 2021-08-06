package com.tny.game.net.command.plugins.filter.text;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.io.word.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.plugins.filter.text.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.List;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public class TextCheckFilter<UID> extends AbstractParamFilter<UID, TextCheck, String> {

    private List<WordsFilter> wordsFilters;

    private ResultCode lengthIllegalCode = NetResultCode.ILLEGAL_PARAMETERS;

    private ResultCode contentIllegalCode = NetResultCode.ILLEGAL_PARAMETERS;

    public TextCheckFilter() {
        super(TextCheck.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message message, int index, TextCheck annotation,
            String param) {
        int size = param.length();
        if (size < annotation.lowLength() || annotation.highLength() < size) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    tunnel.getUserId(), message.getProtocolId(),
                    index, size, annotation.lowLength(), annotation.highLength());
            return code(this.lengthIllegalCode, annotation.lengthIllegalCode(), annotation.illegalCode());
        }
        for (WordsFilter filter : this.wordsFilters) {
            if (filter.hasBadWords(param)) {
                return code(this.contentIllegalCode, annotation.contentIllegalCode(), annotation.illegalCode());
            }
        }
        return ResultCode.SUCCESS;
    }

    public TextCheckFilter<UID> setWordsFilters(List<WordsFilter> wordsFilters) {
        this.wordsFilters = ImmutableList.copyOf(wordsFilters);
        return this;
    }

    public TextCheckFilter<UID> setLengthIllegalCode(ResultCode lengthIllegalCode) {
        this.lengthIllegalCode = lengthIllegalCode;
        return this;
    }

    public TextCheckFilter<UID> setContentIllegalCode(ResultCode contentIllegalCode) {
        this.contentIllegalCode = contentIllegalCode;
        return this;
    }

}
