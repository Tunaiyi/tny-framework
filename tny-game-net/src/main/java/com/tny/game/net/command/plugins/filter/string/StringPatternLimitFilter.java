package com.tny.game.net.command.plugins.filter.string;

import com.tny.game.common.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.plugins.filter.string.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Map;
import java.util.regex.Pattern;

public class StringPatternLimitFilter extends AbstractParamFilter<Object, PatternMatch, String> {

    private Map<String, Pattern> patternMap = new CopyOnWriteMap<>();

    private final static StringPatternLimitFilter INSTANCE = new StringPatternLimitFilter();

    public static StringPatternLimitFilter getInstance() {
        return INSTANCE;
    }

    private StringPatternLimitFilter() {
        super(PatternMatch.class);
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
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message<Object> message, int index, PatternMatch annotation,
            String param) {
        if (!this.getPattern(annotation.pattern()).matcher(param).matches()) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}",
                    message.getUserId(), head.getId(),
                    index, param, annotation.pattern());
            return NetResultCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }
}
