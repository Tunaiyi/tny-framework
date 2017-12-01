package com.tny.game.net.base;

import com.tny.game.net.message.Message;
import com.tny.game.net.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class NetLogger {

    private static final Logger RECEIVE_RQS_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_RQS);
    private static final Logger RECEIVE_RSP_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_RSP);
    private static final Logger RECEIVE_PUSH_LOGGER = LoggerFactory.getLogger(NetLogger.RECEIVE_PUSH);

    private static final Logger SEND_RQS_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_RQS);
    private static final Logger SEND_RSP_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_RSP);
    private static final Logger SEND_PUSH_LOGGER = LoggerFactory.getLogger(NetLogger.SEND_PUSH);

    public static final String CODER = "netCoder";
    public static final String CONTEXT = "appContext";
    private static final String RECEIVE_MESSAGE = "receiveMessage";
    private static final String RECEIVE_RQS = "receiveRequest";
    private static final String RECEIVE_RSP = "receiveResponse";
    private static final String RECEIVE_PUSH = "receivePush";
    private static final String SEND_MESSAGE = "sendMessage";
    private static final String SEND_RQS = "sendRequest";
    private static final String SEND_RSP = "sendResponse";
    private static final String SEND_PUSH = "sendPush";
    public static final String CHECKER = "signGenerator";
    public static final String DISPATCHER = "dispatcher";
    public static final String NET = "coreLogger";
    public static final String NIO_CLIENT = "nioClient";
    public static final String CLIENT = "coreClient";
    public static final String SESSION = "session";
    public static final String TELNET = "telnet";
    public static final String EXECUTOR = "executor";


    private static Logger getSendLogger(Message message) {
        if (message == null)
            return null;
        switch (message.getMode()) {
            case PUSH:
                return SEND_PUSH_LOGGER;
            case REQUEST:
                return SEND_RQS_LOGGER;
            case RESPONSE:
                return SEND_RSP_LOGGER;
        }
        return null;
    }

    private static Logger getReceiveLogger(Message message) {
        if (message == null)
            return null;
        switch (message.getMode()) {
            case PUSH:
                return RECEIVE_PUSH_LOGGER;
            case REQUEST:
                return RECEIVE_RQS_LOGGER;
            case RESPONSE:
                return RECEIVE_RSP_LOGGER;
        }
        return null;
    }

    public static void logSend(Session session, Message message) {
        Logger logger = getSendLogger(message);
        if (logger != null && logger.isDebugEnabled())
            logger.debug("\n#---------------------------------------------\n#>> 发送 {} 消息 [{}] \n#>> - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#>> 校验码 : {} \n#>> 创建时间 : {} \n#>> 消息码 : {} \n#>> 消息体 : {}#---------------------------------------------",
                    message.getMode(),
                    session,
                    message.getProtocol(), message.getID(), message.getToMessage(),
                    message.getSign(), new Date(message.getTime()), message.getCode(), message.getBody(Object.class));
    }

    public static void logReceive(Session session, Message message) {
        Logger logger = getReceiveLogger(message);
        if (logger != null && logger.isDebugEnabled())
            logger.debug("\n#---------------------------------------------\n#<< 接收 {} 消息 [{}] \n#<< - Protocol : {} | 消息ID : {} | 响应请求ID {} \n#<< 校验码 : {} \n#<< 创建时间 : {} \n#<< 消息码 : {} \n#<< 消息体 : {}#---------------------------------------------",
                    message.getMode(),
                    session,
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