/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.oplog.*;
import com.tny.game.oplog.log4j2.*;
import com.tny.game.oplog.simple.*;
import com.tny.game.oplog.utils.*;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.Message;
import org.slf4j.*;

import java.nio.charset.Charset;
import java.util.*;

@Plugin(name = "KafkaOpLogLayout", category = "Core", elementType = "layout", printObject = true)
public class KafkaOpLogLayout extends AbstractStringLayout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpLogJsonLayout.class);

    private final Map<String, String> result;

    protected KafkaOpLogLayout(Charset charset) {
        super(charset);
        Map<String, String> result = new HashMap<>();
        result.put("version", "2.0");
        this.result = Collections.unmodifiableMap(result);
    }

    @Override
    public String toSerializable(LogEvent event) {
        Message message = event.getMessage();
        if (message instanceof LogMessage) {
            LogMessage logMessage = (LogMessage)message;
            Log loggable = logMessage.getLog();
            ObjectMapper mapper = OpLogMapper.getMapper();
            try {
                LogRecord record = new LogRecord(loggable.getLogId(), loggable.getType(), mapper.writeValueAsString(logMessage.getLog()));
                return mapper.writeValueAsString(record);// + "\r\n";
            } catch (JsonProcessingException e) {
                LOGGER.error("OpLogLayout toSerializable exception", e);
            }
        }
        return "";
    }

    @Override
    public Map<String, String> getContentFormat() {
        return this.result;
    }

    @PluginFactory
    public static OpLogJsonLayout createLayout(
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset
    ) {
        return new OpLogJsonLayout(charset);
    }

}
