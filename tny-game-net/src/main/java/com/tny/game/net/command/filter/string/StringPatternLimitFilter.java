package com.tny.game.net.command.filter.string;

import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.NetResultCode;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.command.filter.AbstractParamFilter;
import com.tny.game.net.command.filter.string.annotation.PatternMatch;
import com.tny.game.net.transport.Tunnel;
import com.tny.game.net.transport.message.*;

import java.util.Map;
import java.util.regex.Pattern;

public class StringPatternLimitFilter extends AbstractParamFilter<Object, PatternMatch, String> {

    private Map<String, Pattern> patternMap = new CopyOnWriteMap<>();

    private final static StringPatternLimitFilter INSTANCE = new StringPatternLimitFilter();

    public static StringPatternLimitFilter getInstance() {
        return INSTANCE;
    }

    private StringPatternLimitFilter() {
        super(PatternMatch.class, String.class);
    }

    private Pattern getPattern(String string) {
        Pattern pattern = this.patternMap.get(string);
        if (pattern != null)
            return pattern;
        pattern = Pattern.compile(string);
        Pattern oldPattern = this.patternMap.putIfAbsent(string, pattern);
        return oldPattern == null ? pattern : oldPattern;
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> message, int index, PatternMatch annotation, String param) {
        if (!this.getPattern(annotation.pattern()).matcher(param).matches()) {
            MessageHeader header = message.getHeader();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}",
                    message.getUserID(), header.getProtocol(),
                    index, param, annotation.pattern());
            return NetResultCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }
}
