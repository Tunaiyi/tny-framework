package com.tny.game.suite.net.rmi;

import com.tny.game.common.utils.json.JSONUtils;
import com.tny.game.net.base.CoreResponseCode;
import com.tny.game.net.checker.MessageCheckGenerator;
import com.tny.game.net.client.exception.ClientException;
import com.tny.game.net.client.rmi.RMIClient;
import com.tny.game.net.client.rmi.RMIService;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;
import com.tny.game.net.dispatcher.message.simple.SimpleMessageBuilderFactory;
import com.tny.game.suite.core.ScopeType;
import com.tny.game.suite.core.SessionKeys;
import com.tny.game.suite.login.ServeTicket;
import com.tny.game.suite.login.ServeTicketMaker;
import com.tny.game.suite.login.TicketMaker;
import com.tny.game.suite.utils.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class GameRMIClient extends RMIClient {

    public static final Logger LOGGER = LoggerFactory.getLogger(GameRMIClient.class);

    private ScopeType scopeType;

    private int serverID;

    private int clientID;

    private String rmiUrl;

    private TicketMaker<ServeTicket> maker;

    private MessageBuilderFactory messageBuilderFactory;

    public GameRMIClient(ScopeType scopeType, int serverID, int userID, String rmiUrl, RMIService rmiService, MessageCheckGenerator checker) {
        super(rmiService, checker);
        String key = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(scopeType.getServerType().getName()), "");
        this.attributes().setAttribute(SessionKeys.SYSTEM_USER_ID, this.clientID);
        this.attributes().setAttribute(SessionKeys.SYSTEM_USER_USER_GROUP, scopeType.getServerType().getName());
        this.attributes().setAttribute(SessionKeys.SYSTEM_USER_PASSWORD, key);
        this.serverID = serverID;
        this.clientID = userID;
        this.scopeType = scopeType;
        this.rmiUrl = rmiUrl;
        this.maker = new ServeTicketMaker();
        this.messageBuilderFactory = new SimpleMessageBuilderFactory();
    }

    public String getRmiUrl() {
        return this.rmiUrl;
    }

    public int getServerID() {
        return this.serverID;
    }

    @Override
    public Response sendRequest(int protocol, Object... params) throws ClientException {
        ServeTicket ticket = new ServeTicket(this.scopeType, this.clientID, this.maker);
        String ticketWord = JSONUtils.toJson(ticket);
        Request request = messageBuilderFactory
                .newRequestBuilder(null)
                .setID(this.requestIDCreator.incrementAndGet())
                .setRequestVerifier(this.verifier)
                .setProtocol(protocol)
                .addParameter(params)
                .addParameter(ticketWord)
                .build();
        Response response = null;
        try {
            response = this.rmiService.send(request);
        } catch (RemoteException e) {
            LOGGER.error("远程调用异常协议[{}] {}", protocol, params, e);
            throw new ClientException(CoreResponseCode.REMOTE_EXCEPTION, e);
        }
        return response;
    }

}
