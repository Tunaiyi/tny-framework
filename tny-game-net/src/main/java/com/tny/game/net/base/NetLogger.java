/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

public class NetLogger {

    public static final ProcessWatcher NET_TRACE_ALL_WATCHER = ProcessWatcher.of(MessageCommand.class + ".track_all_receive_send",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_ALL = ProcessWatcher.of(MessageCommand.class + ".track_input-all", TrackPrintOption.CLOSE);
    //.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_DECODE_WATCHER = ProcessWatcher.of(MessageCommand.class + ".track_input-decode",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".track_input-read_to_tunnel", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".track_input-tunnel_to_execute", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_BOX_PROCESS_WATCHER = ProcessWatcher.of(MessageCommand.class + ".track_input-box_process",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".track_input-execute_command", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_OUTPUT_WRITTEN_WATCHER = ProcessWatcher.of(MessageCommand.class + ".track_output-all",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_ENCODE_WATCHER = ProcessWatcher.of(MessageCommand.class + ".track_output-encode",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".track_output-write_to_encode", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_WATCHER = ProcessWatcher.of(MessageCommand.class + ".command_exe_invoke",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_GET_CONTROLLER_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".command_exe_invoke-get_controller", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_AUTHENTICATE_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".command_exe_invoke-check_authenticate", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_INVOKABLE_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".command_exe_invoke-check_invokable", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_BEFORE_PLUGINS_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".command_exe_invoke-before_plugins", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_INVOKING_WATCHER = ProcessWatcher.of(MessageCommand.class + ".command_exe_invoke-invoking",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_AFTER_PLUGINS_WATCHER = ProcessWatcher.of(
            MessageCommand.class + ".command_exe_invoke-after_plugins", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_HANDLE_RESULT_WATCHER = ProcessWatcher.of(MessageCommand.class + ".command_exe_handle_result",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    //    public static final ProcessWatcher MSG_TICK_2_WATCHER = ProcessWatcher
    //            .of(MessageCommand.class + ".tick2", TrackPrintOption.CLOSE)
    //            ;//.schedule(15, TimeUnit.SECONDS);
    //
    //    public static final ProcessWatcher MSG_TICK_3_WATCHER = ProcessWatcher
    //            .of(MessageCommand.class + ".tick3", TrackPrintOption.CLOSE)
    //            ;//.schedule(15, TimeUnit.SECONDS);

    public static final AttrKey<ProcessTracer> NET_TRACE_ALL_ATTR_KEY = AttrKeys.key(ProcessWatcher.class, "NET_TRACE_ALL_ATTR_KEY");

    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY = AttrKeys.key(ProcessWatcher.class,
            "NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY");

    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_ALL_ATTR_KEY = AttrKeys.key(ProcessWatcher.class, "NET_TRACE_INPUT_ALL_ATTR_KEY");

    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY = AttrKeys.key(ProcessWatcher.class,
            "NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY");

    public static final AttrKey<ProcessTracer> MESSAGE_DISPATCH_TO_EXECUTE_ATTR_KEY = AttrKeys.key(ProcessWatcher.class,
            "MESSAGE_DISPATCH_TO_EXECUTE_ATTR_KEY");

    public static final AttrKey<ProcessTracer> NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY = AttrKeys.key(ProcessWatcher.class,
            "NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY");

    private static class WatcherAttribute {

        private final AttrKey<ProcessTracer> key;

        private final ProcessWatcher watcher;

        private WatcherAttribute(ProcessWatcher watcher, AttrKey<ProcessTracer> key) {
            this.key = key;
            this.watcher = watcher;
        }

    }

    public static final WatcherAttribute NET_TRACE_ALL_ATTR = new WatcherAttribute(NET_TRACE_ALL_WATCHER, NET_TRACE_ALL_ATTR_KEY);

    public static final WatcherAttribute NET_TRACE_INPUT_ALL_ATTR = new WatcherAttribute(NET_TRACE_INPUT_ALL, NET_TRACE_INPUT_ALL_ATTR_KEY);

    public static final WatcherAttribute NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR = new WatcherAttribute(NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER,
            NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY);

    public static final WatcherAttribute NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR = new WatcherAttribute(NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_WATCHER,
            NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY);

    public static final WatcherAttribute NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR = new WatcherAttribute(NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER,
            NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY);

    public static void trace(WatcherAttribute attribute, Message message) {
        if (attribute.watcher.isSchedule()) {
            ProcessTracer tracer = attribute.watcher.trace();
            message.attributes().setAttribute(attribute.key, tracer);
        }
    }

    public static void traceDone(WatcherAttribute attribute, Message message) {
        if (message == null) {
            return;
        }
        Attributes attributes = message.attributes();
        if (!attributes.isEmpty()) {
            ProcessTracer tracer = attributes.getAttribute(attribute.key);
            if (tracer != null) {
                tracer.done();
            }
        }

    }

    public static final String CODER = "netCoder";

    public static final String CHECKER = "com.tny.game.net.checker";

    public static final String DISPATCHER = "com.tny.game.net.dispatcher";

    public static final String CLIENT = "com.tny.game.net.client";

    public static final String SESSION = "com.tny.game.net.session";

    public static final String EXECUTOR = "com.tny.game.net.executor";

    private static final String RECEIVE = "com.tny.game.net.message.receive.{}";

    private static final String SEND = "com.tny.game.net.message.send.{}";

    private static final String RECEIVE_RQS = "com.tny.game.net.message.receive.{}.Request";

    private static final String RECEIVE_RSP = "com.tny.game.net.message.receive.{}.Response";

    private static final String RECEIVE_PUSH = "com.tny.game.net.message.receive.{}.Push";

    private static final String SEND_RQS = "com.tny.game.net.message.send.{}.Request";

    private static final String SEND_RSP = "com.tny.game.net.message.send.{}.Response";

    private static final String SEND_PUSH = "com.tny.game.net.message.send.{}.Push";

    private static class NetLoggerGroup {

        private final String userType;

        private final Logger receiveLogger;

        private final Logger pushReceiveLogger;

        private final Logger requestReceiveLogger;

        private final Logger responseReceiveLogger;

        private final Logger sendLogger;

        private final Logger pushSendLogger;

        private final Logger requestSendLogger;

        private final Logger responseSendLogger;

        private static final ConcurrentMap<String, NetLoggerGroup> NET_LOGGER_GROUP_MAP = new ConcurrentHashMap<>();

        private static NetLoggerGroup of(String userType) {
            return NET_LOGGER_GROUP_MAP.computeIfAbsent(userType, NetLoggerGroup::new);
        }

        private NetLoggerGroup(String userType) {
            this.userType = userType;
            this.receiveLogger = LoggerFactory.getLogger(format(NetLogger.RECEIVE, userType));
            this.pushReceiveLogger = LoggerFactory.getLogger(format(NetLogger.RECEIVE_PUSH, userType));
            this.requestReceiveLogger = LoggerFactory.getLogger(format(NetLogger.RECEIVE_RQS, userType));
            this.responseReceiveLogger = LoggerFactory.getLogger(format(NetLogger.RECEIVE_RSP, userType));

            this.sendLogger = LoggerFactory.getLogger(format(NetLogger.SEND, userType));
            this.pushSendLogger = LoggerFactory.getLogger(format(NetLogger.SEND_PUSH, userType));
            this.requestSendLogger = LoggerFactory.getLogger(format(NetLogger.SEND_RQS, userType));
            this.responseSendLogger = LoggerFactory.getLogger(format(NetLogger.SEND_RSP, userType));
        }

        public String getUserType() {
            return userType;
        }

        public Logger forReceiveMessage(Message message) {
            switch (message.getMode()) {
                case PUSH:
                    return pushReceiveLogger;
                case REQUEST:
                    return requestReceiveLogger;
                case RESPONSE:
                    return responseReceiveLogger;
                default:
                    return receiveLogger;
            }
        }

        public Logger forSendMessage(Message message) {
            switch (message.getMode()) {
                case PUSH:
                    return pushSendLogger;
                case REQUEST:
                    return requestSendLogger;
                case RESPONSE:
                    return responseSendLogger;
                default:
                    return sendLogger;
            }
        }

    }

    private static Logger getLoggerByMessage(Message message, Logger pushLogger, Logger rqsLogger, Logger rspLogger) {
        if (message == null) {
            return null;
        }
        switch (message.getMode()) {
            case PUSH:
                return pushLogger;
            case REQUEST:
                return rqsLogger;
            case RESPONSE:
                return rspLogger;
        }
        return null;
    }

    private static Logger getSendLogger(String userType, Message message) {
        return NetLoggerGroup.of(userType).forSendMessage(message);
    }

    private static Logger getReceiveLogger(String userType, Message message) {
        return NetLoggerGroup.of(userType).forReceiveMessage(message);
    }

    //    private static Object[] toMessageArgs(Message message, Object[] args) {
    //        Object[] msgArgs = new Object[5];
    //        MessageHead head = message.getHead();
    //        msgArgs[0] = message.getMode();
    //        msgArgs[1] = head.getId();
    //        msgArgs[2] = head.getId();
    //        msgArgs[3] = head.getToMessage();
    //        msgArgs[4] = message.getBody(Object.class);
    //        if (args.length > 0) {
    //            msgArgs = ArrayUtils.addAll(msgArgs, args);
    //        }
    //        return msgArgs;
    //    }

    public static void logSend(Supplier<Tunnel<?>> tunnelSupplier, Message message) {
        logSend(tunnelSupplier.get(), message);
    }

    private static void logSend(Tunnel<?> tunnel, Message message) {
        Logger logger = getSendLogger(tunnel.getUserGroup(), message);
        if (logger != null && logger.isDebugEnabled()) {
            doLogSend(logger, tunnel, message);
        }

    }

    public static void doLogSend(Logger logger, Tunnel<?> tunnel, Message message) {
        MessageHead head = message.getHead();
        logger.debug("\n    # Tunnel {} >> [发送]消息 {}", tunnel, message);
    }

    public static void logReceive(Supplier<Tunnel<?>> tunnelSupplier, Message message) {
        logReceive(tunnelSupplier.get(), message);
    }

    public static void logReceive(Tunnel<?> tunnel, Message message) {
        Logger logger = getReceiveLogger(tunnel.getUserGroup(), message);
        if (logger != null && logger.isDebugEnabled()) {
            doLogReceive(logger, tunnel, message);
        }
    }

    private static void doLogReceive(Logger logger, Tunnel<?> tunnel, Message message) {
        MessageHead head = message.getHead();
        logger.debug("\n    # Tunnel {} << [接收]消息 {}", tunnel, message);
    }
    // public static void log(Session session, Protocol protocol, ResultCode code, Object body) {
    //     if (RECEIVE_LOGGER.isDebugEnabled())
    //         RECEIVE_LOGGER.debug("\n##响应到 [{}|{}|{}] \n##响应 - 请求 [{}] {} ##响应码 : {} \n##响应消息体 : {}",
    //                 session.getGroup(), session.getHostName(), session.getUID(),
    //                 0, protocol.getProtocol(),
    //                 code.getCode(), body);
    // }
    //
    // public static void log(Session session, Protocol protocol, int code, Object body) {
    //     if (LOG_REQUEST.isDebugEnabled())
    //         RECEIVE_LOGGER.debug("\n##响应到 [{}|{}|{}] \n##响应 - 请求 [{}] {} ##响应码 : {} \n##响应消息体 : {}",
    //                 session.getGroup(), session.getHostName(), session.getUID(),
    //                 0, protocol.getProtocol(),
    //                 code, body);
    // }

}