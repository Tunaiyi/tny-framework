package com.tny.game.net.utils;

import com.tny.game.common.config.*;

import java.util.concurrent.TimeUnit;

/*
 * Created by Kun Yang on 2017/3/26.*/
public interface NetConfigs {

    String NULL = null;

    String NET_CONFIG_PATH = "service.properties";
    Config NET_CONFIG = ConfigLib.getExistConfig(NET_CONFIG_PATH);

    /* App 类型*/
    String SERVER_APP_TYPE = "tny.net.server.type";
    /* App作用域*/
    String SERVER_SCOPE_TYPE = "tny.net.server.scope";

    String BASE_APP_TYPE_CLASS = "tny.net.base.app_type_class";
    String BASE_SCOPE_TYPE_CLASS = "tny.net.base.scope_type_class";


    /* SessionKeeper 清除失效Session时间间隙*/
    String SESSION_KEEPER_CLEAR_INTERVAL = "tny.net.session.keeper.clear_interval";
    /* SessionKeeper 默认清除失效Session时间间隙 (1分钟)*/
    long SESSION_KEEPER_CLEAR_INTERVAL_DEFAULT_VALUE = TimeUnit.MINUTES.toMillis(1);
    /* SessionKeeper Session 离线之后的生命周期*/
    String SESSION_KEEPER_OFFLINE_CLOSE_DELAY = "tny.net.session.keeper.offline_close_delay";
    /* SessionKeeper 默认Session 离线之后的生命周期(10分钟)*/
    long SESSION_KEEPER_OFFLINE_CLOSE_DELAY_DEFAULT_VALUE = TimeUnit.MINUTES.toMillis(10);
    /* SessionKeeper 离线Session队列数量*/
    String SESSION_KEEPER_OFFLINE_MAX_SIZE = "tny.net.session.keeper.offline_max_size";
    /* SessionKeeper 默认离线Session队列数量(1024)*/
    int SESSION_KEEPER_OFFLINE_MAX_SIZE_DEFAULT_VALUE = 1024;
    /* SessionKeeper sessionFactory Unit name*/
    String SESSION_FACTORY_UNIT = "tny.net.session.keeper.session_factory_unit";

    /* Session 缓存发送message数量*/
    String SESSION_CACHE_MESSAGE_SIZE = "tny.net.session.cache_message_size";
    /* Session 缓存发送message默认值 (0)*/
    int SESSION_CACHE_MESSAGE_SIZE_DEFAULT_VALUE = 0;
    /* Session  接受 Tunnel 最大数量*/
    String SESSION_MAX_TUNNEL_SIZE = "tny.net.session.max_tunnel_size";
    /* Session 接受 Tunnel 最大数量默认值 (1)*/
    int SESSION_MAX_TUNNEL_SIZE_DEFAULT_VALUE = 1;

    /* Session 最大空闲时间(超过自动断线)*/
    String SESSION_KEEPER_KEEP_IDLE_TIME = "tny.net.session.keeper.keep_idle_time";
    /* Session 默认最大空闲时间(超过自动断线) (3分钟)*/
    long SESSION_KEEPER_DEFAULT_KEEP_IDLE_TIME = TimeUnit.MINUTES.toMillis(3);
    /* SessionEventExecutor 处理Session事件的线程数*/
    String SESSION_EXECUTOR_THREADS = "tny.net.session.event_executor.threads";
    /* SessionEventExecutor 默认处理Session事件的线程数*/
    int SESSION_EXECUTOR_DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    /* MessageDispatchCommandExecutor 运行时间*/
    String DISPATCHER_EXECUTOR_THREADS = "tny.net.dispatcher.executor.threads";



    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    long CONNECT_TIMEOUT_DEFAULT_VALUE = 5000L;


    String CONNECTION_SIZE_URL_PARAM = "connection_size";
    int CONNECTION_SIZE_DEFAULT_VALUE = 1;
    String INIT_CONNECT_ASYN_URL_PARAM = "init_connect_asyn";
    boolean INIT_CONNECT_ASYN_DEFAULT_VALUE = false;

    String SEND_TIMEOUT_URL_PARAM = "send_timeout";
    String LOGIN_TIMEOUT_URL_PARAM = "login_timeout";
    String RESEND_TIMES_URL_PARAM = "resend_times";


}
