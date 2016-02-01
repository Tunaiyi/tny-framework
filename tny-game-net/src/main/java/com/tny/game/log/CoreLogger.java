package com.tny.game.log;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;
import com.tny.game.net.dispatcher.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class CoreLogger {

    private static final Logger LOG_RESPONE = LoggerFactory.getLogger(CoreLogger.RESPONSE);

    private static final Logger LOG_REQUEST = LoggerFactory.getLogger(CoreLogger.REQUEST);

    public static final String CODER = "netCoder";
    public static final String CONTEXT = "context";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    public static final String CHECKER = "checker";
    public static final String DISPATCHER = "dispatcher";
    public static final String NET = "coreLogger";
    public static final String NIO_CLIENT = "nioClient";
    public static final String CLIENT = "coreClient";
    public static final String SESSION = "session";
    public static final String TELNET = "telnet";
    public static final String EXECUTOR = "executor";

    public static void log(Session session, Response response) {
        if (LOG_RESPONE.isDebugEnabled())
            LOG_RESPONE.debug("\n##响应到 [{}|{}|{}] \n##响应 - 请求 [{}] {} ##响应码 : {} \n##响应消息体 : {}",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    response.getID(), response.getProtocol(),
                    response.getResult(), response.getBody(Object.class));
    }

    public static void log(Session session, Request request) {
        if (LOG_REQUEST.isDebugEnabled())
            LOG_REQUEST.debug("\n$$接受到 [{}|{}|{}] \n$$接受 - 请求 [{}] {} $$请求参数 : {{}} \n$$请求时间 : {} $$请求校验码 : {} ",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    request.getID(), request.getProtocol(),
                    request.getParamList().toString(), new Date(request.getTime()), request.getCheckKey());
    }

    public static void log(Session session, Protocol protocol, ResultCode code, Object body) {
        if (LOG_RESPONE.isDebugEnabled())
            LOG_RESPONE.debug("\n##响应到 [{}|{}|{}] \n##响应 - 请求 [{}] {} ##响应码 : {} \n##响应消息体 : {}",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    0, protocol.getProtocol(),
                    code.getCode(), body);
    }

    public static void log(Session session, Protocol protocol, int code, Object body) {
        if (LOG_REQUEST.isDebugEnabled())
            LOG_RESPONE.debug("\n##响应到 [{}|{}|{}] \n##响应 - 请求 [{}] {} ##响应码 : {} \n##响应消息体 : {}",
                    session.getGroup(), session.getHostName(), session.getUID(),
                    0, protocol.getProtocol(),
                    code, body);
    }

}