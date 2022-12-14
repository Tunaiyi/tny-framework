package com.tny.game.suite.cluster;

import com.tny.game.zookeeper.*;

import java.util.Map;
import java.util.regex.Pattern;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.suite.utils.Configs.*;

public class ClusterUtils {

    public static final byte[] EMPTY_BYTES = new byte[0];

    public static final String IP_LIST = "tny.keeper.zookeeper_ip_list";

    private static final String GAMES_OUTLINE_LIST = "tny.server.monitor.games.outline_list";

    private static final String GAMES_LAUNCH_LIST = "tny.server.monitor.games.launch_list";

    private static final String GAMES_SETTING_LIST = "tny.server.monitor.games.setting_list";

    private static final String CLIENT_VERSION_CHANGE = "tny.server.monitor.doors.client_version_change";

    private static final String ZONE_CHANGE = "tny.server.monitor.doors.zone_change";

    public static final String OUTLINE_LIST_PATH = MONITOR_CONFIG.getString(ClusterUtils.GAMES_OUTLINE_LIST);

    public static final String GAMES_LAUNCH_PATH = MONITOR_CONFIG.getString(ClusterUtils.GAMES_LAUNCH_LIST);

    public static final String SETTING_LIST_PATH = MONITOR_CONFIG.getString(ClusterUtils.GAMES_SETTING_LIST);

    public static final String VERSION_PATH = MONITOR_CONFIG.getString(ClusterUtils.CLIENT_VERSION_CHANGE);

    public static final String ZONE_CHANGE_PATH = MONITOR_CONFIG.getString(ClusterUtils.ZONE_CHANGE);

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
        return path + "/" + serverID;
    }

    public static String getWebNodesPath(String serverType) {
        String path = MONITOR_CONFIG.getString("tny.server.monitor.ws." + serverType.toLowerCase() + ".list");
        if (path == null) {
            throw new NullPointerException(format("{} web ????????????zookeeper????????????", serverType));
        }
        return path;
    }

    public final static Pattern WS_NOTES_PATH_REGEX = Pattern.compile("tny\\.server\\.monitor\\.ws\\.([1-9A-Za-z_]+)\\.list");

    public static Map<String, String> getAllWebNodesPaths() {
        return MONITOR_CONFIG.find(WS_NOTES_PATH_REGEX);
    }

    public static final NodeDataFormatter STRING_FORMATTER = new NodeDataFormatter() {

        @Override
        public byte[] data2Bytes(Object data) {
            return data.toString().getBytes();
        }

        @Override
        public <T> T bytes2Data(byte[] bytes) {
            return as(new String(bytes));
        }

    };

    public static final NodeDataFormatter NO_FORMATTER = new NodeDataFormatter() {

        @Override
        public byte[] data2Bytes(Object data) {
            if (data instanceof byte[]) {
                return (byte[])data;
            }
            throw new ClassCastException(data + " is not byte[] class");
        }

        @Override
        public <D> D bytes2Data(byte[] bytes) {
            return as(bytes);
        }
    };

}
