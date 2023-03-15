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
package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

public class NetLogger {

    public static final ProcessWatcher MESSAGE_DECODE_WATCHER = ProcessWatcher.of(RpcInvokeCommand.class + ".track_input-decode",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_BOX_PROCESS_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".track_input-box_process",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".track_input-execute_command", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_ENCODE_WATCHER = ProcessWatcher.of(RpcInvokeCommand.class + ".track_output-encode",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".track_output-write_to_encode", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_GET_CONTROLLER_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-get_controller", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_AUTHENTICATE_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-check_authenticate", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_CHECK_INVOKABLE_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-check_invokable", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_BEFORE_PLUGINS_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-before_plugins", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_INVOKING_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-invoking",
            TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    public static final ProcessWatcher MESSAGE_EXE_INVOKE_AFTER_PLUGINS_WATCHER = ProcessWatcher.of(
            RpcInvokeCommand.class + ".command_exe_invoke-after_plugins", TrackPrintOption.CLOSE);//.schedule(15, TimeUnit.SECONDS);

    private static class WatcherAttribute {

        private final AttrKey<ProcessTracer> key;

        private final ProcessWatcher watcher;

        private WatcherAttribute(ProcessWatcher watcher, AttrKey<ProcessTracer> key) {
            this.key = key;
            this.watcher = watcher;
        }

    }

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

    private static final String MESSAGE_RECEIVE_LOGGER_NAME = "com.tny.game.net.rpc.{}.receive.{}.{}";

    private static final String MESSAGE_SEND_LOGGER_NAME = "com.tny.game.net.rpc.{}.send.{}.{}";

    private static final String RELAY_RECEIVE_LOGGER_NAME = "com.tny.game.net.relay.{}.receive.{}.{}";

    private static final String RELAY_SEND_LOGGER_NAME = "com.tny.game.net.relay.{}.send.{}.{}";

    private static Logger getMessageSendLogger(String service, MessageSubject message) {
        return NetLoggerGroup.ofRpc(service).forSendMessage(message.getMode());
    }

    private static Logger getMessageReceiveLogger(String service, MessageSubject message) {
        return NetLoggerGroup.ofRpc(service).forReceiveMessage(message.getMode());
    }

    private static Logger getRelayPacketSendLogger(String service, RelayPacket<?> packet) {
        return NetLoggerGroup.ofRelay(service).forSendMessage(packet.getType());
    }

    private static Logger getRelayPacketReceiveLogger(String service, RelayPacket<?> packet) {
        return NetLoggerGroup.ofRelay(service).forReceiveMessage(packet.getType());
    }

    public static void logSend(NetMessager messager, MessageSubject message) {
        Logger logger = getMessageSendLogger(messager.getGroup(), message);
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("# {} [发送] =>> Message : {}", messager, message);
        }
    }

    public static void logReceive(NetMessager messager, MessageSubject message) {
        Logger logger = getMessageReceiveLogger(messager.getGroup(), message);
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("# {} [接收] <<= Message : {}", messager, message);
        }
    }

    public static void logReceive(NetRelayLink link, RelayPacket<?> packet) {
        Logger logger = getRelayPacketReceiveLogger(link.getService(), packet);
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("#{} [接收] << LinkPacket : {}", link, packet);
        }
    }

    public static void logSend(NetRelayLink link, RelayPacket<?> packet) {
        Logger logger = getRelayPacketSendLogger(link.getService(), packet);
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("#{} [发送] >> LinkPacket : {}", link, packet);
        }
    }

    public static void logReceive(RelayTransporter transporter, LinkOpenPacket packet) {
        var arguments = packet.getArguments();
        Logger logger = getRelayPacketSendLogger(arguments.getService(), packet);
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug("RelayLink({})[{}]{} # [接收] << LinkPacket : {}", transporter.getAccessMode(), "NEW-LINK", transporter, packet);
        }
    }

    private static class NetLoggerGroup<E extends Enum<E>> {

        private final Logger[] receiveLoggers;

        private final Logger[] sendLoggers;

        private static final ConcurrentMap<String, NetLoggerGroup<?>> NET_LOGGER_GROUP_MAP = new ConcurrentHashMap<>();

        private static NetLoggerGroup<MessageMode> ofRpc(String userType) {
            return as(NET_LOGGER_GROUP_MAP.computeIfAbsent("Rpc:" + userType,
                    (k) -> new NetLoggerGroup<>(userType, MessageMode.class, MessageMode::getWay, MESSAGE_RECEIVE_LOGGER_NAME,
                            MESSAGE_SEND_LOGGER_NAME)));
        }

        private static NetLoggerGroup<RelayPacketType> ofRelay(String userType) {
            return as(NET_LOGGER_GROUP_MAP.computeIfAbsent("Relay:" + userType,
                    (k) -> new NetLoggerGroup<>(userType, RelayPacketType.class, RelayPacketType::getWay, RELAY_RECEIVE_LOGGER_NAME,
                            RELAY_SEND_LOGGER_NAME)));
        }

        private NetLoggerGroup(String service, Class<E> enumClass, Function<E, NetworkWay> wayFunction, String subReceiveLoggerKey,
                String subSendLoggerKey) {
            var enumSet = EnumSet.allOf(enumClass);
            this.receiveLoggers = new Logger[enumSet.size()];
            this.sendLoggers = new Logger[enumSet.size()];
            for (E item : enumSet) {
                var way = wayFunction.apply(item);
                receiveLoggers[item.ordinal()] = LoggerFactory.getLogger(
                        format(subReceiveLoggerKey, way.getValue(), service.toLowerCase(), item.name().toLowerCase()));
                sendLoggers[item.ordinal()] = LoggerFactory.getLogger(
                        format(subSendLoggerKey, way.getValue(), service.toLowerCase(), item.name().toLowerCase()));
            }
        }

        public Logger forReceiveMessage(E enumValue) {
            return receiveLoggers[enumValue.ordinal()];
        }

        public Logger forSendMessage(E enumValue) {
            return sendLoggers[enumValue.ordinal()];
        }

    }

}