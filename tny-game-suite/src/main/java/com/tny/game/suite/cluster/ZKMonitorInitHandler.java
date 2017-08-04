package com.tny.game.suite.cluster;

import com.tny.game.zookeeper.ZKMonitor;

/**
 * Created by Kun Yang on 2017/8/3.
 */
public interface ZKMonitorInitHandler {

    void onInit(ZKMonitor monitor);

}
