package com.tny.game.suite.net.filter;

import com.tny.game.common.lifecycle.*;
import com.tny.game.common.result.*;
import com.tny.game.common.word.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import com.tny.game.suite.net.filter.annotation.*;
import com.tny.game.suite.utils.*;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({GAME, TEXT_FILTER})
public class TextLimit<UID> extends AbstractParamFilter<UID, TextFilter, String> implements ApplicationContextAware, AppPrepareStart {

    private List<WordsFilter> wordsFilters;

    private ApplicationContext applicationContext;

    private Pattern fullPattern = Pattern.compile("[\\p{P}a-zA-Z0-9\u4e00-\u9fff]*");

    protected TextLimit() {
        super(TextFilter.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message, int index, TextFilter annotation,
            String param) {
        int size = param.length();
        if (size < annotation.lowLength() || annotation.highLength() < size) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    message.getUserId(), message.getProtocol(),
                    index, size, annotation.lowLength(), annotation.highLength());
            return SuiteResultCode.TEXT_LENGTH_ILLEGAL;
        }
        for (WordsFilter filter : wordsFilters) {
            if (filter.hasBadWords(param))
                return SuiteResultCode.TEXT_FILTER_WORD;
        }
        return ResultCode.SUCCESS;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void prepareStart() throws Exception {
        Map<String, WordsFilter> filtersMap = this.applicationContext.getBeansOfType(WordsFilter.class);
        wordsFilters = filtersMap.values().stream()
                                 .sorted(Comparator.comparing(WordsFilter::order))
                                 .collect(Collectors.toList());
    }

}
