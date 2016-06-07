package com.tny.game.suite.cluster;

import com.tny.game.suite.cache.ShardDataSourceFactory;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;
import com.tny.game.suite.cluster.game.ServerState;
import com.tny.game.suite.core.ServerType;

public abstract class ShardDataSourceCluster extends WebServerCluster {

	private ShardDataSourceFactory dataSourceFactory;

	public ShardDataSourceCluster(ServerType serverType, int webServerID, ShardDataSourceFactory dataSourceFactory) {
		super(serverType,  webServerID, true);
		this.dataSourceFactory = dataSourceFactory;
		this.webServerID = webServerID;
	}


	@Override
	protected void postUpdateSetting(ServerNode node, ServerSetting serverSetting, boolean create) {
		try {
			ServerOutline outline = node.getOutline();
			if (outline.isHasDB()) {
				ServerSetting setting = node.getSetting();
				if (setting != null && setting.getServerState() != ServerState.INVALID)
					this.dataSourceFactory.register(outline.getServerID(),
							outline.getDbHost(), outline.getDbPort(), outline.getDb());
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
				if (setting != null && setting.getServerState() != ServerState.INVALID)
					this.dataSourceFactory.register(outline.getServerID(),
							outline.getDbHost(), outline.getDbPort(), outline.getDb());
			}
		} catch (Exception e) {
			LOGGER.error("{} 服务器注册 dataSource 异常", node, e);
		}
	}

}
