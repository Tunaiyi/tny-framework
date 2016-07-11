package com.tny.game.net.client.rmi;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.checker.RequestVerifyChecker;
import com.tny.game.net.client.exception.ClientException;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public class RMIClient {

    protected RMIService rmiService;

    protected RequestVerifyChecker checker;

    protected AtomicInteger requestIDCreator = new AtomicInteger(1);

    protected MessageBuilderFactory messageBuilderFactory;

    private volatile transient Attributes attributes;

    public RMIClient(RMIService rmiService, RequestVerifyChecker checker) {
        this.checker = checker;
        this.rmiService = rmiService;
    }

    public Attributes attributes() {
        if (this.attributes != null)
            return this.attributes;
        synchronized (this) {
            if (this.attributes != null)
                return this.attributes;
            return this.attributes = ContextAttributes.create();
        }
    }

    public Response sendRequest(Protocol protocol, Object... requestParams) throws ClientException {
        return this.sendRequest(protocol.getProtocol(), requestParams);
    }

    /**
     * 发送请求
     *
     * @param protocol 方法名
     * @param params   参数列表
     * @return 响应结果
     * @throws ClientException
     */
    public Response sendRequest(int protocol, Object... params) throws ClientException {
        Request request = messageBuilderFactory
                .newRequestBuilder()
                // .setAttributes(this.attributes())
                .setID(this.requestIDCreator.getAndIncrement())
                .setRequestChecker(this.checker)
                .setProtocol(protocol)
                .addParameter(params)
                .build();
        Response response = null;
        try {
            response = this.rmiService.send(request);
        } catch (RemoteException e) {
            throw new ClientException(CoreResponseCode.REMOTE_EXCEPTION, e);
        }
        return response;
    }

}
