package com.tny.game.net.utils;

import com.tny.game.common.config.Config;
import com.tny.game.common.config.ConfigLib;

import java.util.concurrent.TimeUnit;

/*
 * Created by Kun Yang on 2017/3/26.*/
public interface NetConfigs {

    String NET_CONFIG_PATH = "service.properties";
    Config NET_CONFIG = ConfigLib.getExistConfig(NET_CONFIG_PATH);

    /* App 类型*/
    String SERVER_APP_TYPE = "tny.net.server.type";
    /* App作用域*/
    String SERVER_SCOPE_TYPE = "tny.net.server.scope";
    /* 绑定IP*/
    String SERVER_BIND_IPS = "tny.net.server.bind.ips";

    String BASE_APP_TYPE_CLASS = "tny.net.base.app_type_class";
    String BASE_SCOPE_TYPE_CLASS = "tny.net.base.scope_type_class";


    /* Session 缓存message数量*/
    String SESSION_CACHE_MESSAGE_SIZE = "tny.net.session.cache_message_size";
    /* SessionHolder 清除失效Session时间间隙*/
    String SESSION_HOLDER_CLEAR_INTERVAL = "tny.net.session_holder.clear_interval";
    /* SessionHolder 默认清除失效Session时间间隙 (1分钟)*/
    long SESSION_HOLDER_DEFAULT_CLEAR_INTERVAL = TimeUnit.MINUTES.toMillis(1);
    /* SessionHolder Session 离线之后的生命周期*/
    String SESSION_HOLDER_OFFLINE_SESSION_LIFE_TIME = "tny.net.session.holder.offline_session_life_time";
    /* SessionHolder 默认Session 离线之后的生命周期(10分钟)*/
    long SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_LIFE_TIME = TimeUnit.MINUTES.toMillis(10);
    /* SessionHolder 离线Session队列数量*/
    String SESSION_HOLDER_OFFLINE_SESSION_MAX_SIZE = "tny.net.session.holder.offline_session_max_size";
    /* SessionHolder 默认离线Session队列数量(1024)*/
    int SESSION_HOLDER_DEFAULT_OFFLINE_SESSION_MAX_SIZE = 1024;
    /* Session 最大空闲时间(超过自动断线)*/
    String SESSION_HOLDER_KEEP_IDLE_TIME = "tny.net.session.holder.keep_idle_time";
    /* Session 默认最大空闲时间(超过自动断线) (3分钟)*/
    long SESSION_HOLDER_DEFAULT_KEEP_IDLE_TIME = TimeUnit.MINUTES.toMillis(3);
    /* SessionEventExecutor 处理Session事件的线程数*/
    String SESSION_EXECUTOR_THREADS = "tny.net.session.event_executor.threads";
    /* SessionEventExecutor 默认处理Session事件的线程数*/
    int SESSION_EXECUTOR_DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    /* MessageDispatchCommandExecutor 运行时间*/
    String DISPATCHER_EXECUTOR_THREADS = "tny.net.dispatcher.executor.threads";

    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    String SEND_TIMEOUT_URL_PARAM = "send_timeout";
    String LOGIN_TIMEOUT_URL_PARAM = "login_timeout";
    String RESEND_TIMES_URL_PARAM = "resend_times";


}
