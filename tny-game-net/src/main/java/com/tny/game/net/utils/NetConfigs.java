package com.tny.game.net.utils;

/*
 * Created by Kun Yang on 2017/3/26.*/
public interface NetConfigs {

    String CONNECT_TIMEOUT_URL_PARAM = "connect_timeout";
    long CONNECT_TIMEOUT_DEFAULT_VALUE = 5000L;

    String CONNECT_ASYNC_URL_PARAM = "connect_async";
    boolean CONNECT_ASYNC_DEFAULT_VALUE = false;

    String RETRY_TIMES_URL_PARAM = "retry";
    int RETRY_TIMES_DEFAULT_VALUE = 3;

    String RETRY_INTERVAL_URL_PARAM = "retry_interval";
    long RETRY_INTERVAL_DEFAULT_VALUE = 3000L;

}
