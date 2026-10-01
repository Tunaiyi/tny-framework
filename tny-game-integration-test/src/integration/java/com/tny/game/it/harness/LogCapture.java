/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.it.harness;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.*;

import java.util.*;
import java.util.stream.*;

/**
 * 集成测试用内存日志捕获器（specs R4"损坏报文被拒且可观察"的告警可定位断言支撑）：
 * 以 WARN 阈值挂到 log4j2 root logger，close 时摘除。事件文本 = 格式化消息 + 异常 toString。
 */
public final class LogCapture implements AutoCloseable {

    private static final String APPENDER_NAME = "it-log-capture";

    private final CapturingAppender appender;

    private final LoggerConfig rootConfig;

    private final LoggerContext context;

    private LogCapture(CapturingAppender appender, LoggerConfig rootConfig, LoggerContext context) {
        this.appender = appender;
        this.rootConfig = rootConfig;
        this.context = context;
    }

    public static LogCapture attach() {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        CapturingAppender capturing = new CapturingAppender();
        capturing.start();
        configuration.addAppender(capturing);
        LoggerConfig root = configuration.getRootLogger();
        root.addAppender(capturing, Level.WARN, null);
        loggerContext.updateLoggers();
        return new LogCapture(capturing, root, loggerContext);
    }

    /** 已捕获事件文本流（格式化消息 + 异常）。 */
    public Stream<String> events() {
        return appender.events.stream().map(event -> {
            String message = event.getMessage() != null ? event.getMessage().getFormattedMessage() : "";
            Throwable thrown = event.getThrown();
            return thrown != null ? message + " | " + thrown : message;
        });
    }

    @Override
    public void close() {
        rootConfig.removeAppender(APPENDER_NAME);
        context.updateLoggers();
        appender.stop();
    }

    private static final class CapturingAppender extends AbstractAppender {

        private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<>());

        CapturingAppender() {
            super(APPENDER_NAME, null, null, true, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(LogEvent event) {
            events.add(event.toImmutable());
        }
    }

}
