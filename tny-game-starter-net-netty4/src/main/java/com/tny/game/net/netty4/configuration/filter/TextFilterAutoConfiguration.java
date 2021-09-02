package com.tny.game.net.netty4.configuration.filter;

import com.tny.game.common.io.word.*;
import com.tny.game.net.command.plugins.filter.text.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
@ConditionalOnProperty(value = "tny.net.filter.words.enable", havingValue = "true")
@EnableConfigurationProperties(TextFilterProperties.class)
public class TextFilterAutoConfiguration {

	@Resource
	private TextFilterProperties textFilterProperties;

	@Bean
	public WordsFilter wordsFilter() throws Exception {
		LocalWordsFilter filter = new LocalWordsFilter(this.textFilterProperties.getFile(), this.textFilterProperties.getHideSymbol());
		filter.load();
		return filter;
	}

	@Bean
	@ConditionalOnBean(WordsFilter.class)
	public TextCheckFilter<?> textCheckFilter(List<WordsFilter> wordsFilters) {
		return new TextCheckFilter<>();
	}

}
