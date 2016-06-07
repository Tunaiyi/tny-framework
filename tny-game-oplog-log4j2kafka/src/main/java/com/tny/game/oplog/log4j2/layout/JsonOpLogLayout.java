package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.oplog.Loggable;
import com.tny.game.oplog.log4j2.LogMessage;
import com.tny.game.oplog.log4j2.OpLogMapper;
import com.tny.game.oplog.simple.LogRecord;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Plugin(name = "JsonOpLogLayout", category = "Core", elementType = "layout", printObject = true)
public class JsonOpLogLayout extends AbstractStringLayout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonOpLogLayout.class);

    private final Map<String, String> result;

    protected JsonOpLogLayout(Charset charset) {
        super(charset);
        Map<String, String> result = new HashMap<>();
        result.put("version", "2.0");
        this.result = Collections.unmodifiableMap(result);
    }

    @Override
    public String toSerializable(LogEvent event) {
        Message message = event.getMessage();
        if (message instanceof LogMessage) {
            LogMessage logMessage = (LogMessage) message;
            Loggable loggable = logMessage.getLog();
            ObjectMapper mapper = OpLogMapper.getMapper();
            try {
                LogRecord record = new LogRecord(loggable.getLogID(), loggable.getType(), mapper.writeValueAsString(logMessage.getLog()));
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
    public static JsonOpLogLayout createLayout(
            @PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset
    ) {
        return new JsonOpLogLayout(charset);
    }

}
