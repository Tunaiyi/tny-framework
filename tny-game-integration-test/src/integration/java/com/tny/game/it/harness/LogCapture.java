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
