package com.tny.game.suite.login;

import com.tny.game.common.result.ResultCode;
import com.tny.game.common.result.ResultCodes;
import com.tny.game.net.base.AppType;
import com.tny.game.net.exception.CommandException;
import com.tny.game.net.exception.ValidatorFailException;
import com.tny.game.net.transport.*;
import com.tny.game.net.message.Message;
import com.tny.game.suite.core.AttributesKeys;
import com.tny.game.suite.utils.SuiteResultCode;

import javax.annotation.Resource;
import java.time.Instant;

public class ServerAuthenticateValidator extends GameAuthenticateValidator<Integer> {

    @Resource
    private ServerTicketMaker maker;

    @Override
    public Certificate<Integer> validate(Tunnel<Integer> tunnel, Message<Integer> message) throws CommandException {
        ResultCode code = ResultCodes.of(message.getCode());
        if (code.isFailure())
            throw new ValidatorFailException(SuiteResultCode.AUTH_TICKET_TIMEOUT, new CommandException(code));
        ServerTicket ticket = message.getBody(ServerTicket.class);
        if (System.currentTimeMillis() - ticket.getTime() > 60000)
            throw new ValidatorFailException(SuiteResultCode.AUTH_TICKET_TIMEOUT);
        AppType serverType = ticket.asServerType();
        if (this.maker.make(ticket).equals(ticket.getSecret())) {
            Certificate<Integer> info = Certificates.createAutherized(ticket.getTime(), ticket.getServerID(), serverType.getName(), Instant.ofEpochMilli(ticket.getTime()));
            ServerTicket localTicket = tunnel.attributes().getAttribute(AttributesKeys.LOCAL_SERVER_TICKET);
            ServerTicket signTicket = localTicket != null ? localTicket : ticket;
            tunnel.attributes().setAttribute(AttributesKeys.SERVER_TICKET, ticket);
            tunnel.attributes().setAttribute(AttributesKeys.SYSTEM_USER_ID, signTicket.getServerID());
            tunnel.attributes().setAttribute(AttributesKeys.SYSTEM_USER_USER_GROUP, signTicket.getServerType());
            return info;
        }
        return Certificates.createUnautherized(-1);
    }
}