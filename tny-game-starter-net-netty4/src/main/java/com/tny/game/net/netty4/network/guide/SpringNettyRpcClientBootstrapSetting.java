package com.tny.game.net.netty4.network.guide;

import com.tny.game.net.command.*;

import static com.tny.game.net.base.configuration.NetUnitNames.*;

/**
 * 只是为了生成配置说明
 *
 * @author : kgtny
 * @date : 2021/7/13 8:26 下午
 */
public class SpringNettyRpcClientBootstrapSetting extends SpringNettyNetClientBootstrapSetting {

    public SpringNettyRpcClientBootstrapSetting() {
        super();
        this.setCertificateFactory(defaultName(MessagerCertificateFactory.class));
    }

}
