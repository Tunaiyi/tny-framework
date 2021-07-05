package com.tny.game.starter.net.netty4.configuration;

import com.tny.game.common.word.*;
import com.tny.game.net.command.plugins.filter.text.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;

import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@ConditionalOnProperty(value = "tny.net.filter.words.enable", havingValue = "true")
public class TextFilterAutoConfiguration {

    @Value("{tny.net.filter.text_check.file_url:words.txt}")
    private String file;

    @Value("{tny.net.filter.text_check.hide_symbol:*}")
    private String hideSymbol;

    @Bean
    public WordsFilter wordsFilter() throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter(this.file, this.hideSymbol);
        filter.load();
        return filter;
    }

    @Bean
    @ConditionalOnBean(WordsFilter.class)
    public TextCheckFilter<?> textCheckFilter(List<WordsFilter> wordsFilters) {
        return new TextCheckFilter<>();
    }

}
