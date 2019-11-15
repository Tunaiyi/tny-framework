package com.tny.game.suite.core;

import com.thoughtworks.xstream.XStream;
import com.tny.game.base.log.*;
import com.tny.game.common.config.*;
import com.tny.game.common.formula.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.utils.*;
import com.tny.game.suite.utils.*;
import org.joda.time.DateTime;
import org.slf4j.*;

import java.io.*;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class GameInfo {

    private static GameInfo GAMES_INFO;

    private final static DateTime startAt = DateTime.now();

    private static ScopeType scopeType;

    private static String local;

    private static Map<Integer, GameInfo> GAMES_INFO_MAP;

    private int serverId;

    private int zoneId;

    private boolean register;

    private boolean mainServer = false;

    private DateTime openDate;

    private List<InetConnector> publicConnectors;

    private List<InetConnector> privateConnectors;

    private static Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    static {
        String scopeStr = NetConfigs.NET_CONFIG.getString(Configs.SERVER_SCOPE);
        GameInfo.local = NetConfigs.NET_CONFIG.getStr(Configs.SERVER_LOCAL, "zh-cn");
        GameInfo.scopeType = ScopeTypes.of(scopeStr.toUpperCase());
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
                        throw new IllegalArgumentException(format("发现服务器 {} 与 {} 同为主服务器", GAMES_INFO.getZoneId(), info.getZoneId()));
                    GAMES_INFO = info;
                }
                if (map.put(info.getZoneId(), info) != null)
                    throw new IllegalArgumentException(format("发现有重复 serverId {} 的服务器", info.getZoneId()));
            }
            if (GAMES_INFO == null)
                throw new IllegalArgumentException(format("未发现主服务器"));
            LOGGER.info("#itemModelManager# 解析 <{}> xml完成! ", GameInfo.class.getName());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
        GameInfo.GAMES_INFO_MAP = Collections.unmodifiableMap(map);
        LOGGER.info("#itemModelManager# 装载 <{}> model 完成 | 耗时 {} ms", GameInfo.class.getName(), RunningChecker.end(GameInfo.class).cost());
    }

    public static GameInfo info() {
        return GAMES_INFO;
    }

    public static GameInfo getInfo(int zoneId) {
        return GAMES_INFO_MAP.get(zoneId);
    }

    public static boolean isLocal(String local) {
        return GameInfo.local.toLowerCase().equals(local.toLowerCase());
    }

    public static String getLocal() {
        return GameInfo.local;
    }

    public static int getMainZoneId() {
        return info().getZoneId();
    }

    public static boolean isHasServer(int zoneId) {
        return GAMES_INFO_MAP.containsKey(zoneId);
    }


    public GameInfo() {
    }

    public static Collection<GameInfo> getAllGamesInfo() {
        return GAMES_INFO_MAP.values();
    }

    public int getZoneId() {
        return this.zoneId;
    }

    public int getServerId() {
        return this.serverId;
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
        return Configs.VERSION_CONFIG.getString(Configs.VERSION_KEY);
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
