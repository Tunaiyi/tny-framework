/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.command.plugins.filter.text;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.plugins.filter.*;
import com.tny.game.net.command.plugins.filter.text.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.Map;
import java.util.regex.Pattern;

import static com.tny.game.net.command.plugins.filter.FilterCode.*;

public class TextPatternLimitFilter extends AbstractParamFilter<PatternMatch, String> {

    private final Map<String, Pattern> patternMap = new CopyOnWriteMap<>();

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
    protected ResultCode doFilter(MethodControllerHolder holder, Tunnel tunnel, Message message, int index, PatternMatch annotation,
            String param) {
        if (!this.getPattern(annotation.value()).matcher(param).matches()) {
            MessageHead head = message.getHead();
            LOGGER.warn("{} 玩家请求 协议[{}] 第{}个参数 [{}] 的字符串无法匹配正则表达式{}", tunnel.getIdentify(), head.getId(), index, param,
                    annotation.value());
            return code(NetResultCode.SERVER_ILLEGAL_PARAMETERS, annotation.illegalCode());
        }
        return ResultCode.SUCCESS;
    }

}
