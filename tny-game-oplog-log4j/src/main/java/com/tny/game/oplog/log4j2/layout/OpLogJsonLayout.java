package com.tny.game.oplog.log4j2.layout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.oplog.Log;
import com.tny.game.oplog.log4j2.LogMessage;
import com.tny.game.oplog.record.OperateRecord;
import com.tny.game.oplog.record.UserStuffRecord;
import com.tny.game.oplog.utils.OpLogMapper;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.tny.game.common.utils.ObjectAide.*;
import static org.slf4j.LoggerFactory.*;

@Plugin(name = "OpLogJsonLayout", category = "Core", elementType = "layout", printObject = true)
public class OpLogJsonLayout extends AbstractStringLayout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = getLogger(OpLogJsonLayout.class);

    private final Map<String, String> result;

    private AtomicLong logIDCounter = new AtomicLong(System.currentTimeMillis());

    protected OpLogJsonLayout(Charset charset) {
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
            Log loggable = logMessage.getLog();
            ObjectMapper mapper = OpLogMapper.getMapper();
            try {
                Object log = null;
                if (loggable instanceof OperateRecord) {
                    log = new JsonOperateRecord(logIDCounter.incrementAndGet(), as(loggable), mapper);
                } else if (loggable instanceof UserStuffRecord) {
                    log = new JsonUserStuffRecord(logIDCounter.incrementAndGet(), as(loggable));
                }
                if (log != null)
                    return mapper.writeValueAsString(log) + "\r\n";
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
