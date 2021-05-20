package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NetLogger {

    public static final ProcessWatcher NET_TRACE_ALL_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_all_receive_send", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_ALL = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-all", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_DECODE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-decode", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-read_to_tunnel", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-tunnel_to_execute", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_BOX_PROCESS_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-box_process", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);
        
    public static final ProcessWatcher NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_input-execute_command", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_OUTPUT_WRITTEN_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_output-all", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_ENCODE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_output-encode", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".trace_output-write_to_encode", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_GET_CONTROLLER_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-get_controller", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_AUTHENTICATE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-check_authenticate", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_INVOKABLE_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-check_invokable", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_BEFORE_PLUGINS_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-before_plugins", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_INVOKING_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-invoking", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_AFTER_PLUGINS_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_invoke-after_plugins", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_HANDLE_RESULT_WATCHER = ProcessWatcher
            .of(MessageCommand.class + ".command_exe_handle_result", TrackPrintOption.CLOSE)
            .schedule(15, TimeUnit.SECONDS);

    //    public static final ProcessWatcher MSG_TICK_2_WATCHER = ProcessWatcher
    //            .of(MessageCommand.class + ".tick2", TrackPrintOption.CLOSE)
    //            .schedule(15, TimeUnit.SECONDS);
    //
    //    public static final ProcessWatcher MSG_TICK_3_WATCHER = ProcessWatcher
    //            .of(MessageCommand.class + ".tick3", TrackPrintOption.CLOSE)
    //            .schedule(15, TimeUnit.SECONDS);

    public static final AttrKey<ProcessTracer> NET_TRACE_ALL_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "NET_TRACE_ALL_ATTR_KEY");
    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY");
    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_ALL_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "NET_TRACE_INPUT_ALL_ATTR_KEY");
    public static final AttrKey<ProcessTracer> NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY");
    public static final AttrKey<ProcessTracer> MESSAGE_DISPATCH_TO_EXECUTE_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "MESSAGE_DISPATCH_TO_EXECUTE_ATTR_KEY");
    public static final AttrKey<ProcessTracer> NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY =
            AttrKeys.key(ProcessWatcher.class, "NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY");

    private static class WatcherAttribute {

        private final AttrKey<ProcessTracer> key;
        private final ProcessWatcher watcher;

        private WatcherAttribute(ProcessWatcher watcher, AttrKey<ProcessTracer> key) {
            this.key = key;
            this.watcher = watcher;
        }

    }

    public static final WatcherAttribute NET_TRACE_ALL_ATTR = new WatcherAttribute(
            NET_TRACE_ALL_WATCHER, NET_TRACE_ALL_ATTR_KEY);
    public static final WatcherAttribute NET_TRACE_INPUT_ALL_ATTR = new WatcherAttribute(
            NET_TRACE_INPUT_ALL, NET_TRACE_INPUT_ALL_ATTR_KEY);
    public static final WatcherAttribute NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR = new WatcherAttribute(
            NET_TRACE_INPUT_READ_TO_TUNNEL_WATCHER, NET_TRACE_INPUT_READ_TO_TUNNEL_ATTR_KEY);
    public static final WatcherAttribute NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR = new WatcherAttribute(
            NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_WATCHER, NET_TRACE_INPUT_TUNNEL_TO_EXECUTE_ATTR_KEY);
    public static final WatcherAttribute NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR = new WatcherAttribute(
            NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER, NET_TRACE_OUTPUT_WRITE_TO_ENCODE_ATTR_KEY);

    public static void trace(WatcherAttribute attribute, Message message) {
        ProcessTracer tracer = attribute.watcher.trace();
        message.attributes().setAttribute(attribute.key, tracer);
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

    private static final Logger RECEIVE_RQS_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_RQS);
    private static final Logger RECEIVE_RSP_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_RSP);
    private static final Logger RECEIVE_PUSH_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_PUSH);

    private static final Logger SEND_RQS_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_RQS);
    private static final Logger SEND_RSP_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_RSP);
    private static final Logger SEND_PUSH_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_PUSH);

    public static final String CODER = "netCoder";
    private static final String RECEIVE_RQS = "receiveRequest";
    private static final String RECEIVE_RSP = "receiveResponse";
    private static final String RECEIVE_PUSH = "receivePush";
    private static final String SEND_RQS = "sendRequest";
    private static final String SEND_RSP = "sendResponse";
    private static final String SEND_PUSH = "sendPush";
    public static final String CHECKER = "signGenerator";
    public static final String DISPATCHER = "dispatcher";
    public static final String NET = "coreLogger";
    public static final String CLIENT = "coreClient";
    public static final String SESSION = "session";
    public static final String EXECUTOR = "executor";

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

    private static Logger getSendLogger(Message message) {
        return getLoggerByMessage(message, SEND_PUSH_LOGGER, SEND_RQS_LOGGER, SEND_RSP_LOGGER);
    }

    private static Logger getReceiveLogger(Message message) {
        return getLoggerByMessage(message, RECEIVE_PUSH_LOGGER, RECEIVE_RQS_LOGGER, RECEIVE_RSP_LOGGER);
    }

    public static void debugReceive(Message message, String msg, Object... args) {
        Logger logger = getReceiveLogger(message);
        if (logger != null && logger.isDebugEnabled()) {
            Object[] msgArgs = toMessageArgs(message, args);
            logger.debug(
                    "\n#-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-\n#<< 接受 {} 消息 \n#<< - Protocol : {} | 消息ID : {} | 响应请求ID {} | 消息体 : {} \n#<<" +
                            msg + "\n#-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-", msgArgs);
        }
    }

    public static void debugSend(Message message, String msg, Object... args) {
        Logger logger = getSendLogger(message);
        if (logger != null && logger.isDebugEnabled()) {
            Object[] msgArgs = toMessageArgs(message, args);
            logger.debug(
                    "\n#-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-\n#>> 发送 {} 消息 \n#>> - Protocol : {} | 消息ID : {} | 响应请求ID {} | 消息体 : {} \n>>" +
                            msg + "\n#-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-", msgArgs);
        }
    }

    private static Object[] toMessageArgs(Message message, Object[] args) {
        Object[] msgArgs = new Object[5];
        MessageHead head = message.getHead();
        msgArgs[0] = message.getMode();
        msgArgs[1] = head.getId();
        msgArgs[2] = head.getId();
        msgArgs[3] = head.getToMessage();
        msgArgs[4] = message.getBody(Object.class);
        if (args.length > 0) {
            msgArgs = ArrayUtils.addAll(msgArgs, args);
        }
        return msgArgs;
    }

    public static void logSend(Tunnel<?> tunnel, Message message) {
        Logger logger = getSendLogger(message);
        if (logger != null && logger.isDebugEnabled()) {
            MessageHead head = message.getHead();
            logger.debug(
                    "\n#---------------------------------------------\n#>> 发送 {} 消息 [{}] \n#>> - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#>> 创建时间 : {} \n#>> 消息码 : {} \n#>> 消息体 : {}\n#---------------------------------------------",
                    message.getMode(),
                    tunnel,
                    head.getId(), head.getId(), head.getToMessage(),
                    new Date(head.getTime()), head.getCode(), message.getBody(Object.class));
        }

    }

    public static void logReceive(Tunnel<?> tunnel, Message message) {
        Logger logger = getReceiveLogger(message);
        if (logger != null && logger.isDebugEnabled()) {
            MessageHead head = message.getHead();
            logger.debug(
                    "\n#---------------------------------------------\n#<< 接收 {} 消息 [{}] \n#<< - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#<< 创建时间 : {} \n#<< 消息码 : {} \n#<< 消息体 : {}\n#---------------------------------------------",
                    message.getMode(),
                    tunnel,
                    head.getId(), head.getId(), head.getToMessage(),
                    new Date(head.getTime()), head.getCode(), message.getBody(Object.class));
        }
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