package com.tny.game.suite.net.filter;

import com.tny.game.common.lifecycle.ServerPrepareStart;
import com.tny.game.common.result.ResultCode;
import com.tny.game.common.word.WordsFilter;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.filter.AbstractParamFilter;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.suite.net.filter.annotation.TextFilter;
import com.tny.game.suite.utils.SuiteResultCode;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.tny.game.suite.SuiteProfiles.*;

@Component
@Profile({SERVER, GAME})
public class TextLimit<UID> extends AbstractParamFilter<UID, TextFilter, String> implements ApplicationContextAware, ServerPrepareStart {

    private List<WordsFilter> wordsFilters;

    private ApplicationContext applicationContext;

    private Pattern fullPattern = Pattern.compile("[\\p{P}a-zA-Z0-9\u4e00-\u9fff]*");

    protected TextLimit() {
        super(TextFilter.class, String.class);
    }

    @Override
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel<UID> tunnel, Message<UID> message, int index, TextFilter annotation, String param) {
        int size = param.length();
        if (size < annotation.lowLength() || annotation.highLength() < size) {
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 超出 {} - {} 范围",
                    message.getUserID(), message.getProtocol(),
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
    public void prepareStart() throws Throwable {
        Map<String, WordsFilter> filtersMap = this.applicationContext.getBeansOfType(WordsFilter.class);
        wordsFilters = filtersMap.values().stream()
                .sorted(Comparator.comparing(WordsFilter::order))
                .collect(Collectors.toList());
    }

}
