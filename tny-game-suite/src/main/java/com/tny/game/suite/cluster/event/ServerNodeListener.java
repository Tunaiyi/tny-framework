package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.ServerNode;
import com.tny.game.suite.cluster.game.ServerLaunch;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;

public interface ServerNodeListener {

    default void onOutlineChange(ServerNode node, ServerOutline oldOnline) {
    }

    default void onLaunchChange(ServerNode node, ServerLaunch oldLaunch) {
    }

    default void onSettingChange(ServerNode node, ServerSetting oldSetting) {
    }

}
