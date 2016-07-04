package com.tny.game.suite.cluster;


import com.tny.game.LogUtils;
import com.tny.game.zookeeper.NodeDataFormatter;

import static com.tny.game.suite.utils.Configs.*;

public class ClusterUtils {

    public static final byte[] EMPTY_BYTES = new byte[0];

    public static final String IP_LIST = "tny.keeper.zookeeper_ip_list";
    private static final String GAMES_OUTLINE_LIST = "tny.server.monitor.games.outline_list";
    private static final String GAMES_LAUNCH_LIST = "tny.server.monitor.games.launch_list";
    private static final String GAMES_SETTING_LIST = "tny.server.monitor.games.setting_list";
    private static final String VERSION_LIST = "tny.client.version_list";
    private static final String ZONE_CHANGE = "tny.ws.admin.zone_change";


    public static final String OUTLINE_LIST_PATH = MONITOR_CONFIG.getStr(ClusterUtils.GAMES_OUTLINE_LIST);
    public static final String GAMES_LAUNCH_PATH = MONITOR_CONFIG.getStr(ClusterUtils.GAMES_LAUNCH_LIST);
    public static final String SETTING_LIST_PATH = MONITOR_CONFIG.getStr(ClusterUtils.GAMES_SETTING_LIST);
    public static final String ZONE_CHENGE_PATH = MONITOR_CONFIG.getStr(ClusterUtils.ZONE_CHANGE);
    public static final String VERSION_PATH = MONITOR_CONFIG.getStr(ClusterUtils.VERSION_LIST);

    public static final NodeDataFormatter PROTO_FORMATTER = new ProtoNodeDataFormatter();

    public static String getServerOutlinePath(int serverID) {
        return OUTLINE_LIST_PATH + "/" + serverID;
    }

    public static String getServerSettingPath(int serverID) {
        return SETTING_LIST_PATH + "/" + serverID;
    }

    public static String getServerLaunchPath(int serverID) {
        return GAMES_LAUNCH_PATH + "/" + serverID;
    }

    public static String getWebNodePath(String serverType, int serverID) {
        String path = getWebNodesPath(serverType);
        if (path == null)
            throw new NullPointerException(LogUtils.format("{} web 服务没有zookeeper节点路径", serverType));
        return path + "/" + serverID;
    }

    public static String getWebNodesPath(String serverType) {
        String path = MONITOR_CONFIG.getStr("tny.server.monitor.ws." + serverType.toLowerCase() + ".list");
        if (path == null)
            throw new NullPointerException(LogUtils.format("{} web 服务没有zookeeper节点路径", serverType));
        return path;
    }

    public static final NodeDataFormatter STRING_FORMATTER = new NodeDataFormatter() {

        @Override
        public byte[] data2Bytes(Object data) {
            return data.toString().getBytes();
        }

        @Override
        @SuppressWarnings("unchecked")
        public String bytes2Data(byte[] bytes) {
            return new String(bytes);
        }

    };

    public static final NodeDataFormatter NO_FORMATTER = new NodeDataFormatter() {

        @Override
        public byte[] data2Bytes(Object data) {
            return (byte[]) data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D bytes2Data(byte[] bytes) {
            return (D) bytes;
        }
    };

}
