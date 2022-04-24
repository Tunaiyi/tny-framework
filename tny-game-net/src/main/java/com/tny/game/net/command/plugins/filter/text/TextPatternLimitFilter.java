package com.tny.game.net.command.plugins.filter.text;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.plugins.filter.text.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Map;
import java.util.regex.Pattern;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public class TextPatternLimitFilter extends AbstractParamFilter<Object, PatternMatch, String> {

    private Map<String, Pattern> patternMap = new CopyOnWriteMap<>();

    private final static TextPatternLimitFilter INSTANCE = new TextPatternLimitFilter();

    public static TextPatternLimitFilter getInstance() {
        return INSTANCE;
    }

    private TextPatternLimitFilter() {
        super(PatternMatch.class);
    }

    private Pattern getPattern(String string) {
        Pattern pattern = this.patternMap.get(string);
        if (pattern != null) {
            return pattern;
        }
        pattern = Pattern.compile(string);
        Pattern oldPattern = this.patternMap.putIfAbsent(string, pattern);
        return oldPattern == null ? pattern : oldPattern;
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<Object> tunnel, Message message, int index, PatternMatch annotation,
            String param) {
        if (!this.getPattern(annotation.value()).matcher(param).matches()) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}",
                    tunnel.getUserId(), head.getId(),
                    index, param, annotation.value());
            return code(NetResultCode.SERVER_ILLEGAL_PARAMETERS, annotation.illegalCode());
        }
        return ResultCode.SUCCESS;
    }

}
