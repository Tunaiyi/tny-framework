package com.tny.game.net.filter.string;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.filter.AbstractParamFilter;
import com.tny.game.net.filter.string.annotation.PatternMatch;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

@Component
public class StringPatternMatchLimit extends AbstractParamFilter<PatternMatch, String> {

    private ConcurrentMap<String, Pattern> patternMap = new ConcurrentHashMap<String, Pattern>();

    protected StringPatternMatchLimit() {
        super(PatternMatch.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodHolder holder, Request request, int index, PatternMatch annotation, String param) {
        if (!this.getPattern(annotation.pattern()).matcher(param).matches()) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}",
                    request.getUserID(), request.getProtocol(),
                    index, param, annotation.pattern());
            return CoreResponseCode.ILLEGAL_PARAMETERS;
        }
        return ResultCode.SUCCESS;
    }

    private Pattern getPattern(String string) {
        Pattern pattern = this.patternMap.get(string);
        if (pattern != null)
            return pattern;
        pattern = Pattern.compile(string);
        Pattern oldPattern = this.patternMap.putIfAbsent(string, pattern);
        return oldPattern == null ? pattern : oldPattern;
    }

}
