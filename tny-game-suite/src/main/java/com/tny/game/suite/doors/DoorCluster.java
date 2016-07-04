package com.tny.game.suite.doors;

import com.tny.game.suite.cluster.WebServerCluster;

import java.util.Collection;

/**
 * Created by Kun Yang on 16/6/28.
 */
public abstract class DoorCluster extends WebServerCluster {

    public DoorCluster(String serverType, int webServerID, boolean watchSetting, String... monitorWebTypes) {
        super(serverType, webServerID, watchSetting, monitorWebTypes);
    }

    public DoorCluster(String serverType, int webServerID, boolean watchSetting, Collection<String> monitorWebTypes) {
        super(serverType, webServerID, watchSetting, monitorWebTypes);
    }

}
