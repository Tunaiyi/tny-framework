package com.tny.game.net.base;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface AppConstants {

    String APP_TYPE = "tny.server.app_type";
    String SCOPE_TYPE = "tny.server.scope";
    String DISPATCHER_EXECUTOR_THREADS = "tny.server.dispatcher.executor.threads";

    String SESSION_CACHE_MESSAGE_SIZE = "tny.server.session.cache_message_size";
    String SESSION_HOLDER_CLEAR_INTERVAL = "tny.server.session_holder.clear_interval";
    String SESSION_HOLDER_SESSION_LIVE = "tny.server.session.holder.session_live";
    String SESSION_HOLDER_SESSION_OFFLINE_SIZE = "tny.server.session.holder.session_offline_size";
    String SESSION_HOLDER_KEEP_IDLE_TIME = "tny.server.session.holder.keep_idle_time";
    String SESSION_EXECUTOR_THREADS = "tny.server.session.executor.threads";

    String TUNNEL_PING_INTERVAL = "tny.server.tunnel.ping_interval";
    String SERVER_BIND_IPS = "tny.server.bind.ips";

    String DEFAULT_USER_GROUP = "USER";
    String UNLOGIN_USER_GROUP = "UNLOGIN";

    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    String SEND_TIMEOUT_URL_PARAM = "send_timeout";
    String LOGING_TIMEOUT_URL_PARAM = "login_timeout";
    String RESEND_TIMES_URL_PARAM = "resend_times";



}
