package com.tny.game.suite.cluster.event;

import com.tny.game.suite.cluster.*;
import com.tny.game.suite.cluster.game.*;

public interface ServerNodeListener {

    default void onOutlineChange(ServerNode node, ServerOutline oldOnline) {
    }

    default void onLaunchChange(ServerNode node, ServerLaunch oldLaunch) {
    }

    default void onSettingChange(ServerNode node, ServerSetting oldSetting) {
    }

}
