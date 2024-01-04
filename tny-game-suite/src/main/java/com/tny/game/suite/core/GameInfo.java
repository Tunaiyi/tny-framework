package com.tny.game.suite.core;

import com.thoughtworks.xstream.XStream;
import com.tny.game.basics.log.*;
import com.tny.game.common.io.config.*;
import com.tny.game.common.runtime.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.suite.utils.*;
import org.slf4j.*;

import java.io.*;
import java.time.*;
import java.time.temporal.ChronoField;
import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

public class GameInfo {

    private static GameInfo GAMES_INFO;

    private final static Instant startAt = Instant.now();

    private static Map<Integer, GameInfo> GAMES_INFO_MAP;

    private int serverId;

    private int zoneId;

    private boolean register;

    private boolean mainServer = false;

    private Instant openDate;

    private AppType appType;

    private ScopeType scopeType;

    private List<InetConnector> publicConnectors;

    private List<InetConnector> privateConnectors;

    private static Logger LOGGER = LoggerFactory.getLogger(LogName.ITEM_MANAGER);

    static {
        LOGGER.info("# {} 创建 {} xstream 对象 ", GameInfo.class.getName(), GameInfo.class.getName());
        XStream xStream = new XStream();
        RunChecker.trace(GameInfo.class);
        xStream.autodetectAnnotations(true);
        xStream.alias("servers", ArrayList.class);
        xStream.alias("server", GameInfo.class);
        xStream.alias("connector", InetConnector.class);
        xStream.registerLocalConverter(GameInfo.class, "openDate", new DateTimeConverter(DateTimeAide.DATE_TIME_MIN_FORMAT));
        Map<Integer, GameInfo> map = new HashMap<>();
        try (InputStream inputStream = FileIOAide.openInputStream(Configs.GAME_INFO_CONFIG_PATH)) {
            LOGGER.info("#itemModelManager# 解析 <{}> xml ......", GameInfo.class.getName());
            @SuppressWarnings("unchecked")
            List<GameInfo> list = (List<GameInfo>) xStream.fromXML(inputStream);
            for (GameInfo info : list) {
                if (info.isMainServer()) {
                    if (GAMES_INFO != null) {
                        throw new IllegalArgumentException(format("发现服务器 {} 与 {} 同为主服务器", GAMES_INFO.getZoneId(), info.getZoneId()));
                    }
                    GAMES_INFO = info;
                }
                if (map.put(info.getZoneId(), info) != null) {
                    throw new IllegalArgumentException(format("发现有重复 serverId {} 的服务器", info.getZoneId()));
                }
            }
            if (GAMES_INFO == null) {
                throw new IllegalArgumentException(format("未发现主服务器"));
            }
            LOGGER.info("#itemModelManager# 解析 <{}> xml完成! ", GameInfo.class.getName());
        } catch (IOException e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
        GameInfo.GAMES_INFO_MAP = Collections.unmodifiableMap(map);
        LOGGER.info("#itemModelManager# 装载 <{}> model 完成 | 耗时 {} ms", GameInfo.class.getName(),
                RunChecker.end(GameInfo.class).costMillisTime());
    }

    public static GameInfo info() {
        return GAMES_INFO;
    }

    public static GameInfo getInfo(int zoneId) {
        return GAMES_INFO_MAP.get(zoneId);
    }

    public static boolean isLocale(String local) {
        return getLocale().equals(local.toLowerCase());
    }

    public static String getLocale() {
        return NetAppContextHolder.getContext().getLocale();
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

    public AppType getAppType() {
        if (this.appType != null) {
            return this.appType;
        }
        return this.appType = AppTypes.of(NetAppContextHolder.getContext().getAppType());
    }

    public ScopeType getScopeType() {
        if (this.scopeType != null) {
            return this.scopeType;
        }
        return this.scopeType = ScopeTypes.of(NetAppContextHolder.getContext().getScopeType());
    }

    public boolean isType(AppType appType) {
        return getAppType() == appType;
    }

    public boolean isScope(ScopeType scopeType) {
        return getScopeType() == scopeType;
    }

    public Instant getOpenDate() {
        return this.openDate;
    }

    public int getOpenedDays() {
        return Period.between(LocalDate.from(this.getOpenDate()), DateTimeAide.today()).getDays();
    }

    public List<InetConnector> getPublicConnectors() {
        if (this.publicConnectors == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.publicConnectors);
    }

    public List<InetConnector> getPrivateConnectors() {
        if (this.privateConnectors == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(this.privateConnectors);
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

    public Instant getStartAt() {
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

    public Instant openDate() {
        return this.openDate;
    }

    public Instant openDateMillis(int millisOfDay) {
        return this.openDate.with(ChronoField.MILLI_OF_DAY, millisOfDay);
    }

    public Instant openDate(int hour) {
        return openDate(hour, 0);
    }

    public Instant openDate(int hour, int minutes) {
        return openDate(hour, minutes, 0);
    }

    public Instant openDate(int hour, int minutes, int seconds) {
        return this.openDate.with(ChronoField.HOUR_OF_DAY, hour)
                .with(ChronoField.MINUTE_OF_HOUR, minutes)
                .with(ChronoField.SECOND_OF_MINUTE, seconds)
                .with(ChronoField.NANO_OF_SECOND, 0);
    }

}
