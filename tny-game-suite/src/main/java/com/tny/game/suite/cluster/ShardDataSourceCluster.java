package com.tny.game.suite.cluster;

import com.google.common.collect.ImmutableList;
import com.tny.game.suite.cache.*;
import com.tny.game.suite.cluster.game.*;

import java.util.*;

public abstract class ShardDataSourceCluster extends ServiceCluster {

    private ShardDataSourceFactory dataSourceFactory;

    public ShardDataSourceCluster(String serverType, ShardDataSourceFactory dataSourceFactory, String... monitorWebTypes) {
        this(serverType, dataSourceFactory, false, Arrays.asList(monitorWebTypes));
    }

    public ShardDataSourceCluster(String serverType, ShardDataSourceFactory dataSourceFactory, Collection<String> monitorWebTypes) {
        this(serverType, dataSourceFactory, false, monitorWebTypes);
    }

    public ShardDataSourceCluster(String serverType, ShardDataSourceFactory dataSourceFactory, boolean monitorAllServices) {
        this(serverType, dataSourceFactory, monitorAllServices, ImmutableList.of());
    }

    protected ShardDataSourceCluster(String serverType, ShardDataSourceFactory dataSourceFactory, boolean monitorAllServices,
            Collection<String> monitorWebTypes) {
        super(serverType, true, monitorAllServices, monitorWebTypes);
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    protected void postUpdateSetting(ServerNode node, ServerSetting serverSetting, boolean create) {
        try {
            ServerOutline outline = node.getOutline();
            if (outline.isHasDB()) {
                ServerSetting setting = node.getSetting();
                if (setting != null && setting.getServerState() != ServerState.INVALID) {
                    this.dataSourceFactory.register(outline.getServerId(),
                            outline.getDbHost(), outline.getDbPort(), outline.getDb());
                }
            }
        } catch (Exception e) {
            LOGGER.error("{} 服务器注册 dataSource 异常", node, e);
        }
    }

    @Override
    protected void postUpdateOutline(ServerNode node, ServerOutline outline, boolean create) {
        try {
            if (outline.isHasDB()) {
                ServerSetting setting = node.getSetting();
                if (setting != null && setting.getServerState() != ServerState.INVALID) {
                    this.dataSourceFactory.register(outline.getServerId(),
                            outline.getDbHost(), outline.getDbPort(), outline.getDb());
                }
            }
        } catch (Exception e) {
            LOGGER.error("{} 服务器注册 dataSource 异常", node, e);
        }
    }

}
