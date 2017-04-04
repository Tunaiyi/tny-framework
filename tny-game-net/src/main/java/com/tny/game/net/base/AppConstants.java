package com.tny.game.net.base;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public interface AppConstants {

    String APP_TYPE = "tny.server.app_type";
    String SCOPE_TYPE = "tny.server.scope_type";
    String DISPATCHER_EXECUTOR_THREADS = "tny.server.dispatcher.executor.threads";
    String SESSION_HOLDER_CLEAR_INTERVAL = "tny.server.session_holder.clear_interval";
    String SESSION_HOLDER_SESSION_LIVE = "tny.server.session_holder.session_live";
    String SESSION_CACHE_MESSAGE_SIZE = "tny.server.session.cache_message_size";
    String SESSION_EXECUTOR_THREADS = "tny.server.session.executor.threads";
    String SERVER_BIND_IPS = "tny.server.bind.ips";

    String DEFAULT_USER_GROUP = "USER";
    String UNLOGIN_USER_GROUP = "UNLOGIN";


}
