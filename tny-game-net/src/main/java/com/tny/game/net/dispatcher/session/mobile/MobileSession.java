package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.ChannelServerSession;
import com.tny.game.net.dispatcher.Response;
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
    protected int createResponseNumber() {
        MobileAttach attach = this.getMobileAttach();
        if (attach == null)
            return 0;
        return attach.createResponseNumber();
    }

    @Override
    protected void prepareWriteResponse(Response response) {
        MobileAttach attach = this.getMobileAttach();
        if (attach != null)
            attach.push(response);
    }

}