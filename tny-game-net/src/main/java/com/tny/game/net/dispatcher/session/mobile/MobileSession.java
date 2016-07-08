package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.common.result.ResultCode;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.base.Protocol;
import com.tny.game.net.dispatcher.ChannelServerSession;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;
import io.netty.channel.Channel;

public class MobileSession extends ChannelServerSession {

    public MobileSession(Channel channel) {
        super(channel);
    }

    public MobileSession(Channel channel, LoginCertificate loginInfo) {
        super(channel, loginInfo);
    }

    protected MobileAttach getMobileAttach() {
        return this.attributes().getAttribute(MobileSessionHolder.MOBILE_ATTACH);
    }

    @Override
    protected void prepareWriteResponse(Protocol protocol, ResultCode code, Object body) {
        MobileAttach attach = this.getMobileAttach();
        if (attach != null && protocol instanceof Request) {
            Request request = (Request) protocol;
            if (request.getID() > Session.DEFAULT_RESPONSE_ID) {
                if (attach.exist(request.getID()))
                    return;
                attach.push(new ResponseItem(request.getID(), code, body));
            }
        }
    }

}