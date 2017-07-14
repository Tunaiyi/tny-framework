package com.tny.game.suite.core;

import com.thoughtworks.xstream.XStream;
import com.tny.game.common.utils.Logs;
import com.tny.game.base.log.LogName;
import com.tny.game.common.RunningChecker;
import com.tny.game.common.config.ConfigLoader;
import com.tny.game.common.formula.DateTimeEx;
import com.tny.game.common.utils.DateTimeAide;
import com.tny.game.suite.utils.Configs;
import com.tny.game.suite.utils.DateTimeConverter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GameInfo {

    private static GameInfo GAMES_INFO;

    private final static DateTime startAt = DateTime.now();

    protected static ScopeType scopeType;

    protected static String local;

    private static Map<Integer, GameInfo> GAMES_INFO_MAP;

    private DateTime openDate;

    protected int serverID;

    private boolean mainServer = false;

    private boolean register;

    private List<InetConnector> publicConnectors;

    private List<InetConnector> privateConnectors;

    private static Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    static {
        String serverTypeStr = Configs.SERVICE_CONFIG.getStr(Configs.SERVER_SCOPE);
        GameInfo.local = Configs.SERVICE_CONFIG.getStr(Configs.SERVER_LOCAL, "zh-cn");
        GameInfo.scopeType = ScopeTypes.of(serverTypeStr.toUpperCase());
        LOGGER.info("# {} 创建 {} xstream 对象 ", GameInfo.class.getName(), GameInfo.class.getName());
        XStream xStream = new XStream();
        RunningChecker.start(GameInfo.class);
        xStream.autodetectAnnotations(true);
        xStream.alias("servers", ArrayList.class);
        xStream.alias("server", GameInfo.class);
        xStream.alias("connector", InetConnector.class);
        xStream.registerLocalConverter(GameInfo.class, "openDate", new DateTimeConverter(DateTimeAide.DATE_TIME_MIN_FORMAT));
        Map<Integer, GameInfo> map = new HashMap<>();
        try (InputStream inputStream = ConfigLoader.loadInputStream(Configs.GAME_INFO_CONFIG_PATH)) {
            LOGGER.info("#itemModelManager# 解析 <{}> xml ......", GameInfo.class.getName());
            @SuppressWarnings("unchecked")
            List<GameInfo> list = (List<GameInfo>) xStream.fromXML(inputStream);
            for (GameInfo info : list) {
                if (info.isMainServer()) {
                    if (GAMES_INFO != null)
                        throw new IllegalArgumentException(Logs.format("发现服务器 {} 与 {} 同为主服务器", GAMES_INFO.getServerID(), info.getServerID()));
                    GAMES_INFO = info;
                }
                if (map.put(info.getServerID(), info) != null)
                    throw new IllegalArgumentException(Logs.format("发现有重复 serverID {} 的服务器", info.getServerID()));
            }
            if (GAMES_INFO == null)
                throw new IllegalArgumentException(Logs.format("未发现主服务器"));
            LOGGER.info("#itemModelManager# 解析 <{}> xml完成! ", GameInfo.class.getName());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
        GameInfo.GAMES_INFO_MAP = Collections.unmodifiableMap(map);
        LOGGER.info("#itemModelManager# 装载 <{}> model 完成 | 耗时 {} ms", GameInfo.class.getName(), RunningChecker.end(GameInfo.class).cost());
    }

    public static GameInfo getMainInfo() {
        return GAMES_INFO;
    }

    public static GameInfo getInfo(int serverID) {
        return GAMES_INFO_MAP.get(serverID);
    }

    public static boolean isLocal(String local) {
        return GameInfo.local.toLowerCase().equals(local.toLowerCase());
    }

    public static String getLocal() {
        return GameInfo.local;
    }

    public static int getMainServerID() {
        return getMainInfo().getServerID();
    }

    public static boolean isHasServer(int serverID) {
        return GAMES_INFO_MAP.containsKey(serverID);
    }


    public GameInfo() {
    }

    public static Collection<GameInfo> getAllGamesInfo() {
        return GAMES_INFO_MAP.values();
    }

    public int getServerID() {
        return this.serverID;
    }

    public AppType getServerType() {
        return GameInfo.scopeType.getAppType();
    }

    public ScopeType getScopeType() {
        return GameInfo.scopeType;
    }

    public boolean isType(AppType serverType) {
        return GameInfo.scopeType == serverType;
    }

    public boolean isScope(ScopeType scopeType) {
        return GameInfo.scopeType == scopeType;
    }

    public DateTime getOpenDate() {
        return this.openDate;
    }

    public int getOpenedDays() {
        return DateTimeEx.days(this.getOpenDate().toLocalDate(), DateTimeEx.today());
    }

    public List<InetConnector> getPublicConnectors() {
        if (publicConnectors == null)
            return new ArrayList<>();
        return new ArrayList<>(publicConnectors);
    }

    public List<InetConnector> getPrivateConnectors() {
        if (privateConnectors == null)
            return new ArrayList<>();
        return new ArrayList<>(privateConnectors);
    }

    public Optional<InetConnector> getPublicConnector(String connectorID) {
        return this.getPublicConnectors().stream()
                .filter(c -> c.getId().equals(connectorID))
                .findFirst();
    }

    public Optional<InetConnector> getPrivateConnector(String connectorID) {
        return this.getPrivateConnectors().stream()
                .filter(c -> c.getId().equals(connectorID))
                .findFirst();
    }

    public DateTime getStartAt() {
        return GameInfo.startAt;
    }

    public String getVersion() {
        return Configs.VERSION_CONFIG.getStr(Configs.VERSION_KEY);
    }

    public int getVersionNumber() {
        return Configs.VERSION_CONFIG.getInt(Configs.VERSION_NO);
    }

    public boolean isMainServer() {
        return this.mainServer;
    }

    public boolean isRegister() {
        return this.register;
    }

    public DateTime openDate() {
        return this.openDate;
    }

    public DateTime openDateMillis(int millisOfDay) {
        return this.openDate.withMillisOfDay(millisOfDay);
    }

    public DateTime openDate(int hour) {
        return openDate(hour, 0);
    }

    public DateTime openDate(int hour, int minutes) {
        return openDate(hour, minutes, 0);
    }

    public DateTime openDate(int hour, int minutes, int seconds) {
        return this.openDate.withTime(hour, minutes, seconds, 0);
    }

}
