package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.game.GameServerCluster;
import com.tny.game.suite.cluster.game.ServerSetting;

public interface GameServerClusterListener {

    void onChange(GameServerCluster source, ServerSetting setting);

}
