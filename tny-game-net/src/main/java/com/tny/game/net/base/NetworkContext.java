package com.tny.game.net.base;

import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/26 2:22 下午
 */
public interface NetworkContext extends EndpointContext {

    NetBootstrapSetting getSetting();

    MessageFactory getMessageFactory();

    <UID> CertificateFactory<UID> getCertificateFactory();

}
