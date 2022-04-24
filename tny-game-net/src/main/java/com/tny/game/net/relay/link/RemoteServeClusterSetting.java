package com.tny.game.net.relay.link;

import com.tny.game.net.serve.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/3 8:47 下午
 */
public interface RemoteServeClusterSetting extends Serve {

    String getUsername();

    long getLinkMaxIdleTime();

    long getLinkHeartbeatInterval();

    int getLinkConnectionSize();

    boolean isDiscovery();

}
