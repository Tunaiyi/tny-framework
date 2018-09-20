package com.tny.game.net.client.rmi;

import com.tny.game.common.context.Attributes;
import com.tny.game.common.context.ContextAttributes;
import com.tny.game.net.transport.message.MessageBuilderFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class RMIClient {

    protected RMIService rmiService;

    protected AtomicInteger requestIDCreator = new AtomicInteger(1);

    protected MessageBuilderFactory messageBuilderFactory;

    private volatile transient Attributes attributes;

    public RMIClient(RMIService rmiService) {
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
    //
    // public Response sendRequest(Protocol protocol, Object... requestParams) throws ClientException {
    //     return this.sendRequest(protocol.getProtocol(), requestParams);
    // }
    //
    // /**
    //  * 发送请求
    //  *
    //  * @param protocol 方法名
    //  * @param params   参数列表
    //  * @return 响应结果
    //  * @throws ClientException
    //  */
    // public Response sendRequest(int protocol, Object... params) throws ClientException {
    //     Request request = messageBuilderFactory
    //             .newRequestBuilder(null)
    //             // .setAttributes(this.attributes())
    //             .setId(this.requestIDCreator.getAndIncrement())
    //             .setRequestVerifier(this.verifier)
    //             .setProtocol(protocol)
    //             .addParameter(params)
    //             .build();
    //     Response response = null;
    //     try {
    //         response = this.rmiService.send(request);
    //     } catch (RemoteException e) {
    //         throw new ClientException(CoreResponseCode.REMOTE_EXCEPTION, e);
    //     }
    //     return response;
    // }

}
