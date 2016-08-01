package com.tny.game.suite.doors;

import com.tny.game.suite.cluster.ClusterUtils;
import com.tny.game.suite.cluster.ServerNode;
import com.tny.game.suite.cluster.Servers;
import com.tny.game.suite.cluster.WebServerCluster;
import com.tny.game.suite.cluster.game.ServerOutline;
import com.tny.game.suite.cluster.game.ServerSetting;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分区入口集群器
 * Created by Kun Yang on 16/6/28.
 */
@Component
@Profile({"suite.doors"})
public class DoorsCluster extends WebServerCluster {

    @Autowired
    private ServletContext servletContext;

    private VersionHolder versionHolder = new VersionHolder();

    private AtomicLong zoneChangeVersion = new AtomicLong(System.currentTimeMillis());

    public DoorsCluster() {
        super(Servers.DOORS, 1, false, Servers.API, Servers.LOG, Servers.ACCESS);
    }

    @Override
    protected void postUpdateOutline(ServerNode node, ServerOutline outline, boolean create) {
        String settingPath = ClusterUtils.getServerSettingPath(outline.getServerID());
        ServerSetting setting = this.getSetting(settingPath);
        if (setting == null) {
            setting = new ServerSetting(outline);
            this.remoteMonitor.putNodeData(CreateMode.PERSISTENT, setting, settingPath, ClusterUtils.PROTO_FORMATTER);
        }
        if (create)
            updateSetting(node, setting);
    }

    private ServerSetting getSetting(String settingPath) {
        ServerSetting setting;
        this.remoteMonitor.createFullNode(settingPath, new byte[0], CreateMode.PERSISTENT, false, ClusterUtils.NO_FORMATTER);
        setting = this.remoteMonitor.getNodeData(settingPath, CreateMode.PERSISTENT);
        return setting;
    }

    public boolean updateServerNode(int serverID, ServerSetting setting) {
        ServerNode node = this.getServerNode(serverID);
        if (node == null)
            return false;
        updateSetting(node, setting);
        String settingPath = ClusterUtils.getServerSettingPath(node.getServerID());
        this.remoteMonitor.syncNode(settingPath, setting);
        return true;
    }

    public boolean isExistVersion(String version) {
        return this.versionHolder.isExistVersion(version);
    }

    public List<String> getVersionList() {
        return this.versionHolder.getVersionList();
    }

    public void addVersion(String version) {
        if (this.versionHolder.addVersion(version)) {
            this.remoteMonitor.syncNode(ClusterUtils.VERSION_PATH, this.versionHolder);
        }
    }

    public void removeVersion(Collection<String> collection) {
        this.versionHolder.removeVersion(collection);
        this.remoteMonitor.syncNode(ClusterUtils.VERSION_PATH, this.versionHolder);
    }

    public void notifyZoneChange() {
        this.remoteMonitor.syncNode(ClusterUtils.ZONE_CHENGE_PATH, this.zoneChangeVersion.incrementAndGet() + "");
    }

    @Override
    public void doMonitor() {
        super.doMonitor();
        try {
            this.remoteMonitor.getClient().delete(ClusterUtils.ZONE_CHENGE_PATH, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        this.remoteMonitor.createFullNode(ClusterUtils.ZONE_CHENGE_PATH, "", CreateMode.PERSISTENT, false);
        this.remoteMonitor.createFullNode(ClusterUtils.VERSION_PATH, this.versionHolder, CreateMode.PERSISTENT, false);
        this.remoteMonitor.getNodeData(ClusterUtils.ZONE_CHENGE_PATH, CreateMode.PERSISTENT);
        this.versionHolder = this.remoteMonitor.getNodeData(ClusterUtils.VERSION_PATH, CreateMode.PERSISTENT);
    }

    @Override
    public String getWebServiceNodePath() {
        return this.servletContext.getContextPath();
    }

}
