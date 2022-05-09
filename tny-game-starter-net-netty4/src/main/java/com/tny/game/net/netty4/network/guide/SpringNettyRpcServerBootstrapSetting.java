package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.command.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
//@Order(HIGHEST_PRECEDENCE)
//@Configuration
public class SpringNettyRpcServerBootstrapSetting extends SpringNettyNetServerBootstrapSetting {

    public SpringNettyRpcServerBootstrapSetting() {
        super();
        this.setCertificateFactory(defaultName(MessagerCertificateFactory.class));
    }

}
