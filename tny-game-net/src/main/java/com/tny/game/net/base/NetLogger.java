package com.tny.game.net.base;

import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class NetLogger {

    private static final Logger RECEIVE_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_MESSAGE);

    private static final Logger SEND_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_MESSAGE);

    public static final String CODER = "netCoder";
    public static final String CONTEXT = "context";
    private static final String RECEIVE_MESSAGE = "receiveMessage";
    private static final String SEND_MESSAGE = "sendMessage";
    public static final String CHECKER = "signGenerator";
    public static final String DISPATCHER = "dispatcher";
    public static final String NET = "coreLogger";
    public static final String NIO_CLIENT = "nioClient";
    public static final String CLIENT = "coreClient";
    public static final String SESSION = "session";
    public static final String TELNET = "telnet";
    public static final String EXECUTOR = "executor";


    public static void logSend(Session session, Message message) {
        if (SEND_LOGGER.isDebugEnabled())
            SEND_LOGGER.debug("\n#---------------------------------------------\n#>> 发送消息 [{}|{}|{}] \n#>> - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#>> 校验码 : {} \n#<< 创建时间 : {} \n#>> 消息码 : {} \n#>> 消息体 : {}#---------------------------------------------",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    message.getProtocol(), message.getID(), message.getToMessage(),
                    message.getSign(), new Date(message.getTime()), message.getCode(), message.getBody(Object.class));
    }

    public static void logReceive(Session session, Message message) {
        if (RECEIVE_LOGGER.isDebugEnabled())
            RECEIVE_LOGGER.debug("\n#---------------------------------------------\n#<< 接收消息 [{}|{}|{}] \n#<< - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#<< 校验码 : {} \n#<< 创建时间 : {} \n#<< 消息码 : {} \n#<< 消息体 : {}#---------------------------------------------",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    message.getProtocol(), message.getID(), message.getToMessage(),
                    message.getSign(), new Date(message.getTime()), message.getCode(), message.getBody(Object.class));
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