package com.tny.game.suite.utils;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.io.config.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.util.*;

/**
 * Created by Kun Yang on 16/1/27.
 */
public interface Configs {

    String GAME_INFO_CONFIG_PATH = "game_info.xml";
    String TIME_TASK_MODEL_CONFIG_PATH = "config/TimeTaskModel.xml";
    String DEFAULT_ITEM_MODEL_CONFIG_PATH = "config/base/DefaultItemModel.xml";
    String FEATURE_MODEL_CONFIG_PATH = "config/base/FeatureModel.xml";
    String WORD_FILTER_CONFIG_PATH = "words.txt";

    String SNAP_REASON_PATH = "snap_reason.properties";
    String SNAP_REASON_SYS_KEY = "tny.oplog.reason.path";
    Config SNAP_REASON_CONFIG = ConfigLib.getConfig(System.getProperty(SNAP_REASON_SYS_KEY, SNAP_REASON_PATH));

    //region 套件配置 suite.properties
    String SUITE_CONFIG_PATH = "suite.properties";
    Config SUITE_CONFIG = ConfigLib.getConfig(SUITE_CONFIG_PATH);
    String SUITE_SCAN_PATHS /*                          */ = "tny.app.scan_paths";

    String SUITE_LAUNCHER_PROFILES /*                   */ = "tny.app.launcher.profiles";
    String SUITE_ASYNC_DB_EXE_STEP /*                   */ = "tny.app.async_db.executor.step";
    String SUITE_ASYNC_DB_EXE_WAIT_TIME /*              */ = "tny.app.async_db.executor.wait_time";
    String SUITE_ASYNC_DB_EXE_TRY_TIME /*               */ = "tny.app.async_db.executor.try_time";
    String SUITE_ASYNC_DB_EXE_THREAD_SIZE /*            */ = "tny.app.async_db.executor.thread_size";
    String SUITE_ASYNC_OBJ_POOL_KEEP_TIME /*            */ = "tny.app.async_db.object_pool.keep_time";
    String SUITE_ASYNC_OBJ_POOL_RECYCLE_TIME /*         */ = "tny.app.async_db.object_pool.recycle_time";
    String SUITE_TIME_TASK_PATH /*                      */ = "tny.app.time_task.path";
    String SUITE_FEATURE_MODEL_CONFIG_PATH /*           */ = "tny.app.base.default_item_model.path";
    String SUITE_BASE_DEFAULT_ITEM_MODEL_PATH /*        */ = "tny.app.base.default_item_model.path";

    static Collection<String> getProfiles() {
        String profiles = Configs.SUITE_CONFIG.getString(Configs.SUITE_LAUNCHER_PROFILES);
        return ImmutableSet.copyOf(StringUtils.split(profiles, ","));
    }

    static List<String> getScanPathList() {
        List<String> paths = new ArrayList<>();
        String pathsWords = Configs.SUITE_CONFIG.getString(Configs.SUITE_SCAN_PATHS);
        if (pathsWords != null) {
            paths.addAll(Arrays.asList(StringUtils.split(pathsWords, ",")));
            paths.add("com.tny.game.suite");
        }
        return paths;
    }

    static String[] getScanPathArray() {
        List<String> paths = getScanPathList();
        return paths.toArray(new String[0]);
    }
    //endregion

    ConfigFormatter URL_FORMATTER = new ConfigFormatter() {

        @Override
        public boolean isKey(String key) {
            return key.equals(SERVER_URL) || key.startsWith(SERVER_URL + ".");
        }

        @Override
        public Object formatObject(String value) {
            return URL.valueOf(value);
        }

    };

    Config SERVICE_CONFIG = ConfigLib.getConfig("service.properties");

    //region 服务器配置 service.properties
    String SERVER_ID /*                     */ = "tny.net.server.id";
    String SERVER_LOCAL /*                  */ = "tny.net.server.local";
    String SERVER_URL /*                    */ = "tny.net.server.url";
    //endregion

    //region 授权认证配置 authz.properties
    String AUTHZ_CONFIG_PATH = "authz.properties";
    Config AUTH_CONFIG = ConfigLib.getConfig(Configs.AUTHZ_CONFIG_PATH);
    String AUTH_GAME_TICKET_PUBLIC_KEY /*       */ = "tny.server.auth.game.ticket.pub_key";
    String AUTH_GAME_TICKET_PRIVATE_KEY /*      */ = "tny.server.auth.game.ticket.pri_key";
    String AUTH_PF_TOKEN_KEY /*                 */ = "tny.server.auth.game.pf_token_key";
    String AUTH_GAMES_TICKET_KEY /*             */ = "tny.server.auth.game.game_ticket_key";
    String AUTH_CLIENT_MESSAGE_KEY /*           */ = "tny.server.auth.game.client.message_key";
    String AUTH_SERVER_HEAD /*                  */ = "tny.server.auth.";
    String AUTH_PASSWORD_KEY /*                 */ = "password";
    String AUTH_PAY_BILL_KEY /*                 */ = "tny.server.auth.sdker.pay_bill_key";

    static String createAuthKey(String serverType) {
        return AUTH_SERVER_HEAD + serverType + "." + AUTH_PASSWORD_KEY;
    }

    static String createAuthKey(AppType serverType) {
        return AUTH_SERVER_HEAD + serverType.getName() + "." + AUTH_PASSWORD_KEY;
    }
    //endregion

    //region 服务器监控 monitor.properties
    String MONITOR_CONFIG_PATH = "monitor.properties";
    Config MONITOR_CONFIG = ConfigLib.getConfig(MONITOR_CONFIG_PATH);
    //endregion

    //region 版本配置 version.properties
    String VERSION_CONFIG_PATH = "version.properties";
    Config VERSION_CONFIG = ConfigLib.getConfig(Configs.VERSION_CONFIG_PATH);
    String VERSION_KEY = "tny.server.version";
    String VERSION_FEATURE_VERSION = "tny.server.feature_version";
    String VERSION_NO = "tny.server.version_no";
    //endregion

    //region 开发调试测试参数 develop.properties
    String DEVELOP_CONFIG_PATH = "develop.properties";
    Config DEVELOP_CONFIG = ConfigLib.getConfig(Configs.DEVELOP_CONFIG_PATH);

    String DEVELOP_AUTH_CHECK /*            */ = "tny.server.dev.auth.check";
    String DEVELOP_AUTH_OFFLINE_AT /*       */ = "tny.server.dev.auth.offline_at";
    String DEVELOP_AUTH_ONLINE_AT /*        */ = "tny.server.dev.auth.online_at";
    String DEVELOP_AUTH_CREATE_AT /*        */ = "tny.server.dev.auth.create_at";
    String DEVELOP_AUTH_CREATE_ROLE_AT /*   */ = "tny.server.dev.auth.create_role_at";
    String DEVELOP_AUTH_SERVER_ID /*        */ = "tny.server.dev.auth.server_id";
    String DEVELOP_AUTH_ZONE_ID /*          */ = "tny.server.dev.auth.zone_id";
    String DEVELOP_AUTH_ENTRY_ID /*         */ = "tny.server.dev.auth.entry_id";
    String DEVELOP_AUTH_PF /*               */ = "tny.server.dev.auth.pf";
    String DEVELOP_VERIFY_CHECK /*          */ = "tny.server.dev.verify.check";
    String DEVELOP_PAY_PRICE /*             */ = "tny.server.dev.pay.pay_price";
    String DEVELOP_MODULE_TIME_CONSUMING /* */ = "tny.server.dev.module.time_consuming";
    String DEVELOP_FEATURE_VERSION /*       */ = "tny.server.dev.feature_version";

    static LocalDate devDate(String key, LocalDate... defaultValue) {
        String crateAt = DEVELOP_CONFIG.getString(key);
        if (crateAt != null) {
            try {
                return LocalDate.from(DateTimeAide.DATE_TIME_MIN_FORMAT.parse(crateAt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue.length <= 0 ? LocalDate.now() : defaultValue[0];
    }

    static Instant devDateTime(String key, Instant... defaultValue) {
        String crateAt = DEVELOP_CONFIG.getString(key);
        if (crateAt != null) {
            try {
                return Instant.from(DateTimeAide.DATE_TIME_MIN_FORMAT.parse(crateAt));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue.length <= 0 ? Instant.now() : defaultValue[0];
    }

    static long devDateTime(String key, long defaultValue) {
        String crateAt = DEVELOP_CONFIG.getString(key);
        if (crateAt != null) {
            try {
                return Instant.from(DateTimeAide.DATE_TIME_MIN_FORMAT.parse(crateAt)).toEpochMilli();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }
    //endregion

    //region 协议配置
    String PROTOCOLS_CONFIG_PATH = "protocols.properties";
    Config PROTOCOLS_CONFIG = ConfigLib.getConfig(PROTOCOLS_CONFIG_PATH);
    String PATH_HEAD = "tny.server.url.path";
    //endregion

    //    static Function<String, String> keyAs(String nextChild) {
    //        if (nextChild.endsWith(".")) {
    //            String head = PREFIX + nextChild;
    //            return (child) -> head + child;
    //        } else {
    //            return key(nextChild + ".")
    //        }
    //    }
}