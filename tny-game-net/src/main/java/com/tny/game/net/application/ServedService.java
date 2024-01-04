package com.tny.game.net.application;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/28 10:04
 **/
public interface ServedService extends NetService {

    /**
     * @return 服务发现名
     */
    String getServeName();

}
