package com.tny.game.net.tunnel;

import com.tny.game.common.context.Attributes;
import com.tny.game.net.session.Session;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface Tunnel<UID> extends Terminal<UID> {

    long getID();

    boolean isConnected();

    Attributes attributes();

    Session<UID> getSession();

    String getHostName();

    /**
     * @return 是否登陆认证
     */
    boolean isLogin();

}
