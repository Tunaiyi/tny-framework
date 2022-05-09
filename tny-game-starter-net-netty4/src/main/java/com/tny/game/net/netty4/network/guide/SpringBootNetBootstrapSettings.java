package com.tny.game.net.netty4.network.guide;

import java.util.Map;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/5/6 19:09
 **/
public interface SpringBootNetBootstrapSettings {

    SpringNettyNetServerBootstrapSetting getServer();

    SpringNettyNetClientBootstrapSetting getClient();

    Map<String, ? extends SpringNettyNetServerBootstrapSetting> getServers();

    Map<String, ? extends SpringNettyNetClientBootstrapSetting> getClients();

}
