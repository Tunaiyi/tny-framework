package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.game.*;

public interface GameServerClusterListener {

    void onChange(GameServerCluster source, ServerSetting setting);

}
