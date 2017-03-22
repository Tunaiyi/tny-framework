package com.tny.game.suite.net.filter;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.filter.AbstractParamFilter;
import com.tny.game.suite.net.filter.annotation.NameFilter;
import com.tny.game.suite.utils.SuiteResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME})
public class NameLimit extends AbstractParamFilter<NameFilter, String> {

    @Autowired
    private WordsFilter wordsFilter;

    private Pattern fullPattern = Pattern.compile("[a-zA-Z0-9\u4e00-\u9fa5]*");

    protected NameLimit() {
        super(NameFilter.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Request request, int index, NameFilter annotation, String param) {
        if (System.getProperty("com.sd.fol.name.test", "").equals("true"))
            return ResultCode.SUCCESS;
        if (!this.fullPattern.matcher(param).matches()) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}",
                    request.getUserID(), request.getProtocol(),
                    index, param, this.fullPattern.pattern());
            return SuiteResultCode.NAME_CONTENT_ILLEGAL;
        }
        int size = param.length();
        if (size < annotation.lowLength() || annotation.highLength() < size) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    request.getUserID(), request.getProtocol(),
                    index, size, annotation.lowLength(), annotation.highLength());
            return SuiteResultCode.NAME_LENGTH_ILLEGAL;
        }
        if (this.wordsFilter.hasBadWords(param)) {
            return SuiteResultCode.NAME_FILTER_WORD;
        }
        return ResultCode.SUCCESS;
    }
}
