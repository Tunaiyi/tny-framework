package com.tny.game.oplog.log4j2.appender;

import com.tny.game.oplog.Log;
import com.tny.game.oplog.log4j2.KafkaOplogManager;
import com.tny.game.oplog.log4j2.LogMessage;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.util.StringEncoder;
import org.apache.logging.log4j.message.Message;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Plugin(name = "KafkaOplog", category = "Core", elementType = "appender", printObject = true)
public class KafkaOpLogAppender extends AbstractAppender {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @PluginFactory
    public static KafkaOpLogAppender createAppender(
            @PluginElement("Layout") final Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @Required(message = "No name provided for KafkaAppender") @PluginAttribute("name") final String name,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) final boolean ignoreExceptions,
            @Required(message = "No topic provided for KafkaAppender") @PluginAttribute("topic") final String topic,
            @PluginElement("Properties") final Property[] properties,
            @PluginConfiguration final Configuration configuration) {
        final KafkaOplogManager kafkaManager = new KafkaOplogManager(configuration.getLoggerContext(), name, topic, properties);
        return new KafkaOpLogAppender(name, layout, filter, ignoreExceptions, kafkaManager);
    }

    private final KafkaOplogManager manager;

    private KafkaOpLogAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter, final boolean ignoreExceptions, final KafkaOplogManager manager) {
        super(name, filter, layout, ignoreExceptions);
        this.manager = manager;
    }

    @Override
    public void append(final LogEvent event) {
        if (event.getLoggerName().startsWith("org.apache.kafka")) {
            LOGGER.warn("Recursive logging from [{}] for appender [{}].", event.getLoggerName(), getName());
        } else {
            try {
                Layout<? extends Serializable> layout = getLayout();
                byte[] data;
                if (layout != null) {
                    if (layout instanceof SerializedLayout) {
                        byte[] header = layout.getHeader();
                        byte[] body = layout.toByteArray(event);
                        data = new byte[header.length + body.length];
                        System.arraycopy(header, 0, data, 0, header.length);
                        System.arraycopy(body, 0, data, header.length, body.length);
                    } else {
                        data = layout.toByteArray(event);
                    }
                } else {
                    data = StringEncoder.toBytes(event.getMessage().getFormattedMessage(), StandardCharsets.UTF_8);
                }
                if (data.length > 0) {
                    Message message = event.getMessage();
                    if (message instanceof LogMessage) {
                        LogMessage logMessage = (LogMessage) message;
                        Log loggable = logMessage.getLog();
                        String key = loggable.getServerId() + "-" + loggable.getDate();
                        manager.send(loggable.getServerId(), key, data);
                    }
                }
            } catch (final Exception e) {
                LOGGER.error("Unable to write to Kafka [{}] for appender [{}].", manager.getName(), getName(), e);
                throw new AppenderLoggingException("Unable to write to Kafka in appender: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void start() {
        super.start();
        manager.startup();
    }

    @Override
    public void stop() {
        super.stop();
        manager.release();
    }

}
