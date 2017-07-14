package com.tny.game.suite.utils;

import com.tny.game.common.config.Config;
import com.tny.game.common.config.ConfigLib;
import com.tny.game.common.utils.DateTimeAide;
import com.tny.game.suite.core.AppType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    Config SNAP_REASON_CONFIG = ConfigLib.getConfigExist(System.getProperty(SNAP_REASON_SYS_KEY, SNAP_REASON_PATH));

    //region Kafka配置 kafka.properties
    String KAFKA_CONFIG_PATH = "kafka.properties";
    Config KAFKA_CONFIG = ConfigLib.getConfigExist(KAFKA_CONFIG_PATH);
    //endregion

    //region cluster.IP配置 cluster.properties
    String CLUSTER_CONFIG_PATH = "cluster.properties";
    Config CLUSTER_CONFIG = ConfigLib.getConfigExist(CLUSTER_CONFIG_PATH);
    String CLUSTER_KAFKA_PRODUCER_IP_LIST = "tny.kafka.producer.ip_list";
    String CLUSTER_KAFKA_CONSUMER_IP_LIST = "tny.kafka.consumer.ip_list";
    //endregion

    //region 套件配置 suite.properties
    String SUITE_CONFIG_PATH = "suite.properties";
    Config SUITE_CONFIG = ConfigLib.getConfigExist(SUITE_CONFIG_PATH);
    String SUITE_SCAN_PATHS /*                          */ = "tny.server.suite.scan_paths";
    String SUITE_WORD_FILTER_CONFIG_PATH /*             */ = "tny.server.suite.word.filter_path";
    String SUITE_WORD_REPLACE_SYMBOL /*                 */ = "tny.server.suite.word.replace_symbol";
    String SUITE_SERVER_TYPE_CLASS /*                   */ = "tny.server.suite.server.server_type_class";
    String SUITE_SCOPE_TYPE_CLASS /*                    */ = "tny.server.suite.server.scope_type_class";
    String SUITE_SESSION_OFFLINE_WAIT /*                */ = "tny.server.suite.session.offline_wait";
    String SUITE_SESSION_CACHE_RESP_SIZE /*             */ = "tny.server.suite.session.cache_resp_size";
    String SUITE_EXECUTOR_THREAD_SIZE /*                */ = "tny.server.suite.executor.thread_size";
    String SUITE_EXECUTOR_THREAD_MAX_SIZE /*            */ = "tny.server.suite.executor.thread_max_size";
    String SUITE_EXECUTOR_KEEP_ALIVE_TIME /*            */ = "tny.server.suite.executor.keep_alive_time";
    // String SUITE_MSG_CHECKER_DIRECT_PROTS /*            */ = "tny.server.suite.message_checker.direct_ports";
    String SUITE_MSG_CHECKER_RANDOM_SEQ /*              */ = "tny.server.suite.message_checker.random_seq";
    String SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_INC /*      */ = "tny.server.suite.auth.kafka_login.protocols.inc";
    String SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_EXC /*      */ = "tny.server.suite.auth.kafka_login.protocols.exc";
    String SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_INC_RG /*   */ = "tny.server.suite.auth.kafka_login.protocols.inc_rg";
    String SUITE_AUTH_KAFKA_LOGIN_PROTOCOLS_EXC_RG /*   */ = "tny.server.suite.auth.kafka_login.protocols.exc_rg";
    String SUITE_LAUNCHER_PROFILES /*                   */ = "tny.server.suite.launcher.profiles";
    String SUITE_ASYNC_DB_EXE_STEP /*                   */ = "tny.server.suite.async_db.executor.step";
    String SUITE_ASYNC_DB_EXE_WAIT_TIME /*              */ = "tny.server.suite.async_db.executor.wait_time";
    String SUITE_ASYNC_DB_EXE_TRY_TIME /*               */ = "tny.server.suite.async_db.executor.try_time";
    String SUITE_ASYNC_DB_EXE_THREAD_SIZE /*            */ = "tny.server.suite.async_db.executor.thread_size";
    String SUITE_ASYNC_OBJ_POOL_KEEP_TIME /*            */ = "tny.server.suite.async_db.object_pool.keep_time";
    String SUITE_ASYNC_OBJ_POOL_RECYCLE_TIME /*         */ = "tny.server.suite.async_db.object_pool.recycle_time";
    String SUITE_TIME_TASK_PATH /*                      */ = "tny.server.suite.time_task.path";
    String SUITE_CLUSTER_GAME_MONITOR_SERVER_TYPES /*   */ = "tny.server.suite.cluster.game.monitor_server_types ";
    String SUITE_FEATURE_MODEL_CONFIG_PATH /*           */ = "tny.server.suite.base.default_item_model.path";
    String SUITE_BASE_DEFAULT_ITEM_MODEL_PATH /*        */ = "tny.server.suite.base.default_item_model.path";
    String SUITE_BASE_ITEM_TYPE_CLASS /*                */ = "tny.server.suite.base.item_type_class";
    String SUITE_BASE_ABILITY_CLASS /*                  */ = "tny.server.suite.base.ability_class";
    String SUITE_BASE_ACTION_CLASS /*                   */ = "tny.server.suite.base.action_class";
    String SUITE_BASE_BEHAVIOR_CLASS /*                 */ = "tny.server.suite.base.behavior_class";
    String SUITE_BASE_DEMAND_TYPE_CLASS /*              */ = "tny.server.suite.base.demand_type_class";
    String SUITE_BASE_DEMAND_PARAM_CLASS /*             */ = "tny.server.suite.base.demand_param_class";
    String SUITE_BASE_MODULE_CLASS /*                   */ = "tny.server.suite.base.module_class";
    String SUITE_BASE_FEATURE_CLASS /*                  */ = "tny.server.suite.base.feature_class";
    String SUITE_BASE_OPEN_MODE_CLASS /*                */ = "tny.server.suite.base.open_mode_class";

    static List<String> getScanPaths() {
        List<String> paths = new ArrayList<>();
        String pathsWords = Configs.SUITE_CONFIG.getStr(Configs.SUITE_SCAN_PATHS);
        if (pathsWords != null) {
            paths.addAll(Arrays.asList(StringUtils.split(pathsWords, ",")));
            paths.add("com.tny.game.suite");
        }
        return paths;
    }

    static String[] getScanPathArray() {
        List<String> paths = new ArrayList<>();
        String pathsWords = Configs.SUITE_CONFIG.getStr(Configs.SUITE_SCAN_PATHS);
        if (pathsWords != null) {
            paths.addAll(Arrays.asList(StringUtils.split(pathsWords, ",")));
            paths.add("com.tny.game.suite");
        }
        return paths.toArray(new String[paths.size()]);
    }
    //endregion

    //region 服务器配置 service.properties
    String SERVICE_CONFIG_PATH = "service.properties";
    Config SERVICE_CONFIG = ConfigLib.getConfigExist(SERVICE_CONFIG_PATH);
    String SERVER_ID /*                 */ = "tny.server.id";
    String SERVER_SCOPE /*              */ = "tny.server.scope";
    String SERVER_LOCAL /*              */ = "tny.server.local";
    String SERVER_TYPE /*               */ = "tny.server.type";
    String PUBLIC_HOST /*               */ = "tny.server.public_host";
    String PRIVATE_HOST /*              */ = "tny.server.private_host";
    String RMI_PORT /*                  */ = "tny.server.rmi.registryPort";
    String PROJECT_NAME /*              */ = "tny.server.project_name";
    String PROJECT /*                   */ = "tny.server.project";
    String SERVICE_CONFIG_WEB_SERVICE_HOST = "tny.web_service.server.host";
    String SERVICE_CONFIG_WEB_SERVICE_PORT = "tny.web_service.server.port";
    //endregion

    //region 授权认证配置 authz.properties
    String AUTHZ_CONFIG_PATH = "authz.properties";
    Config AUTH_CONFIG = ConfigLib.getConfigExist(Configs.AUTHZ_CONFIG_PATH);
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
    Config MONITOR_CONFIG = ConfigLib.getConfigExist(MONITOR_CONFIG_PATH);
    //endregion

    //region 版本配置 version.properties
    String VERSION_CONFIG_PATH = "version.properties";
    Config VERSION_CONFIG = ConfigLib.getConfigExist(Configs.VERSION_CONFIG_PATH);
    String VERSION_KEY = "tny.server.version";
    String VERSION_NO = "tny.server.version_no";
    //endregion

    //region 开发调试测试参数 develop.properties
    String DEVELOP_CONFIG_PATH = "develop.properties";
    Config DEVELOP_CONFIG = ConfigLib.getConfigExist(Configs.DEVELOP_CONFIG_PATH);

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


    static LocalDate devDate(String key, LocalDate... defaultValue) {
        String crateAt = DEVELOP_CONFIG.getStr(key);
        if (crateAt != null) {
            try {
                return DateTimeAide.DATE_TIME_MIN_FORMAT.parseLocalDate(crateAt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue.length <= 0 ? LocalDate.now() : defaultValue[0];
    }

    static DateTime devDateTime(String key, DateTime... defaultValue) {
        String crateAt = DEVELOP_CONFIG.getStr(key);
        if (crateAt != null) {
            try {
                return DateTimeAide.DATE_TIME_MIN_FORMAT.parseDateTime(crateAt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue.length <= 0 ? DateTime.now() : defaultValue[0];
    }

    static long devDateTime(String key, long defaultValue) {
        String crateAt = DEVELOP_CONFIG.getStr(key);
        if (crateAt != null) {
            try {
                DateTime dateTime = DateTimeAide.DATE_TIME_MIN_FORMAT.parseDateTime(crateAt);
                return dateTime.getMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }
    //endregion



    //region 协议配置
    String PROTOCOLS_CONFIG_PATH = "protocols.properties";
    Config PROTOCOLS_CONFIG = ConfigLib.getConfigExist(PROTOCOLS_CONFIG_PATH);
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